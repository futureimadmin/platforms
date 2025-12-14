package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents the result of an execution plan execution.
 */
public class ExecutionResult {
    
    @NotBlank
    @JsonProperty("planId")
    private String planId;
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("errorMessage")
    private String errorMessage;
    
    @JsonProperty("startedAt")
    private LocalDateTime startedAt;
    
    @JsonProperty("completedAt")
    private LocalDateTime completedAt;
    
    @JsonProperty("results")
    private Map<String, Object> results;
    
    @JsonProperty("executionTime")
    private Long executionTime; // in milliseconds
    
    // Constructors
    public ExecutionResult() {}
    
    public ExecutionResult(String planId, boolean success) {
        this.planId = planId;
        this.success = success;
    }
    
    // Getters and Setters
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public Map<String, Object> getResults() { return results; }
    public void setResults(Map<String, Object> results) { this.results = results; }
    
    public Long getExecutionTime() { return executionTime; }
    public void setExecutionTime(Long executionTime) { this.executionTime = executionTime; }
}