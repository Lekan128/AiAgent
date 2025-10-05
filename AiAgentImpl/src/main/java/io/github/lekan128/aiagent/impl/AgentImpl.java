package io.github.lekan128.aiagent.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lekan128.aiagent.impl.method.MethodExecutionResult;
import io.github.lekan128.aiagent.api.Agent;
import io.github.lekan128.aiagent.api.ObjectMapperSingleton;
import io.github.lekan128.aiagent.api.llm.LLM;
import io.github.lekan128.aiagent.impl.method.caller.ReflectionCaller;
import io.github.lekan128.aiagent.impl.method.caller.ReflectionInvocableMethod;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Concrete, internal implementation of the {@link Agent} interface.
 *
 * <p>This class contains the core business logic for the AI Agent workflow, including
 * prompt generation, orchestrating tool calling (reflection and execution), and
 * final response generation.</p>
 *
 * <p><strong>Internal API:</strong> This class is strictly for internal library use
 * and is not intended for external consumption. Its methods and structure are subject
 * to change without notice. Users must obtain an instance via {@code AgentProvider}.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 * @see Agent
 * see AgentProvider
 */
class AgentImpl implements Agent {

    /**
     * Executes the main AI Agent workflow, coordinating the user query, LLM calls, and structured response generation.
     *
     * <p>The workflow proceeds in three phases:
     * <ol>
     * <li>**Planning:** Calls the LLM (via an assumed internal method {@code callWithToolsForPlan})
     * to determine if any tools are needed, resulting in a list of method execution requests.</li>
     * <li>**Execution:** Executes the planned tool calls sequentially using {@link ReflectionCaller#executePipeline(List)},
     * collecting the results.</li>
     * <li>**Final Response:** Calls the LLM again (via an assumed internal method {@code callForFinalResponse}),
     * providing the tool results and original query, to generate the final response and map it to the
     * specified {@code responseClass}.</li>
     * </ol></p>
     *
     * @param <T> The target type to which the LLM's final response should be mapped.
     * @param userQuery The specific request or question from the user.
     * @param aiPersona The defined role or personality for the AI to adopt.
     * @param llm The specific {@link LLM} instance to be used.
     * @param responseClass The Java class representing the desired structured response type.
     * @return An instance of type {@code T} containing the structured response data.
     * @throws JsonProcessingException If there is an error during the final response deserialization.
     * @throws ClassNotFoundException If a class used in reflection (tool call) cannot be found.
     * @throws InvocationTargetException If an invoked tool method throws an exception.
     * @throws NoSuchMethodException If a required constructor or method (in a tool or response class) is not found.
     * @throws InstantiationException If the system is unable to create a new instance (tool or response class).
     * @throws IllegalAccessException If the application does not have access to a definition (tool or response class).
     */
    @Override
    public <T> T useAgent(String userQuery, String aiPersona, LLM llm, Class<T> responseClass) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<ReflectionInvocableMethod> invocableMethodList = AgentImpl.callWithToolsForPlan(
                userQuery, llm
        );

        List<MethodExecutionResult> methodExecutionResults = ReflectionCaller.executePipeline(invocableMethodList);

        T response = AgentImpl.callForFinalResponse(aiPersona, userQuery, methodExecutionResults, llm, responseClass);
        return response;
    }

    private static List<ReflectionInvocableMethod> callWithToolsForPlan(String userQuery, LLM llm) throws JsonProcessingException {
        String completePrompt = getCompletePromptForPlan(userQuery);


        String generateContentResponse = llm.call(completePrompt);

        List<ReflectionInvocableMethod> response;
        ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();

        try {
            response = objectMapper.readValue(generateContentResponse
                            .replace("```json", "")
                            .replace("```", ""),
                    new TypeReference<>() {
                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert " + llm.getModelName() + " generateContentResponse to POJO\n"+e);
        }
        return response;
    }

    private static String getCompletePromptForPlan(String userQuery) throws JsonProcessingException {
        String toolsJson = AiUtil.getAiToolsAsJson();
        String outputFormat = Util.convertToString(ReflectionInvocableMethod.class);


        String completePrompt = String.format("""
                [SYSTEM INSTRUCTIONS]
                You are an expert AI assistant that functions as a tool-use planner".
                Your sole purpose is to analyze a user's query and generate a JSON plan of tool calls required to fulfill it.
                            
                [RULES]
                1. Analyze the Query: Carefully examine the user's query to understand their intent.
                2. Select Tools: From the list of available tools, choose the most appropriate tool(s) to call.
                3. Generate Arguments: For each tool call, determine the most effective arguments based on the user's query. Do NOT use the entire query as an argument unless it is the most logical.
                4.  Chaining Method Calls:
                    a. Saving a Result: To save a method's output for a later step, add a `"returnObjectKey"` field to its JSON object. The value should be a descriptive placeholder string, like `{{product_name}}` or `{{search_results}}`.
                    b. Using a Saved Result: To use a saved result in a subsequent method, set the argument's `"value"` to the exact placeholder string you defined in a previous step (e.g., `"value": "{{product_name}}"`).
                5. Execution Order: The list of method calls MUST be in the correct sequential order. Any method that uses a placeholder in its arguments must appear AFTER the method that defines that placeholder in its `returnObjectKey`.
                6. Format Output: Your output MUST be a valid JSON array of method calls.
                7. Empty Plan: If no tools are required to answer the query, you MUST return an empty array `[]`.
                8. No Extra Text: Do not provide any explanation, field or text outside of the `Output Format` JSON array.
                            
                [TOOLS AVAILABLE]
                %s
                            
                [EXAMPLE]
                1. User Query: "Find me some information on the product with the id 1234ABC"
                 Your Output: [
                    {
                        "className": "org.example.ProductService",
                        "methodName": "findProductName",
                        "methodArguments": [{"type": "java.lang.String", "value": "1234ABC"}],
                        "returnObjectKey": "{{product_name}}"
                    },
                    {
                        "className":"org.example.web.search.DuckDuckGo",
                        "methodName":"search",
                        "methodArguments":[{"type":"java.lang.String","value":"{{product_name}}"}],
                        "returnObjectKey": "{{search_result}}"
                    }
                    ]
                2. User Query: "I need to find a Nivea brand anti-perspirant for under 15 dollars. Only show me stuff that's in stock."
                 Your Output:[
                    {
                        "className": "org.example.ProductService",
                        "methodName": "findProducts",
                        "methodArguments": [{"type": "org.example.ProductService$SearchFilter","value": {"searchWord": "anti-perspirant","maxPrice": "15.0","inStockOnly": "true"}}], 
                        "returnObjectKey": "{{arg0}}"
                    }
                 ]
                            
                            
                [TASK]
                User Query: "<<<%s>>>"
                Output Format: [%s]
                Your Output:
                """, toolsJson, userQuery, outputFormat);
        return completePrompt;
    }

    /**
     *
     * @param aiPersona example = "A product describer, that give description of products to be sold online"
     * */
    private static <T> T callForFinalResponse(String aiPersona, String userQuery, List<MethodExecutionResult> executionResults, LLM llm, Class<T> responseType) throws JsonProcessingException {
        String completePrompt = getPromptForFinalResult(aiPersona, userQuery, executionResults, responseType);


        String generateContentResponse = llm.call(completePrompt);

        T response;

        ObjectMapper objectMapper = ObjectMapperSingleton.getObjectMapper();
        try {
            response = objectMapper.readValue(generateContentResponse.replace("```json", "").replace("```", ""), responseType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to convert " + llm.getModelName() + " generateContentResponse to POJO\n"+e + '\n' + generateContentResponse);
        }
        return response;
    }

    private static <T> String getPromptForFinalResult(String aiPersonality, String userQuery, List<MethodExecutionResult> executionResults, Class<T> responseType) throws JsonProcessingException {
        String toolResultsJson = ObjectMapperSingleton.getObjectMapper().writeValueAsString(executionResults); // The JSON from your list of ToolExecutionResult
        String finalOutputFormat = Util.convertToString(responseType);
        String chatHistoryJson = "";

        String synthesisPrompt = String.format("""
                [SYSTEM_INSTRUCTIONS]
                You are %s.
                Your purpose is to synthesize a final answer by analyzing the user's query, the conversation history, and the results from any tools that were called.
                
                [RULES]
                1. Primary Goal: Your main goal is to answer the user's latest query in the `[TASK]` section.
                2. Use All Context: Use the `[CHAT_HISTORY]` to understand the flow of the conversation and the `[TOOL_RESULTS]` for factual data.
                3. Synthesize: The tool results may represent a sequence of steps. Analyse the entire chain to understand the data flow. Focus on and combine the most relevant tool responses to construct your answer.
                4. Handle Missing Info: If the `[CHAT_HISTORY]` or `[TOOL_RESULTS]` are empty, unhelpful, or don't contain enough information, use the JSON value `null` for non-string fields (like numbers, booleans, objects) of the Final Output Format. For string fields, state that you were unable to find the details. Do not invent information.
                5. Strictly Adhere to Format: Your final output MUST be a single, valid JSON object that conforms to the Final Output Format. Provide no other text.
                                
                [CHAT_HISTORY]
                %s
                                
                [TOOL_RESULTS]
                %s
                                
                [EXAMPLE]
                // Example 1: A multi-step chained query
                User Query: "Describe my top product"
                Tool Results: [ {
                    "request" : {"className" : "org.example.MyService","methodName" : "getCurrentUserId","methodArguments" : [ ],"returnObjectKey" : "{{user_id}}"},
                    "response" : "User_@12"
                }, {
                    "request" : {"className" : "org.example.ProductService","methodName" : "getUsersTopProductName","methodArguments" : [ {"type" : "java.lang.String","value" : "{{user_id}}"} ],"returnObjectKey" : "{{top_product_name}}"},
                    "response" : "Samsung galaxy s25 Ultra"
                }, {
                    "request" : {"className" : "org.example.web.WebSearchProcessor","methodName" : "search","methodArguments" : [ {"type" : "java.lang.String","value" : "{{top_product_name}}"} ]},
                    "response" : "The Samsung Galaxy S25 Ultra boasts a tough titanium frame and Gorilla® Armor 2 display glass for enhanced durability, with an IP68 rating for water and dust resistance, and integrated Galaxy AI features"
                } ]
                Your Output: {
                    "productName" : "Samsung galaxy s25 Ultra",
                    "description" : "The Samsung Galaxy S25 Ultra features a tough titanium frame, Gorilla® Armor 2 display glass for enhanced durability, an IP68 rating for water and dust resistance, and integrated Galaxy AI features.",
                    "price": 203399.99,
                    "toolsUsed" : [ "getCurrentUserId", "getUsersTopProductName", "search" ]
                }
                
                // Example 2: A single-step query with helpful results
                User Query: "What is AI"
                Tool Results: [{"request":{"className":"org.example.google.Search","methodName":"search","methodArguments":[{"type":"java.lang.String","value":"Summary of AI"}]},"response":"AI (Artificial Intelligence) is the development of computer systems capable of performing tasks that typically require human intelligence."}]
                Your Output:{"summary":"AI is the development of computer systems performing human-like tasks","researchAbout":"AI (Artificial Intelligence)"}
                
                // Example 3: A single-step query with unhelpful results
                User Query: "SoPure Cream"
                Tool Results: [{"request":{"className":"org.example.ProductService","methodName":"findProduct","methodArguments":[{"type":"java.lang.String","value":"Mona Lisa"}],"returnObjectKey" : "{{product_details}}"},"response":{"name":"Mona Lisa","price":"1200", "type": "replica"}}]
                Your Output:{"productName":"SoPure Cream","description":"Unable to find the details.","price":null,"toolsUsed" : [ ]}
                 
                [TASK]
                User Query: "<<<%s>>>"
                Final Output Format: %s
                Your Output:
                """, aiPersonality,
                chatHistoryJson, // A JSON representation of the conversation so far
                toolResultsJson, // The JSON from your ToolExecutionResult
                userQuery, // The user's most recent message
                finalOutputFormat
        );

        return synthesisPrompt;

    }


}
