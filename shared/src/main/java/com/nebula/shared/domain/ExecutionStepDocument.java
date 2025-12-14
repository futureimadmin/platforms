package com.nebula.shared.domain;

import com.google.cloud.firestore.annotation.PropertyName;
import com.nebula.shared.enums.ExecutionStatus;

import java.time.Instant;
import java.util.Map;

/**
 * Firestore subdocument for ExecutionStep persistence
 */
public class ExecutionStepDocument {
    
    @PropertyName("stepIndex")
    private Integer stepIndex;
    
    @PropertyName("name")
    private String name;
    
    @PropertyName("description")
    private String description;
    
    @PropertyName("status")
    private ExecutionStatus status;
    
    @PropertyName("assignedAgentId")
    private String assignedAgentId;
    
    @PropertyName("input")
    private String input;
    
    @PropertyName("output")
    private String output;
    
    @PropertyName("errorMessage")
    private String errorMessage;
    
    @PropertyName("startTime")
    private Instant startTime;
    
    @PropertyName("endTime")
    private Instant endTime;
    
    @PropertyName("estimatedDurationMinutes")
    private Long estimatedDurationMinutes;
    
    @PropertyName("actualDurationMinutes")
    private Long actualDurationMinutes;
    
    @PropertyName("retryCount")
    private Integer retryCount;
    
    @PropertyName("maxRetries")
    private Integer maxRetries;
    
    @PropertyName("dependencies")
    private java.util.List<Integer> dependencies;
    
    @PropertyName("metadata")
    private Map<String, Object> metadata;

    // Default constructor for Firestore
    public ExecutionStepDocument() {
        this.status = ExecutionStatus.PENDING;
        this.retryCount = 0;
        this.maxRetries = 3;
    }

    // Constructor with required fields
    public ExecutionStepDocument(Integer stepIndex, String name, String description) {
        this();
        this.stepIndex = stepIndex;
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Integer getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(Integer stepIndex) {
        this.stepIndex = stepIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
        
        // Set start/end times based on status
        if (status == ExecutionStatus.RUNNING && this.startTime == null) {
            this.startTime = Instant.now();
        } else if ((status == ExecutionStatus.COMPLETED || status == ExecutionStatus.FAILED) && this.endTime == null) {
            this.endTime = Instant.now();
            calculateActualDuration();
        }
    }

    public String getAssignedAgentId() {
        return assignedAgentId;
    }

    public void setAssignedAgentId(String assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
        calculateActualDuration();
    }

    public Long getEstimatedDurationMinutes() {
        return estimatedDurationMinutes;
    }

    public void setEstimatedDurationMinutes(Long estimatedDurationMinutes) {
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public Long getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public void setActualDurationMinutes(Long actualDurationMinutes) {
        this.actualDurationMinutes = actualDurationMinutes;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public java.util.List<Integer> getDependencies() {
        return dependencies;
    }

    public void setDependencies(java.util.List<Integer> dependencies) {
        this.dependencies = dependencies;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    // Utility methods
    public void incrementRetryCount() {
        this.retryCount = (this.retryCount == null ? 0 : this.retryCount) + 1;
    }

    public boolean canRetry() {
        return this.retryCount == null || this.maxRetries == null || this.retryCount < this.maxRetries;
    }

    public void markAsCompleted(String output) {
        this.output = output;
        setStatus(ExecutionStatus.COMPLETED);
    }

    public void markAsFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        setStatus(ExecutionStatus.FAILED);
    }

    private void calculateActualDuration() {
        if (startTime != null && endTime != null) {
            this.actualDurationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        }
    }

    @Override
    public String toString() {
        return "ExecutionStepDocument{" +
                "stepIndex=" + stepIndex +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", assignedAgentId='" + assignedAgentId + '\'' +
                '}';
    }
}