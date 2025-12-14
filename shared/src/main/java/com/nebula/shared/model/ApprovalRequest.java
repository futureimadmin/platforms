package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a human approval request for execution steps.
 */
public class ApprovalRequest {
    
    @NotBlank
    @JsonProperty("requestId")
    private String requestId;
    
    @NotBlank
    @JsonProperty("planId")
    private String planId;
    
    @NotBlank
    @JsonProperty("stepId")
    private String stepId;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("context")
    private Map<String, Object> context;
    
    @JsonProperty("status")
    private String status; // PENDING, APPROVED, REJECTED, TIMEOUT
    
    @JsonProperty("requestedAt")
    private LocalDateTime requestedAt;
    
    @JsonProperty("respondedAt")
    private LocalDateTime respondedAt;
    
    @JsonProperty("approvedBy")
    private String approvedBy;
    
    @JsonProperty("comments")
    private String comments;
    
    // Constructors
    public ApprovalRequest() {
        this.requestedAt = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public ApprovalRequest(String requestId, String planId, String stepId, String description) {
        this();
        this.requestId = requestId;
        this.planId = planId;
        this.stepId = stepId;
        this.description = description;
    }
    
    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getPlanId() { return planId; }
    public void setPlanId(String planId) { this.planId = planId; }
    
    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Map<String, Object> getContext() { return context; }
    public void setContext(Map<String, Object> context) { this.context = context; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
    
    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}