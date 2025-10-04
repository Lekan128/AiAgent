package com.github.lekan128.aiagent.impl;

import com.github.lekan128.aigaent.api.Agent;

public class AgentFactory {
    public static Agent createAgent(){
        return new AgentImpl();
    }
}
