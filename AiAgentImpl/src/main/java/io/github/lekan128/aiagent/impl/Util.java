package io.github.lekan128.aiagent.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lekan128.aiagent.api.ObjectMapperSingleton;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.github.victools.jsonschema.generator.SchemaVersion;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

class Util {

    static String convertToString(Type target) throws JsonProcessingException {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(
                SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);

        SchemaGenerator generator = new SchemaGenerator(configBuilder.build());
        JsonNode fullSchema = generator.generateSchema(target);
        JsonNode reducedSchema = fullSchema.get("properties");

        Map<String, Object> flatSchema = flattenSchema(reducedSchema);

        ObjectMapper mapper = ObjectMapperSingleton.getObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flatSchema);

        System.out.println(json);
        return json;
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
                            result.put(fieldName, Map.of("key", valType.get("type").asText()));
                        } else {
                            result.put(fieldName, "{}");
                        }
                    }
                    break;

                case "array":
                    JsonNode items = fieldDef.get("items");
                    if (items.get("type").asText().equals("object")) {
                        // Array of objects → recurse
                        if (items.has("properties")) {
                            result.put(fieldName,
                                    new Object[]{flattenSchema(items.get("properties"))});
                        } else {
                            result.put(fieldName, new Object[]{"{}"});
                        }
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
