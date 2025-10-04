package com.github.lekan128.aigent.core;

import com.github.lekan128.aiagent.impl.AgentFactory;
import com.github.lekan128.aigaent.api.Agent;

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