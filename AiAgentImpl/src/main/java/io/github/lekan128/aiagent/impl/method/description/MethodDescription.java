package io.github.lekan128.aiagent.impl.method.description;

import java.util.List;
import java.util.Map;

/**
 * A data transfer object (DTO) used internally by the {@code Agent} to hold descriptive metadata
 * about a user-defined method that has been exposed as an AI tool.
 *
 * <p>This object is constructed via reflection and the {@code @AiToolMethod} and {@code @ArgDesc}
 * annotations. It is primarily used to serialize the method's structure into a format (e.g., JSON Schema)
 * that the Language Model (LLM) can understand for the purpose of Tool Calling.</p>
 *
 * <p><strong>Note:</strong> This class is part of the library's internal machinery and is not intended
 * for direct instantiation or manipulation by end-users.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 * @see io.github.lekan128.aiagent.api.annotation.AiToolMethod
 * @see io.github.lekan128.aiagent.api.annotation.ArgDesc
 */
public class MethodDescription {
    private String description;
    private String className;
    private String methodName;
    private List<Parameter> methodArguments;
    private String returnType;

    /**
     * Retrieves the overall purpose of the method, typically derived from the {@code @AiToolMethod} value.
     * @return The descriptive string of the method's functionality.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the overall purpose of the method.
     * @param description The descriptive string of the method's functionality.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the name of the class that contains the tool method.
     * @return The simple name of the host class.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the name of the class that contains the tool method.
     * @param className The simple name of the host class.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Retrieves the name of the tool method.
     * @return The method name.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the name of the tool method.
     * @param methodName The method name.
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Retrieves the list of parameters for the tool method.
     * @return A list of {@link Parameter} objects describing the method's arguments.
     */
    public List<Parameter> getMethodArguments() {
        return methodArguments;
    }

    /**
     * Sets the list of parameters for the tool method.
     * @param methodArguments A list of {@link Parameter} objects.
     */
    public void setMethodArguments(List<Parameter> methodArguments) {
        this.methodArguments = methodArguments;
    }

    /**
     * Retrieves the fully qualified name of the method's return type.
     * @return The return type as a string.
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Sets the fully qualified name of the method's return type.
     * @param returnType The return type as a string.
     */
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    /**
     * Nested DTO representing the metadata for a single parameter of a tool method.
     *
     * <p>This structure is used to generate the JSON Schema property definitions
     * required by the LLM for argument prediction.</p>
     */
    public static class Parameter {
        private String name;
        private String description;
        private String type;
        private boolean required =true;
        private Map<String, Object> fields;

        /**
         * Retrieves the name of the parameter.
         * @return The parameter name.
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the name of the parameter.
         * @param name The parameter name.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Retrieves the Java type of the parameter (e.g., "String", "int", "List").
         * @return The parameter type.
         */
        public String getType() {
            return type;
        }

        /**
         * Sets the Java type of the parameter.
         * @param type The parameter type.
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * Checks if the parameter is required for the tool call. Defaults to true.
         * @return True if the parameter is required, false otherwise.
         */
        public boolean isRequired() {
            return required;
        }

        /**
         * Sets whether the parameter is required.
         * @param required True if required.
         */
        public void setRequired(boolean required) {
            this.required = required;
        }

        /**
         * Retrieves the map of additional fields, primarily used for complex JSON schema definitions
         * (e.g., for properties of an object parameter).
         * @return A map of field details.
         */
        public Map<String, Object> getFields() {
            return fields;
        }

        /**
         * Sets the map of additional fields.
         * @param fields A map of field details.
         */
        public void setFields(Map<String, Object> fields) {
            this.fields = fields;
        }

        /**
         * Retrieves the description of the parameter, derived from the {@code @ArgDesc} annotation.
         * @return The descriptive string for the parameter.
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the description of the parameter.
         * @param description The descriptive string for the parameter.
         */
        public void setDescription(String description) {
            this.description = description;
        }
    }
}