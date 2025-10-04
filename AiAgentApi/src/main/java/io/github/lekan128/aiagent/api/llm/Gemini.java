package io.github.lekan128.aiagent.api.llm;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import io.github.cdimascio.dotenv.Dotenv;

public class Gemini extends LLM {

    @Override
    public String getModelName() {
        return "Gemini";
    }

    @Override
    public String call(String prompt){
        Dotenv dotenv = Dotenv.load(); // Loads variables from .env in the current directory
        GenerateContentResponse generateContentResponse;

        try (Client client = Client.builder().apiKey(dotenv.get("GEMINI_API_KEY")).build()) {
            generateContentResponse = client.models.generateContent(
                    "gemini-2.5-flash",
                    prompt,
                    null);
        }
        return generateContentResponse.text();
    }
}

