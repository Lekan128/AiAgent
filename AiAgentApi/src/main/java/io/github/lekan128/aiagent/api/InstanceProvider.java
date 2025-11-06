package io.github.lekan128.aiagent.api;

public interface InstanceProvider {
    Object getInstance(Class<?> clazz) throws Exception;
}
