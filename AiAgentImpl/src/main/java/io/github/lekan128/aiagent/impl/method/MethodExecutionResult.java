package io.github.lekan128.aiagent.impl.method;

import io.github.lekan128.aiagent.impl.method.caller.ReflectionInvocableMethod;

/**
 * @apiNote Internal API. Not intended for external use.
 * &#064;Internal
 * Not part of the public API. Subject to change without notice.
 */
public class MethodExecutionResult {
    private ReflectionInvocableMethod request;
    private Object response;

    public MethodExecutionResult() {
    }

    public MethodExecutionResult(ReflectionInvocableMethod request, Object response) {
        this.request = request;
        this.response = response;
    }

    public ReflectionInvocableMethod getRequest() {
        return request;
    }

    public void setRequest(ReflectionInvocableMethod request) {
        this.request = request;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
