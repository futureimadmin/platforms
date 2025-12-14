package com.nebula.shared.domain;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.nebula.shared.enums.AgentType;
import com.nebula.shared.enums.ToolType;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Firestore document for Agent persistence
 */
public class AgentDocument {
    
    @DocumentId
    private String id;
    
    @PropertyName("name")
    private String name;
    
    @PropertyName("type")
    private AgentType type;
    
    @PropertyName("description")
    private String description;
    
    @PropertyName("capabilities")
    private List<String> capabilities;
    
    @PropertyName("tools")
    private List<ToolType> tools;
    
    @PropertyName("planId")
    private String planId;
    
    @PropertyName("status")
    private String status;
    
    @PropertyName("configuration")
    private Map<String, Object> configuration;
    
    @PropertyName("createdAt")
    private Instant createdAt;
    
    @PropertyName("updatedAt")
    private Instant updatedAt;
    
    @PropertyName("createdBy")
    private String createdBy;
    
    @PropertyName("lastActiveAt")
    private Instant lastActiveAt;
    
    @PropertyName("executionCount")
    private Long executionCount;
    
    @PropertyName("successRate")
    private Double successRate;
    
    @PropertyName("metadata")
    private Map<String, Object> metadata;

    // Default constructor for Firestore
    public AgentDocument() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.executionCount = 0L;
        this.successRate = 0.0;
    }

    // Constructor with required fields
    public AgentDocument(String name, AgentType type, String description, String planId) {
        this();
        this.name = name;
        this.type = type;
        this.description = description;
        this.planId = planId;
        this.status = "CREATED";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public AgentType getType() {
        return type;
    }

    public void setType(AgentType type) {
        this.type = type;
        this.updatedAt = Instant.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
        this.updatedAt = Instant.now();
    }

    public List<ToolType> getTools() {
        return tools;
    }

    public void setTools(List<ToolType> tools) {
        this.tools = tools;
        this.updatedAt = Instant.now();
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
        this.updatedAt = Instant.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        this.updatedAt = Instant.now();
    }

    public Instant getLastActiveAt() {
        return lastActiveAt;
    }

    public void setLastActiveAt(Instant lastActiveAt) {
        this.lastActiveAt = lastActiveAt;
        this.updatedAt = Instant.now();
    }

    public Long getExecutionCount() {
        return executionCount;
    }

    public void setExecutionCount(Long executionCount) {
        this.executionCount = executionCount;
        this.updatedAt = Instant.now();
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
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
    public void incrementExecutionCount() {
        this.executionCount = (this.executionCount == null ? 0L : this.executionCount) + 1;
        this.lastActiveAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateSuccessRate(boolean success) {
        // Simple success rate calculation - can be enhanced
        if (this.executionCount == null || this.executionCount == 0) {
            this.successRate = success ? 1.0 : 0.0;
        } else {
            double currentSuccesses = (this.successRate == null ? 0.0 : this.successRate) * this.executionCount;
            if (success) currentSuccesses++;
            this.successRate = currentSuccesses / (this.executionCount + 1);
        }
        this.updatedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "AgentDocument{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status='" + status + '\'' +
                ", planId='" + planId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}