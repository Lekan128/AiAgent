package com.github.lekan128.aigaent.api;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSingleton {
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (objectMapper != null){
            return objectMapper;
        }
        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(
                com.fasterxml.jackson.annotation.PropertyAccessor.FIELD,
                com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY
        );
        return objectMapper;
    }
}
