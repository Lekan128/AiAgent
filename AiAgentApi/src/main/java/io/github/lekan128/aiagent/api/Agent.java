package io.github.lekan128.aiagent.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.lekan128.aiagent.api.llm.LLM;

import java.lang.reflect.InvocationTargetException;

public interface Agent {
    <T> T useAgent(String userQuery, String aiPersona, LLM llm, Class<T> responseClass) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
