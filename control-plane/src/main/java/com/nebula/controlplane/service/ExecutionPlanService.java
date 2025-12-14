package com.nebula.controlplane.service;

import com.nebula.shared.model.ExecutionPlan;
import com.nebula.shared.model.Agent;
import com.nebula.shared.enums.ExecutionFlowType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing execution plans
 */
@Service
public class ExecutionPlanService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExecutionPlanService.class);
    
    // In-memory storage for execution plans (replace with database in production)
    private final Map<String, ExecutionPlan> executionPlans = new ConcurrentHashMap<>();
    private final Map<String, ExecutionPlanStatus> executionStatuses = new ConcurrentHashMap<>();
    
    /**
     * Create a new execution plan
     */
    public ExecutionPlan createExecutionPlan(ExecutionPlan plan) {
        logger.info("Creating execution plan: {}", plan.getName());
        
        // Generate unique plan ID if not provided
        if (plan.getPlanId() == null || plan.getPlanId().isEmpty()) {
            plan.setPlanId(generatePlanId());
        }
        
        // Set metadata
        if (plan.getMetadata() == null) {
            plan.setMetadata(new HashMap<>());
        }
        plan.getMetadata().put("createdAt", LocalDateTime.now().toString());
        plan.getMetadata().put("status", "CREATED");
        
        // Store the plan
        executionPlans.put(plan.getPlanId(), plan);
        
        // Initialize execution status
        ExecutionPlanStatus status = new ExecutionPlanStatus();
        status.setPlanId(plan.getPlanId());
        status.setStatus("CREATED");
        status.setCreatedAt(LocalDateTime.now());
        status.setTotalSteps(countTotalSteps(plan));
        status.setCompletedSteps(0);
        status.setActiveAgents(0);
        status.setTotalAgents(plan.getAgents() != null ? plan.getAgents().size() : 0);
        
        executionStatuses.put(plan.getPlanId(), status);
        
        logger.info("Execution plan created successfully: {}", plan.getPlanId());
        return plan;
    }
    
    /**
     * Get execution plan by ID
     */
    public ExecutionPlan getExecutionPlan(String planId) {
        logger.debug("Retrieving execution plan: {}", planId);
        return executionPlans.get(planId);
    }
    
    /**
     * Get all execution plans
     */
    public List<ExecutionPlan> getAllExecutionPlans() {
        logger.debug("Retrieving all execution plans");
        return new ArrayList<>(executionPlans.values());
    }
    
    /**
     * Update execution plan
     */
    public ExecutionPlan updateExecutionPlan(String planId, ExecutionPlan updatedPlan) {
        logger.info("Updating execution plan: {}", planId);
        
        ExecutionPlan existingPlan = executionPlans.get(planId);
        if (existingPlan == null) {
            throw new RuntimeException("Execution plan not found: " + planId);
        }
        
        // Update fields
        updatedPlan.setPlanId(planId);
        if (updatedPlan.getMetadata() == null) {
            updatedPlan.setMetadata(existingPlan.getMetadata());
        }
        updatedPlan.getMetadata().put("updatedAt", LocalDateTime.now().toString());
        
        executionPlans.put(planId, updatedPlan);
        
        logger.info("Execution plan updated successfully: {}", planId);
        return updatedPlan;
    }
    
    /**
     * Delete execution plan
     */
    public boolean deleteExecutionPlan(String planId) {
        logger.info("Deleting execution plan: {}", planId);
        
        ExecutionPlan removed = executionPlans.remove(planId);
        executionStatuses.remove(planId);
        
        boolean success = removed != null;
        logger.info("Execution plan deletion {}: {}", success ? "successful" : "failed", planId);
        return success;
    }
    
    /**
     * Get execution plan status
     */
    public ExecutionPlanStatus getExecutionStatus(String planId) {
        logger.debug("Retrieving execution status: {}", planId);
        return executionStatuses.get(planId);
    }
    
    /**
     * Update execution status
     */
    public void updateExecutionStatus(String planId, String status) {
        logger.info("Updating execution status for plan {}: {}", planId, status);
        
        ExecutionPlanStatus planStatus = executionStatuses.get(planId);
        if (planStatus != null) {
            planStatus.setStatus(status);
            planStatus.setUpdatedAt(LocalDateTime.now());
            
            if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
                planStatus.setCompletedAt(LocalDateTime.now());
            }
        }
    }
    
    /**
     * Update step completion
     */
    public void updateStepCompletion(String planId, String stepId, boolean completed) {
        logger.debug("Updating step completion for plan {}, step {}: {}", planId, stepId, completed);
        
        ExecutionPlanStatus status = executionStatuses.get(planId);
        if (status != null && completed) {
            status.setCompletedSteps(status.getCompletedSteps() + 1);
            status.setCurrentStep(stepId);
            status.setUpdatedAt(LocalDateTime.now());
        }
    }
    
    /**
     * Get active execution plans
     */
    public List<ExecutionPlanStatus> getActiveExecutions() {
        logger.debug("Retrieving active executions");
        
        return executionStatuses.values().stream()
                .filter(status -> "RUNNING".equals(status.getStatus()) || 
                                "PAUSED".equals(status.getStatus()) ||
                                "WAITING_FOR_APPROVAL".equals(status.getStatus()))
                .toList();
    }
    
    /**
     * Validate execution plan
     */
    public boolean validateExecutionPlan(ExecutionPlan plan) {
        logger.debug("Validating execution plan: {}", plan.getName());
        
        if (plan.getName() == null || plan.getName().trim().isEmpty()) {
            logger.error("Execution plan name is required");
            return false;
        }
        
        if (plan.getExecutionFlow() == null) {
            logger.error("Execution flow is required");
            return false;
        }
        
        if (plan.getAgents() == null || plan.getAgents().isEmpty()) {
            logger.error("At least one agent is required");
            return false;
        }
        
        logger.debug("Execution plan validation successful");
        return true;
    }
    
    /**
     * Generate unique plan ID
     */
    private String generatePlanId() {
        return "plan-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * Count total steps in execution plan
     */
    private int countTotalSteps(ExecutionPlan plan) {
        if (plan.getExecutionFlow() == null) {
            return 0;
        }
        
        // Simple count - in real implementation, this would recursively count all steps
        return plan.getExecutionFlow().getSteps() != null ? plan.getExecutionFlow().getSteps().size() : 0;
    }
    
    /**
     * Inner class for execution plan status
     */
    public static class ExecutionPlanStatus {
        private String planId;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime completedAt;
        private int totalSteps;
        private int completedSteps;
        private int activeAgents;
        private int totalAgents;
        private String currentStep;
        private String errorMessage;
        
        // Getters and setters
        public String getPlanId() { return planId; }
        public void setPlanId(String planId) { this.planId = planId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        
        public LocalDateTime getCompletedAt() { return completedAt; }
        public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
        
        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
        
        public int getCompletedSteps() { return completedSteps; }
        public void setCompletedSteps(int completedSteps) { this.completedSteps = completedSteps; }
        
        public int getActiveAgents() { return activeAgents; }
        public void setActiveAgents(int activeAgents) { this.activeAgents = activeAgents; }
        
        public int getTotalAgents() { return totalAgents; }
        public void setTotalAgents(int totalAgents) { this.totalAgents = totalAgents; }
        
        public String getCurrentStep() { return currentStep; }
        public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}