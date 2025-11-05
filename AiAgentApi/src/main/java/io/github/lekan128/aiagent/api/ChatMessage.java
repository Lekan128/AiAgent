package io.github.lekan128.aiagent.api;

public class ChatMessage {
    private Role role;
    private String content;

    public enum Role {
        USER, // Message sent by the end-user
        AGENT, // Message sent by the AI Agent
    }

    public static ChatMessage newInstance(Role role, String content) {
        return new ChatMessage(role, content);
    }
    public ChatMessage(Role role, String content) {
        this.role = role;
        this.content = content;
    }
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
