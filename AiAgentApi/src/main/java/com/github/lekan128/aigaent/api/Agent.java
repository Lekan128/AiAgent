package com.github.lekan128.aigaent.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.lekan128.aigaent.api.llm.LLM;

import java.lang.reflect.InvocationTargetException;

public interface Agent {
    <T> T useAgent(String userQuery, String aiPersona, LLM llm, Class<T> responseClass) throws JsonProcessingException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
}
