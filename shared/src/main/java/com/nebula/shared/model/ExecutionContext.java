package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the execution context for a running execution plan.
 */
public class ExecutionContext {
    
    @NotBlank
    @JsonProperty("planId")
    private String planId;
    
    @JsonProperty("plan")
    private ExecutionPlan plan;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("startTime")
    private LocalDateTime startTime;
    
    @JsonProperty("sharedData")
    private Map<String, Object> sharedData;
    
    @JsonProperty("currentStepId")
    private String currentStepId;
    
    @JsonProperty("completedSteps")
    private Map<String, Object> completedSteps;
    
    @JsonProperty("completedStepsCount")
    private int completedStepsCount;
    
    @JsonProperty("totalSteps")
    private int totalSteps;
    
    @JsonProperty("activeAgents")
    private int activeAgents;
    
    @JsonProperty("currentStep")
    private String currentStep;
    
    // Constructors
    public ExecutionContext() {
        this.sharedData = new ConcurrentHashMap<>();
        this.completedSteps = new ConcurrentHashMap<>();
    }
    
    public ExecutionContext(String planId, ExecutionPlan plan) {
        this();
        this.planId = planId;
        this.plan = plan;
        this.status = "CREATED";
        this.startTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    
    public ExecutionPlan getPlan() { return plan; }
    public void setPlan(ExecutionPlan plan) { this.plan = plan; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public Map<String, Object> getSharedData() { return sharedData; }
    public void setSharedData(Map<String, Object> sharedData) { this.sharedData = sharedData; }
    
    public String getCurrentStepId() { return currentStepId; }
    public void setCurrentStepId(String currentStepId) { this.currentStepId = currentStepId; }
    
    public Map<String, Object> getCompletedSteps() { return completedSteps; }
    public void setCompletedSteps(Map<String, Object> completedSteps) { this.completedSteps = completedSteps; }
    
    // Utility methods
    public void addSharedData(String key, Object value) {
        this.sharedData.put(key, value);
    }
    
    public Object getSharedData(String key) {
        return this.sharedData.get(key);
    }
    
    public void markStepCompleted(String stepId, Object result) {
        this.completedSteps.put(stepId, result);
    }
    
    public boolean isStepCompleted(String stepId) {
        return this.completedSteps.containsKey(stepId);
    }
    
    // Additional getters and setters for new fields
    public int getCompletedStepsCount() { return completedStepsCount; }
    public void setCompletedStepsCount(int completedStepsCount) { this.completedStepsCount = completedStepsCount; }
    public void incrementCompletedSteps() { this.completedStepsCount++; }
    
    public int getTotalSteps() { return totalSteps; }
    public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
    
    public int getActiveAgents() { return activeAgents; }
    public void setActiveAgents(int activeAgents) { this.activeAgents = activeAgents; }
    
    public String getCurrentStep() { return currentStep; }
    public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
}