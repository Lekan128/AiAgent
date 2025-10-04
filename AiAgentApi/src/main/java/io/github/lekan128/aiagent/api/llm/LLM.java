package io.github.lekan128.aiagent.api.llm;

public abstract class LLM {
    public abstract String getModelName();
    public abstract String call(String prompt);
}
