package com.nebula.shared.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nebula.shared.model.ExecutionPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

/**
 * Represents the status document of an execution plan's execution.
 * Tracks the progress and current state of an execution plan.
 */
public class ExecutionPlanStatusDocument {

    @NotBlank
    @JsonProperty("planId")
    private String planId;

    @NotBlank
    @JsonProperty("status")
    private String status;

    @NotNull
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @PositiveOrZero
    @JsonProperty("totalSteps")
    private int totalSteps;

    @PositiveOrZero
    @JsonProperty("completedSteps")
    private int completedSteps;

    @PositiveOrZero
    @JsonProperty("activeAgents")
    private int activeAgents;

    @PositiveOrZero
    @JsonProperty("totalAgents")
    private int totalAgents;

    // Constructors
    public ExecutionPlanStatusDocument() {
    }

    public ExecutionPlanStatusDocument(String planId, String status, LocalDateTime createdAt, 
                                     int totalSteps, int completedSteps, 
                                     int activeAgents, int totalAgents) {
        this.planId = planId;
        this.status = status;
        this.createdAt = createdAt;
        this.totalSteps = totalSteps;
        this.completedSteps = completedSteps;
        this.activeAgents = activeAgents;
        this.totalAgents = totalAgents;
    }

    // Getters and Setters
    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public int getCompletedSteps() {
        return completedSteps;
    }

    public void setCompletedSteps(int completedSteps) {
        this.completedSteps = completedSteps;
    }

    public int getActiveAgents() {
        return activeAgents;
    }

    public void setActiveAgents(int activeAgents) {
        this.activeAgents = activeAgents;
    }

    public int getTotalAgents() {
        return totalAgents;
    }

    public void setTotalAgents(int totalAgents) {
        this.totalAgents = totalAgents;
    }

    /**
     * Creates a new ExecutionPlanStatusDocument with default values for a newly created execution plan.
     * @param plan The execution plan to create status for
     * @param totalSteps Total number of steps in the execution plan
     * @return A new ExecutionPlanStatusDocument with initialized values
     */
    public static ExecutionPlanStatusDocument createInitialStatus(ExecutionPlan plan, int totalSteps) {
        ExecutionPlanStatusDocument status = new ExecutionPlanStatusDocument();
        status.setPlanId(plan.getPlanId());
        status.setStatus("CREATED");
        status.setCreatedAt(LocalDateTime.now());
        status.setTotalSteps(totalSteps);
        status.setCompletedSteps(0);
        status.setActiveAgents(0);
        status.setTotalAgents(plan.getAgents() != null ? plan.getAgents().size() : 0);
        return status;
    }

    @Override
    public String toString() {
        return "ExecutionPlanStatusDocument{" +
               "planId='" + planId + '\'' +
               ", status='" + status + '\'' +
               ", createdAt=" + createdAt +
               ", totalSteps=" + totalSteps +
               ", completedSteps=" + completedSteps +
               ", activeAgents=" + activeAgents +
               ", totalAgents=" + totalAgents +
               '}';
    }
}
