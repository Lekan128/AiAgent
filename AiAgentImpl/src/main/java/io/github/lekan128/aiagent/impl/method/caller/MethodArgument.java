package io.github.lekan128.aiagent.impl.method.caller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data transfer object (DTO) representing a single argument supplied to a method call
 * within the reflection execution pipeline.
 *
 * <p>This class is designed to hold the data structure received from the Language Model (LLM)
 * in response to a Tool Call request. The object is deserialized using Jackson and represents
 * a single parameter's expected type and value.</p>
 *
 * <p>The annotation {@code @JsonIgnoreProperties(ignoreUnknown = true)} ensures flexibility
 * against future or provider-specific fields in the JSON payload.</p>
 *
 * <p><strong>Note:</strong> This class is part of the library's internal machinery and is not intended
 * for direct instantiation or manipulation by end-users.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class MethodArgument {
    /** The expected Java type of the argument (e.g., "String", "int", "java.util.List", "package.anyOtherObject"). */
    @JsonProperty
    private String type;

    /** The actual value of the argument, which may be a primitive, String, Map, or List
     based on the JSON deserialization, or any other Object. */
    @JsonProperty
    private Object value;

    /**
     * Retrieves the expected Java type of the argument.
     * @return The argument type as a string.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the expected Java type of the argument.
     * @param type The argument type as a string.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Retrieves the deserialized value of the argument.
     * @return The argument value as a generic {@code Object}.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the deserialized value of the argument.
     * @param value The argument value.
     */
    public void setValue(Object value) {
        this.value = value;
    }
}