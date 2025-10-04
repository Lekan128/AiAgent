package io.github.lekan128.aiagent.impl.method.caller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @apiNote Internal API. Not intended for external use.
 * &#064;Internal
 * Not part of the public API. Subject to change without notice.
 */
public class ReflectionInvocableMethod {
    @JsonProperty
    private String className;
    @JsonProperty
    private String methodName;
    @JsonProperty
    private List<MethodArgument> methodArguments;

    private String returnObjectKey;

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

    public List<MethodArgument> getMethodArguments() {
        return methodArguments;
    }

    public void setMethodArguments(List<MethodArgument> methodArguments) {
        this.methodArguments = methodArguments;
    }

    public String getReturnObjectKey() {
        return returnObjectKey;
    }

    public void setReturnObjectKey(String returnObjectKey) {
        this.returnObjectKey = returnObjectKey;
    }
}
