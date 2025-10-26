package io.github.lekan128.aiagent.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;
import io.github.lekan128.aiagent.api.ObjectMapperSingleton;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

class Util {

    static String convertToString(Type target, Type... typeParameters) throws JsonProcessingException {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
                SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);
//        Use this for Map
//        configBuilder.with(Option.MAP_VALUES_AS_ADDITIONAL_PROPERTIES);

        SchemaGenerator generator = new SchemaGenerator(configBuilder.build());
        JsonNode fullSchema = generator.generateSchema(target, typeParameters);
        String type = fullSchema.has("type") ? fullSchema.get("type").asText() : "object";

        ObjectMapper mapper = ObjectMapperSingleton.getObjectMapper();

        JsonNode reducedSchema;
        Map<String, Object> flatSchema;
        String json;

        switch (type) {
            case "object" -> {

                if (fullSchema.has("properties")) {
                    reducedSchema = fullSchema.get("properties");
                    flatSchema = flattenSchema(reducedSchema);
                    json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flatSchema);
                    return json;
                }

                // For maps: additionalProperties defines the type of values
                if (fullSchema.has("additionalProperties")) {
                    JsonNode valNode = fullSchema.get("additionalProperties");

                    if (valNode.get("type").asText().equals("object")) {

                        // Array of objects → recurse
                        if (valNode.has("properties")) {
                            Map<String, Object> valueFlattenSchema = flattenSchema(valNode.get("properties"));
                            flatSchema =  Map.of("key", "string", "value", valueFlattenSchema);
                            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flatSchema);
                        } else {
                            flatSchema =  Map.of("key", "string", "value", "{}");
                            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flatSchema);
                        }
                    }

                    flatSchema = Map.of("key", "string", "value",valNode.get("type").asText());
                    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flatSchema);
                }

                //Unknown
                return  "{}";

            }
            case "array" -> {
                reducedSchema = fullSchema.get("items");

                if (reducedSchema.get("type").asText().equals("object")) {
                    // Array of objects → recurse
                    if (reducedSchema.has("properties")) {
                        flatSchema = flattenSchema(reducedSchema.get("properties"));
                    } else {
                        flatSchema = new HashMap<>();
                    }


                    Map[] flatSchema1 = {flatSchema};
                    json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flatSchema1);
                    return json;
                }
                String[] types = {reducedSchema.get("type").asText()};

                json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(types);

                return json;

            }
            default -> {
                return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fullSchema.get("type"));
            }
        }

    }
    private static Map<String, Object> flattenSchema(JsonNode propertiesNode) {
        Map<String, Object> result = new LinkedHashMap<>();

        Iterator<Map.Entry<String, JsonNode>> fields = propertiesNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String fieldName = field.getKey();
            JsonNode fieldDef = field.getValue();

            String type;
            if (fieldDef.has("type")) {
                type = fieldDef.get("type").asText();
            } else if (fieldDef.isObject() && fieldDef.size() == 0) {
                type = "Any"; // explicitly empty {} // represents unconstrained schema, maps to Java Object
            } else {
                type = "Any"; // fallback safety
            }

            switch (type) {
                case "object":
                    // Nested object → recurse
                    if (fieldDef.has("properties")) {
                        result.put(fieldName, flattenSchema(fieldDef.get("properties")));
                    } else {
                        // For maps: additionalProperties defines the type of values
                        if (fieldDef.has("additionalProperties")) {
                            JsonNode valType = fieldDef.get("additionalProperties");
                            result.put(fieldName, Map.of("key", "string", "value",valType.get("type").asText()));
                        } else {
                            result.put(fieldName, "{}");
                        }
                    }
                    break;

                case "array":
                    JsonNode items = fieldDef.get("items");
                    if (items == null || !items.has("type")){
                        result.put(fieldName, new String[]{"Any"});
                    } else
                    if (items.get("type").asText().equals("object")) {
                        // Array of objects → recurse
                        Map<String, Object> inner = items.has("properties")
                                ? flattenSchema(items.get("properties"))
                                : Map.of();
                        result.put(fieldName, new Object[]{inner});
                    } else {
                        // Array of scalars
                        result.put(fieldName, new String[]{items.get("type").asText()});
                    }
                    break;

                default:
                    // Scalars (string, integer, boolean, etc.)
                    result.put(fieldName, type);
            }
        }
        return result;
    }
}
