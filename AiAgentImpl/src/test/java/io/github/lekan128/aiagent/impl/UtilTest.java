package io.github.lekan128.aiagent.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lekan128.aiagent.api.ObjectMapperSingleton;
import io.github.lekan128.aiagent.impl.method.caller.ReflectionInvocableMethod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void convertToString() throws JsonProcessingException {
        String actualReflectionInvocableMethod = Util.convertToString(ReflectionInvocableMethod.class);
        String expectedReflectionInvocableMethod = """
                {
                  "className" : "string",
                  "methodArguments" : [ {
                    "type" : "string",
                    "value" : "Any"
                  } ],
                  "methodName" : "string",
                  "returnObjectKey" : "string"
                }
                """;
        ObjectMapper mapper = ObjectMapperSingleton.getObjectMapper();
        JsonNode actualJsonNode = mapper.readTree(actualReflectionInvocableMethod);
        JsonNode expectedJsonNode = mapper.readTree(expectedReflectionInvocableMethod);

        Assertions.assertEquals(actualJsonNode, expectedJsonNode, "ReflectionInvocableMethod JSON content should be equal");

        String actualReflectionInvocableMethodList = Util.convertToString(List.class, ReflectionInvocableMethod.class);
        String expectedReflectionInvocableMethodList = """
                [ {
                  "className" : "string",
                  "methodArguments" : [ {
                    "type" : "string",
                    "value" : "Any"
                  } ],
                  "methodName" : "string",
                  "returnObjectKey" : "string"
                } ]
                """;
        actualJsonNode = mapper.readTree(actualReflectionInvocableMethodList);
        expectedJsonNode = mapper.readTree(expectedReflectionInvocableMethodList);
        Assertions.assertEquals(actualJsonNode, expectedJsonNode, "ReflectionInvocableMethod List JSON content should be equal");

        {
            String expectedString = Util.convertToString(String.class);
            String actualString = "\"string\"";
            Assertions.assertEquals(expectedString, actualString);
        }

        {
            String expectedMap = Util.convertToString(Map.class, String.class, ReflectionInvocableMethod.class);
            String actualMap = "{}";
            Assertions.assertEquals(expectedMap, actualMap);
        }

        record SomeClass(String something, int number, List<ReflectionInvocableMethod> list, Map<String, Integer> map){}

    }

    @Test
    void convertToStringSomeClass() throws JsonProcessingException {
        record SomeClass(String something, int number, List<ReflectionInvocableMethod> list, Map<String, Integer> map){}
        String expected = """
                {
                  "list" : [ {
                    "className" : "string",
                    "methodArguments" : [ {
                      "type" : "string",
                      "value" : "Any"
                    } ],
                    "methodName" : "string",
                    "returnObjectKey" : "string"
                  } ],
                  "map" : "{}",
                  "number" : "integer",
                  "something" : "string"
                }
                """;

        ObjectMapper mapper = ObjectMapperSingleton.getObjectMapper();
        JsonNode actualJsonNode = mapper.readTree(Util.convertToString(SomeClass.class));
        JsonNode expectedJsonNode = mapper.readTree(expected);

        Assertions.assertEquals(actualJsonNode, expectedJsonNode, "JSON content should be equal");


    }

    @Test
    void convertToStringMapOfObjects() throws JsonProcessingException {
        String actual = Util.convertToString(Map.class, String.class, ReflectionInvocableMethod.class);
        ObjectMapper mapper = ObjectMapperSingleton.getObjectMapper();
        JsonNode node = mapper.readTree(actual);
        Assertions.assertTrue(node.isObject(), "Should produce an object schema for Map");
    }

    @Test
    void convertToStringArrayOfPrimitives() throws JsonProcessingException {
        String actual = Util.convertToString(List.class, String.class);
        ObjectMapper mapper = ObjectMapperSingleton.getObjectMapper();
        JsonNode node = mapper.readTree(actual);
        Assertions.assertTrue(node.isArray(), "Should produce array schema");
        Assertions.assertEquals("\"string\"", node.get(0).toString());
    }

    @Test
    void flattenSchemaHandlesUntypedObject() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree("{\"field\": {}}");
        Map<String, Object> flattened = invokeFlattenSchema(node);
        Assertions.assertEquals("Any", flattened.get("field"));
    }

    // helper
    @SuppressWarnings("unchecked")
    private static Map<String, Object> invokeFlattenSchema(JsonNode node) {
        try {
            Method method = Util.class.getDeclaredMethod("flattenSchema", JsonNode.class);
            method.setAccessible(true);
            return (Map<String, Object>) method.invoke(null, node);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}