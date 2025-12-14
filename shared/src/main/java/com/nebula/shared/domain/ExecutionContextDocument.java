package com.nebula.shared.domain;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;

import java.time.Instant;
import java.util.Map;

/**
 * Firestore document for ExecutionContext persistence
 */
public class ExecutionContextDocument {
    
    @DocumentId
    private String id;
    
    @PropertyName("planId")
    private String planId;
    
    @PropertyName("agentId")
    private String agentId;
    
    @PropertyName("sessionId")
    private String sessionId;
    
    @PropertyName("currentStep")
    private Integer currentStep;
    
    @PropertyName("totalSteps")
    private Integer totalSteps;
    
    @PropertyName("variables")
    private Map<String, Object> variables;
    
    @PropertyName("state")
    private Map<String, Object> state;
    
    @PropertyName("history")
    private java.util.List<ContextHistoryEntry> history;
    
    @PropertyName("createdAt")
    private Instant createdAt;
    
    @PropertyName("updatedAt")
    private Instant updatedAt;
    
    @PropertyName("lastAccessedAt")
    private Instant lastAccessedAt;
    
    @PropertyName("metadata")
    private Map<String, Object> metadata;
    
    @PropertyName("isActive")
    private Boolean isActive;
    
    @PropertyName("parentContextId")
    private String parentContextId;
    
    @PropertyName("childContextIds")
    private java.util.List<String> childContextIds;

    // Default constructor for Firestore
    public ExecutionContextDocument() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastAccessedAt = Instant.now();
        this.isActive = true;
        this.currentStep = 0;
    }

    // Constructor with required fields
    public ExecutionContextDocument(String planId, String agentId, String sessionId) {
        this();
        this.planId = planId;
        this.agentId = agentId;
        this.sessionId = sessionId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        this.updatedAt = Instant.now();
    }

    public Integer getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Integer currentStep) {
        this.currentStep = currentStep;
        this.updatedAt = Instant.now();
        this.lastAccessedAt = Instant.now();
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
        this.updatedAt = Instant.now();
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
        this.updatedAt = Instant.now();
        this.lastAccessedAt = Instant.now();
    }

    public Map<String, Object> getState() {
        return state;
    }

    public void setState(Map<String, Object> state) {
        this.state = state;
        this.updatedAt = Instant.now();
        this.lastAccessedAt = Instant.now();
    }

    public java.util.List<ContextHistoryEntry> getHistory() {
        return history;
    }

    public void setHistory(java.util.List<ContextHistoryEntry> history) {
        this.history = history;
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

    public Instant getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(Instant lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        this.updatedAt = Instant.now();
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = Instant.now();
    }

    public String getParentContextId() {
        return parentContextId;
    }

    public void setParentContextId(String parentContextId) {
        this.parentContextId = parentContextId;
        this.updatedAt = Instant.now();
    }

    public java.util.List<String> getChildContextIds() {
        return childContextIds;
    }

    public void setChildContextIds(java.util.List<String> childContextIds) {
        this.childContextIds = childContextIds;
        this.updatedAt = Instant.now();
    }

    // Utility methods
    public void addVariable(String key, Object value) {
        if (this.variables == null) {
            this.variables = new java.util.HashMap<>();
        }
        this.variables.put(key, value);
        addHistoryEntry("VARIABLE_SET", "Set variable: " + key);
        this.updatedAt = Instant.now();
        this.lastAccessedAt = Instant.now();
    }

    public Object getVariable(String key) {
        this.lastAccessedAt = Instant.now();
        return this.variables != null ? this.variables.get(key) : null;
    }

    public void updateState(String key, Object value) {
        if (this.state == null) {
            this.state = new java.util.HashMap<>();
        }
        this.state.put(key, value);
        addHistoryEntry("STATE_UPDATE", "Updated state: " + key);
        this.updatedAt = Instant.now();
        this.lastAccessedAt = Instant.now();
    }

    public void incrementStep() {
        this.currentStep = (this.currentStep == null ? 0 : this.currentStep) + 1;
        addHistoryEntry("STEP_INCREMENT", "Advanced to step: " + this.currentStep);
        this.updatedAt = Instant.now();
        this.lastAccessedAt = Instant.now();
    }

    public void addChildContext(String childContextId) {
        if (this.childContextIds == null) {
            this.childContextIds = new java.util.ArrayList<>();
        }
        this.childContextIds.add(childContextId);
        addHistoryEntry("CHILD_CONTEXT_ADDED", "Added child context: " + childContextId);
        this.updatedAt = Instant.now();
    }

    public void addHistoryEntry(String action, String description) {
        if (this.history == null) {
            this.history = new java.util.ArrayList<>();
        }
        this.history.add(new ContextHistoryEntry(action, description, Instant.now()));
    }

    public double getProgressPercentage() {
        if (totalSteps == null || totalSteps == 0) return 0.0;
        return (currentStep == null ? 0.0 : currentStep.doubleValue()) / totalSteps.doubleValue() * 100.0;
    }

    @Override
    public String toString() {
        return "ExecutionContextDocument{" +
                "id='" + id + '\'' +
                ", planId='" + planId + '\'' +
                ", agentId='" + agentId + '\'' +
                ", currentStep=" + currentStep +
                ", totalSteps=" + totalSteps +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }

    // Inner class for history entries
    public static class ContextHistoryEntry {
        @PropertyName("action")
        private String action;
        
        @PropertyName("description")
        private String description;
        
        @PropertyName("timestamp")
        private Instant timestamp;
        
        @PropertyName("metadata")
        private Map<String, Object> metadata;

        public ContextHistoryEntry() {}

        public ContextHistoryEntry(String action, String description, Instant timestamp) {
            this.action = action;
            this.description = description;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        @Override
        public String toString() {
            return "ContextHistoryEntry{" +
                    "action='" + action + '\'' +
                    ", description='" + description + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}