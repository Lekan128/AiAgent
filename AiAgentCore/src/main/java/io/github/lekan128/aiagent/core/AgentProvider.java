package io.github.lekan128.aiagent.core;

import io.github.lekan128.aiagent.impl.AgentFactory;
import io.github.lekan128.aiagent.api.Agent;

/**
 * Provides a guaranteed, thread-safe, singleton instance of the {@link Agent} interface.
 *
 * <p>This utility class implements the Singleton pattern using a static inner final field.
 * The instance is initialized lazily or eagerly (depending on {@code AgentFactory})
 * upon class loading and remains the only instance available for the application's
 * lifecycle. Users <strong>must</strong> obtain their {@code Agent} instance using the
 * {@link #get()} method.</p>
 *
 * <p>The private constructor prevents external instantiation, enforcing the Singleton contract.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 * @see Agent
 * @see AgentFactory
 */
public final class AgentProvider {
    /**
     * The single, final instance of the Agent, initialized via the static block.
     */
    private static final Agent INSTANCE;

    static {
        INSTANCE = AgentFactory.createAgent();
    }
    /**
     * Private constructor to prevent external instantiation of this utility class.
     */
    private AgentProvider() {}

    /**
     * Retrieves the singleton instance of the {@link Agent}.
     *
     * <p>This is the primary method for clients to access the initialized AI Agent.</p>
     *
     * @return The single, globally available instance of the {@code Agent}.
     */
    public static Agent get() {
        return INSTANCE;
    }

}