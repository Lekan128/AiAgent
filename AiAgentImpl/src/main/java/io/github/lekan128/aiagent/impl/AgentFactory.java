package io.github.lekan128.aiagent.impl;

import io.github.lekan128.aiagent.api.Agent;

public class AgentFactory {
    public static Agent createAgent(){
        return new AgentImpl();
    }
}
