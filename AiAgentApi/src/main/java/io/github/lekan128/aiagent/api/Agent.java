package io.github.lekan128.aiagent.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.lekan128.aiagent.api.llm.LLM;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Represents the core Artificial Intelligence (AI) Agent defined in the ai-agent-core library.
 *
 * <p>This interface defines the fundamental capability of routing a user query
 * to a concrete {@link LLM} implementation and coercing the response into a structured format.
 * Instances of this interface MUST be obtained via the
 * {@code AgentProvider} utility class, which is part of the core library; direct instantiation
 * is strongly discouraged and may lead to improper resource handling.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 * see AgentProvider (In ai-agent-core library)
 */
public interface Agent {

    /**
     * Executes the main agent functionality, processing a user query, applying an
     * AI persona, and converting the {@link LLM}'s raw response into a specified Java object structure.
     *
     * <p>This method orchestrates the entire process: it generates the prompt, passes it to the
     * specific {@code LLM} implementation via {@link LLM#call(String)}, and then uses the
     * {@code ObjectMapperSingleton} to deserialize the response into the target class.</p>
     *
     * @param <T> The target type to which the LLM's response should be mapped.
     * @param userQuery The specific request or question from the user. E.g.:
     *  "Summarize the features of the new macbook laptop model."
     * @param aiPersona The defined role or personality that the AI should adopt,
     *  which guides the tone and style of the response. E.g., "An expert product describer."
     * @param llm The specific {@link LLM} instance (e.g., {@code Gemini}) to be used
     *  for generating the response.
     * @param responseClass The Java class representing the desired structure of the
     *  response (e.g.,
     *  {a ProductDescription class which can have a String productName, String description, List.of(String) attributes, Double price}).
     *  The LLM's output will be mapped to an object of this type.
     * @param responseTypeParameters an array of Types(classes for the response type).
     *  For example, if the response type you want is a List of Strings, then you would use it like this:
     *  useAgent(userQuery, aiPersona, llm, List.class, String.class)
     * @return An instance of type {@code T} containing the structured response data
     *  as mapped from the LLM's output (typically JSON).
     * @throws JsonProcessingException If there is an error during the deserialization of
     * the LLM's raw response (e.g., if the JSON is malformed or
     * does not match {@code responseClass} structure).
     * @throws ClassNotFoundException If the system cannot find a required class during
     * initialization or processing.
     * @throws InvocationTargetException If the underlying method invocation fails.
     * @throws NoSuchMethodException If a required constructor or method is not found on
     * the {@code responseClass} during reflection.
     * @throws InstantiationException If the system is unable to create a new instance of
     * the {@code responseClass}.
     * @throws IllegalAccessException If the application does not have access to a definition
     * of the specified class, field, method, or constructor.
     */
    <T> T useAgent(String userQuery, String aiPersona, LLM llm, Class<T> responseClass, Type... responseTypeParameters) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
