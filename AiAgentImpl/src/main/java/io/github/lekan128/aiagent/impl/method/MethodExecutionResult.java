package io.github.lekan128.aiagent.impl.method;

import io.github.lekan128.aiagent.impl.method.caller.ReflectionInvocableMethod;

/**
 * Data transfer object (DTO) encapsulating the result of a single method invocation
 * within the execution pipeline.
 *
 * <p>It pairs the original request with the actual return value produced by the reflection call.</p>
 *
 * <p><strong>Internal API:</strong> This class is strictly for internal library use
 * and is not intended for external consumption. Its methods and structure are subject
 * to change without notice.</p>
 */
public class MethodExecutionResult {
    private ReflectionInvocableMethod request;
    private Object response;

    /**
     * Constructs an empty {@code MethodExecutionResult}.
     */
    public MethodExecutionResult() {
    }

    /**
     * Constructs a {@code MethodExecutionResult} with the executed request and its response.
     * @param request The original {@link ReflectionInvocableMethod} request.
     * @param response The object returned by the invoked method.
     */
    public MethodExecutionResult(ReflectionInvocableMethod request, Object response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Retrieves the original method invocation request.
     * @return The {@link ReflectionInvocableMethod} that was executed.
     */
    public ReflectionInvocableMethod getRequest() {
        return request;
    }

    /**
     * Sets the original method invocation request.
     * @param request The {@link ReflectionInvocableMethod} that was executed.
     */
    public void setRequest(ReflectionInvocableMethod request) {
        this.request = request;
    }

    /**
     * Retrieves the return value from the method execution.
     * @return The object returned by the method.
     */
    public Object getResponse() {
        return response;
    }

    /**
     * Sets the return value from the method execution.
     * @param response The object returned by the method.
     */
    public void setResponse(Object response) {
        this.response = response;
    }
}
