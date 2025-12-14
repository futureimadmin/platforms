package com.nebula.shared.domain;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Firestore document for Conversation persistence
 */
public class ConversationDocument {
    
    @DocumentId
    private String id;
    
    @PropertyName("title")
    private String title;
    
    @PropertyName("planId")
    private String planId;
    
    @PropertyName("agentId")
    private String agentId;
    
    @PropertyName("userId")
    private String userId;
    
    @PropertyName("sessionId")
    private String sessionId;
    
    @PropertyName("messages")
    private List<MessageDocument> messages;
    
    @PropertyName("status")
    private String status; // ACTIVE, PAUSED, COMPLETED, ARCHIVED
    
    @PropertyName("createdAt")
    private Instant createdAt;
    
    @PropertyName("updatedAt")
    private Instant updatedAt;
    
    @PropertyName("lastMessageAt")
    private Instant lastMessageAt;
    
    @PropertyName("messageCount")
    private Integer messageCount;
    
    @PropertyName("context")
    private Map<String, Object> context;
    
    @PropertyName("metadata")
    private Map<String, Object> metadata;

    // Default constructor for Firestore
    public ConversationDocument() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = "ACTIVE";
        this.messageCount = 0;
    }

    // Constructor with required fields
    public ConversationDocument(String title, String planId, String agentId, String userId) {
        this();
        this.title = title;
        this.planId = planId;
        this.agentId = agentId;
        this.userId = userId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = Instant.now();
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
        this.updatedAt = Instant.now();
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
        this.updatedAt = Instant.now();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.updatedAt = Instant.now();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        this.updatedAt = Instant.now();
    }

    public List<MessageDocument> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDocument> messages) {
        this.messages = messages;
        this.messageCount = messages != null ? messages.size() : 0;
        this.updatedAt = Instant.now();
        if (messages != null && !messages.isEmpty()) {
            this.lastMessageAt = Instant.now();
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
        this.updatedAt = Instant.now();
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
        this.updatedAt = Instant.now();
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        this.updatedAt = Instant.now();
    }

    // Utility methods
    public void addMessage(MessageDocument message) {
        if (this.messages == null) {
            this.messages = new java.util.ArrayList<>();
        }
        this.messages.add(message);
        this.messageCount = this.messages.size();
        this.lastMessageAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void incrementMessageCount() {
        this.messageCount = (this.messageCount == null ? 0 : this.messageCount) + 1;
        this.lastMessageAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "ConversationDocument{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", planId='" + planId + '\'' +
                ", agentId='" + agentId + '\'' +
                ", status='" + status + '\'' +
                ", messageCount=" + messageCount +
                ", createdAt=" + createdAt +
                '}';
    }
}