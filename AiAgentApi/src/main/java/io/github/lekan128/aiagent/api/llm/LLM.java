package io.github.lekan128.aiagent.api.llm;

/**
 * Abstract base class that defines the core contract for all Language Model (LLM) implementations.
 *
 * <p>Users are expected to **create concrete subclasses** of {@code LLM} to integrate any
 * proprietary or third-party Language Model API (e.g., custom local models, Azure OpenAI, etc.).
 * Any class extending {@code LLM} can be passed to the {@link io.github.lekan128.aiagent.api.Agent#useAgent(String, String, LLM, Class, java.lang.reflect.Type...)}
 * method, providing a flexible, provider-agnostic system for your application.</p>
 *
 * <p>This abstraction is crucial for maintaining a decoupled and provider-agnostic agent workflow.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 * @see Gemini
 */
public abstract class LLM {

    /**
     * Retrieves the specific, human-readable name of the LLM model being used.
     *
     * <p>This name is typically used for logging, monitoring, and error reporting,
     * allowing developers to quickly identify which model generated a specific response or failure.</p>
     *
     * @return The canonical name of the model (e.g., "gemini-2.5-flash", "llama-3-8b").
     */
    public abstract String getModelName();

    /**
     * Sends the complete, formatted prompt to the underlying LLM API and retrieves the raw text response.
     *
     * <p>The implementation of this method must handle all necessary API calls,
     * authentication, and network communication for the specific LLM. The prompt passed
     * is **fully generated and structured by the {@code Agent}** implementation.</p>
     *
     * @param prompt The final, complete text prompt to be sent to the LLM service.
     * @return The raw, main text content of the LLM's response, stripped of any metadata or internal API wrappers.
     */
    public abstract String call(String prompt);
}
