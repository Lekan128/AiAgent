package com.github.lekan128.aigaent.api.llm;

public abstract class LLM {
    public abstract String getModelName();
    public abstract String call(String prompt);
}
