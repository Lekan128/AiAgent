package io.github.lekan128.aiagent.impl.method.caller;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Data transfer object (DTO) representing a single method invocation request provided by the LLM
 * as part of a complex Tool Calling instruction set.
 *
 * <p>This object defines all components necessary for a reflection call: the target class,
 * the method name, the arguments, and an optional key to store the result in the execution context.</p>
 *
 * <p><strong>Note:</strong> This class is part of the library's internal machinery and is not intended
 * for direct instantiation or manipulation by end-users.</p>
 *
 * @author Olalekan
 * @since 1.0.0
 */
public class ReflectionInvocableMethod {
    /** The fully qualified class name containing the target method. */
    @JsonProperty
    private String className;

    /** The name of the method to be invoked. */
    @JsonProperty
    private String methodName;

    /** The list of arguments and their values to be passed to the method. */
    @JsonProperty
    private List<MethodArgument> methodArguments;

    /** An optional key under which the method's result should be stored in the execution context
     for subsequent method calls. If {@code null}, the result is not stored. */
    private String returnObjectKey;

    /**
     * Retrieves the fully qualified name of the target class.
     * @return The class name.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the fully qualified name of the target class.
     * @param className The class name.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Retrieves the name of the method to be invoked.
     * @return The method name.
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the name of the method to be invoked.
     * @param methodName The method name.
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Retrieves the list of arguments for the method call.
     * @return A list of {@link MethodArgument} DTOs.
     */
    public List<MethodArgument> getMethodArguments() {
        return methodArguments;
    }

    /**
     * Sets the list of arguments for the method call.
     * @param methodArguments A list of {@link MethodArgument} DTOs.
     */
    public void setMethodArguments(List<MethodArgument> methodArguments) {
        this.methodArguments = methodArguments;
    }

    /**
     * Retrieves the key used to store the method's return value in the execution context.
     * @return The context storage key, or {@code null} if the result should not be stored.
     */
    public String getReturnObjectKey() {
        return returnObjectKey;
    }

    /**
     * Sets the key used to store the method's return value in the execution context.
     * @param returnObjectKey The context storage key.
     */
    public void setReturnObjectKey(String returnObjectKey) {
        this.returnObjectKey = returnObjectKey;
    }
}