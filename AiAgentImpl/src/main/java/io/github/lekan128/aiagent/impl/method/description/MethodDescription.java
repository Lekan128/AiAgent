package io.github.lekan128.aiagent.impl.method.description;

import java.util.List;
import java.util.Map;

/**
 * @apiNote Internal API. Not intended for external use.
 * &#064;Internal
 * Not part of the public API. Subject to change without notice.
 */
public class MethodDescription {
    private String description;

    private String className;
    private String methodName;
    private List<Parameter> methodArguments;
    private String returnType;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<Parameter> getMethodArguments() {
        return methodArguments;
    }

    public void setMethodArguments(List<Parameter> methodArguments) {
        this.methodArguments = methodArguments;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public static class Parameter {
        private String name;
        private String description;
        private String type;
        private boolean required =true;

        private Map<String, Object> fields;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public Map<String, Object> getFields() {
            return fields;
        }

        public void setFields(Map<String, Object> fields) {
            this.fields = fields;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}