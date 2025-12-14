package com.nebula.shared.domain;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.nebula.shared.enums.ExecutionStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Firestore document for ExecutionPlan persistence
 */
public class ExecutionPlanDocument {
    
    @DocumentId
    private String id;
    
    @PropertyName("name")
    private String name;
    
    @PropertyName("description")
    private String description;
    
    @PropertyName("userPrompt")
    private String userPrompt;
    
    @PropertyName("status")
    private ExecutionStatus status;
    
    @PropertyName("totalSteps")
    private Integer totalSteps;
    
    @PropertyName("completedSteps")
    private Integer completedSteps;
    
    @PropertyName("currentStepIndex")
    private Integer currentStepIndex;
    
    @PropertyName("activeAgents")
    private Integer activeAgents;
    
    @PropertyName("totalAgents")
    private Integer totalAgents;
    
    @PropertyName("steps")
    private List<ExecutionStepDocument> steps;
    
    @PropertyName("agentIds")
    private List<String> agentIds;
    
    @PropertyName("createdAt")
    private Instant createdAt;
    
    @PropertyName("updatedAt")
    private Instant updatedAt;
    
    @PropertyName("startTime")
    private Instant startTime;
    
    @PropertyName("endTime")
    private Instant endTime;
    
    @PropertyName("createdBy")
    private String createdBy;
    
    @PropertyName("priority")
    private Integer priority;
    
    @PropertyName("estimatedDuration")
    private Long estimatedDurationMinutes;
    
    @PropertyName("actualDuration")
    private Long actualDurationMinutes;
    
    @PropertyName("metadata")
    private Map<String, Object> metadata;
    
    @PropertyName("configuration")
    private Map<String, Object> configuration;

    // Default constructor for Firestore
    public ExecutionPlanDocument() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = ExecutionStatus.PENDING;
        this.completedSteps = 0;
        this.currentStepIndex = 0;
        this.activeAgents = 0;
        this.totalAgents = 0;
        this.priority = 5; // Default medium priority
    }

    // Constructor with required fields
    public ExecutionPlanDocument(String name, String description, String userPrompt, String createdBy) {
        this();
        this.name = name;
        this.description = description;
        this.userPrompt = userPrompt;
        this.createdBy = createdBy;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public String getUserPrompt() {
        return userPrompt;
    }

    public void setUserPrompt(String userPrompt) {
        this.userPrompt = userPrompt;
        this.updatedAt = Instant.now();
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
        
        // Set start/end times based on status
        if (status == ExecutionStatus.RUNNING && this.startTime == null) {
            this.startTime = Instant.now();
        } else if ((status == ExecutionStatus.COMPLETED || status == ExecutionStatus.FAILED) && this.endTime == null) {
            this.endTime = Instant.now();
            calculateActualDuration();
        }
    }

    public Integer getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Integer totalSteps) {
        this.totalSteps = totalSteps;
        this.updatedAt = Instant.now();
    }

    public Integer getCompletedSteps() {
        return completedSteps;
    }

    public void setCompletedSteps(Integer completedSteps) {
        this.completedSteps = completedSteps;
        this.updatedAt = Instant.now();
    }

    public Integer getCurrentStepIndex() {
        return currentStepIndex;
    }

    public void setCurrentStepIndex(Integer currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
        this.updatedAt = Instant.now();
    }

    public Integer getActiveAgents() {
        return activeAgents;
    }

    public void setActiveAgents(Integer activeAgents) {
        this.activeAgents = activeAgents;
        this.updatedAt = Instant.now();
    }

    public Integer getTotalAgents() {
        return totalAgents;
    }

    public void setTotalAgents(Integer totalAgents) {
        this.totalAgents = totalAgents;
        this.updatedAt = Instant.now();
    }

    public List<ExecutionStepDocument> getSteps() {
        return steps;
    }

    public void setSteps(List<ExecutionStepDocument> steps) {
        this.steps = steps;
        this.totalSteps = steps != null ? steps.size() : 0;
        this.updatedAt = Instant.now();
    }

    public List<String> getAgentIds() {
        return agentIds;
    }

    public void setAgentIds(List<String> agentIds) {
        this.agentIds = agentIds;
        this.totalAgents = agentIds != null ? agentIds.size() : 0;
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

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
        this.updatedAt = Instant.now();
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
        this.updatedAt = Instant.now();
        calculateActualDuration();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
        this.updatedAt = Instant.now();
    }

    public Long getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(Long estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.updatedAt = Instant.now();
    }

    public Long getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public void setActualDurationMinutes(Long actualDurationMinutes) {
        this.actualDurationMinutes = actualDurationMinutes;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        this.updatedAt = Instant.now();
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
        this.updatedAt = Instant.now();
    }

    // Utility methods
    public void incrementCompletedSteps() {
        this.completedSteps = (this.completedSteps == null ? 0 : this.completedSteps) + 1;
        this.updatedAt = Instant.now();
        
        // Check if plan is completed
        if (this.totalSteps != null && this.completedSteps.equals(this.totalSteps)) {
            setStatus(ExecutionStatus.COMPLETED);
        }
    }

    public void incrementCurrentStep() {
        this.currentStepIndex = (this.currentStepIndex == null ? 0 : this.currentStepIndex) + 1;
        this.updatedAt = Instant.now();
    }

    public double getProgressPercentage() {
        if (totalSteps == null || totalSteps == 0) return 0.0;
        return (completedSteps == null ? 0.0 : completedSteps.doubleValue()) / totalSteps.doubleValue() * 100.0;
    }

    private void calculateActualDuration() {
        if (startTime != null && endTime != null) {
            this.actualDurationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }

    @Override
    public String toString() {
        return "ExecutionPlanDocument{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", completedSteps=" + completedSteps +
                ", totalSteps=" + totalSteps +
                ", createdAt=" + createdAt +
                '}';
    }
}