package io.github.lekan128.aiagent.impl;

import io.github.lekan128.aiagent.api.Agent;

/**
 * Factory class responsible for the instantiation and configuration of the concrete {@link Agent} implementation.
 *
 * <p>This class encapsulates the knowledge of which specific class implements the {@code Agent} interface (i.e., {@code AgentImpl}),
 * adhering to the Factory Pattern. This separation allows the internal implementation to change without affecting client code.</p>
 *
 * <p>Users of the library should not directly access or instantiate this factory but rather use the
 * {link AgentProvider} to retrieve the singleton instance created by this factory.</p>
 *
 * <p><strong>Internal API:</strong> This class is strictly for internal library use
 * and is not intended for external consumption. Its methods and structure are subject
 * to change without notice.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 * @see Agent
 * see AgentProvider
 */
public class AgentFactory {

    /**
     * Creates and returns a new, configured instance of the concrete {@link Agent} implementation.
     *
     * <p>This method is intended to be called only by the {link AgentProvider} during application startup
     * to ensure the Agent is initialized correctly and consistently.</p>
     *
     * @return A newly created instance of {@code AgentImpl}, which implements the {@code Agent} contract.
     */
    public static Agent createAgent(){
        return new AgentImpl();
    }
}
