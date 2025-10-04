package io.github.lekan128.aiagent.core;

import io.github.lekan128.aiagent.impl.AgentFactory;
import io.github.lekan128.aiagent.api.Agent;

public final class AgentProvider {
    private static final Agent INSTANCE;

    static {
        INSTANCE = AgentFactory.createAgent();
    }
    private AgentProvider() {}

    public static Agent get() {
        return INSTANCE;
    }

}