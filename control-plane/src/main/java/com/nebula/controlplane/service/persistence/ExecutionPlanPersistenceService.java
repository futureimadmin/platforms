package com.nebula.controlplane.service.persistence;

import com.nebula.controlplane.repository.ExecutionPlanRepository;
import com.nebula.shared.domain.ExecutionPlanDocument;
import com.nebula.shared.domain.ExecutionStepDocument;
import com.nebula.shared.enums.ExecutionStatus;
import com.nebula.shared.model.ExecutionPlanStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Service for ExecutionPlan persistence operations
 */
@Service
public class ExecutionPlanPersistenceService {

    private static final Logger logger = LoggerFactory.getLogger(ExecutionPlanPersistenceService.class);

    private final ExecutionPlanRepository executionPlanRepository;

    @Autowired
    public ExecutionPlanPersistenceService(ExecutionPlanRepository executionPlanRepository) {
        this.executionPlanRepository = executionPlanRepository;
    }

    /**
     * Create a new execution plan
     */
    public ExecutionPlanDocument createExecutionPlan(String name, String description, String userPrompt, String createdBy) 
            throws ExecutionException, InterruptedException {
        logger.info("Creating new execution plan: {}", name);
        
        ExecutionPlanDocument planDoc = new ExecutionPlanDocument(name, description, userPrompt, createdBy);
        planDoc.setCreatedAt(Instant.now());
        planDoc.setUpdatedAt(Instant.now());
        planDoc.setStatus(ExecutionStatus.PENDING);
        
        return executionPlanRepository.save(planDoc);
    }

    /**
     * Update an existing execution plan
     */
    public ExecutionPlanDocument updateExecutionPlan(ExecutionPlanDocument planDoc) 
            throws ExecutionException, InterruptedException {
        logger.info("Updating execution plan: {}", planDoc.getId());
        
        planDoc.setUpdatedAt(Instant.now());
        return executionPlanRepository.save(planDoc);
    }

    /**
     * Get execution plan by ID
     */
    public Optional<ExecutionPlanDocument> getExecutionPlanById(String planId) 
            throws ExecutionException, InterruptedException {
        logger.debug("Retrieving execution plan by ID: {}", planId);
        return executionPlanRepository.findById(planId);
    }

    /**
     * Get all execution plans
     */
    public List<ExecutionPlanDocument> getAllExecutionPlans() throws ExecutionException, InterruptedException {
        logger.debug("Retrieving all execution plans");
        return executionPlanRepository.findAll();
    }

    /**
     * Get execution plans by status
     */
    public List<ExecutionPlanDocument> getExecutionPlansByStatus(ExecutionStatus status) 
            throws ExecutionException, InterruptedException {
        logger.debug("Retrieving execution plans by status: {}", status);
        return executionPlanRepository.findByStatus(status);
    }

    /**
     * Get execution plans by creator
     */
    public List<ExecutionPlanDocument> getExecutionPlansByCreator(String createdBy) 
            throws ExecutionException, InterruptedException {
        logger.debug("Retrieving execution plans by creator: {}", createdBy);
        return executionPlanRepository.findByCreatedBy(createdBy);
    }

    /**
     * Get active execution plans
     */
    public List<ExecutionPlanDocument> getActiveExecutionPlans() throws ExecutionException, InterruptedException {
        logger.debug("Retrieving active execution plans");
        return executionPlanRepository.findActivePlans();
    }

    /**
     * Get execution plans within time range
     */
    public List<ExecutionPlanDocument> getExecutionPlansInTimeRange(Instant startTime, Instant endTime) 
            throws ExecutionException, InterruptedException {
        logger.debug("Retrieving execution plans between {} and {}", startTime, endTime);
        return executionPlanRepository.findByCreatedAtBetween(startTime, endTime);
    }

    /**
     * Start execution plan
     */
    public void startExecutionPlan(String planId) throws ExecutionException, InterruptedException {
        logger.info("Starting execution plan: {}", planId);
        executionPlanRepository.setStartTime(planId, Instant.now());
    }

    /**
     * Complete execution plan
     */
    public void completeExecutionPlan(String planId) throws ExecutionException, InterruptedException {
        logger.info("Completing execution plan: {}", planId);
        executionPlanRepository.updateStatus(planId, ExecutionStatus.COMPLETED);
        executionPlanRepository.setEndTime(planId, Instant.now());
    }

    /**
     * Fail execution plan
     */
    public void failExecutionPlan(String planId) throws ExecutionException, InterruptedException {
        logger.info("Failing execution plan: {}", planId);
        executionPlanRepository.updateStatus(planId, ExecutionStatus.FAILED);
        executionPlanRepository.setEndTime(planId, Instant.now());
    }

    /**
     * Pause execution plan
     */
    public void pauseExecutionPlan(String planId) throws ExecutionException, InterruptedException {
        logger.info("Pausing execution plan: {}", planId);
        executionPlanRepository.updateStatus(planId, ExecutionStatus.PAUSED);
    }

    /**
     * Resume execution plan
     */
    public void resumeExecutionPlan(String planId) throws ExecutionException, InterruptedException {
        logger.info("Resuming execution plan: {}", planId);
        executionPlanRepository.updateStatus(planId, ExecutionStatus.RUNNING);
    }

    /**
     * Cancel execution plan
     */
    public void cancelExecutionPlan(String planId) throws ExecutionException, InterruptedException {
        logger.info("Cancelling execution plan: {}", planId);
        executionPlanRepository.updateStatus(planId, ExecutionStatus.CANCELLED);
        executionPlanRepository.setEndTime(planId, Instant.now());
    }

    /**
     * Update execution plan progress
     */
    public void updateExecutionPlanProgress(String planId, int completedSteps, int currentStepIndex) 
            throws ExecutionException, InterruptedException {
        logger.debug("Updating progress for plan: {} - completed: {}, current: {}", 
                planId, completedSteps, currentStepIndex);
        executionPlanRepository.updateProgress(planId, completedSteps, currentStepIndex);
    }

    /**
     * Update active agents count
     */
    public void updateActiveAgentsCount(String planId, int activeAgents) 
            throws ExecutionException, InterruptedException {
        logger.debug("Updating active agents count for plan: {} to {}", planId, activeAgents);
        executionPlanRepository.updateActiveAgentsCount(planId, activeAgents);
    }

    /**
     * Add execution step to plan
     */
    public void addExecutionStep(String planId, ExecutionStepDocument step) 
            throws ExecutionException, InterruptedException {
        logger.debug("Adding execution step to plan: {}", planId);
        
        Optional<ExecutionPlanDocument> planOpt = executionPlanRepository.findById(planId);
        if (planOpt.isPresent()) {
            ExecutionPlanDocument plan = planOpt.get();
            if (plan.getSteps() == null) {
                plan.setSteps(new java.util.ArrayList<>());
            }
            plan.getSteps().add(step);
            plan.setTotalSteps(plan.getSteps().size());
            executionPlanRepository.save(plan);
        }
    }

    /**
     * Update execution step status
     */
    public void updateExecutionStepStatus(String planId, int stepIndex, ExecutionStatus status, String output) 
            throws ExecutionException, InterruptedException {
        logger.debug("Updating step {} status in plan: {} to {}", stepIndex, planId, status);
        
        Optional<ExecutionPlanDocument> planOpt = executionPlanRepository.findById(planId);
        if (planOpt.isPresent()) {
            ExecutionPlanDocument plan = planOpt.get();
            if (plan.getSteps() != null && stepIndex < plan.getSteps().size()) {
                ExecutionStepDocument step = plan.getSteps().get(stepIndex);
                step.setStatus(status);
                if (output != null) {
                    step.setOutput(output);
                }
                
                // Update plan progress if step is completed
                if (status == ExecutionStatus.COMPLETED) {
                    plan.incrementCompletedSteps();
                }
                
                executionPlanRepository.save(plan);
            }
        }
    }

    /**
     * Get execution plan status
     */
    public ExecutionPlanStatus getExecutionPlanStatus(String planId) throws ExecutionException, InterruptedException {
        logger.debug("Getting execution plan status for: {}", planId);
        
        Optional<ExecutionPlanDocument> planOpt = executionPlanRepository.findById(planId);
        if (planOpt.isPresent()) {
            ExecutionPlanDocument plan = planOpt.get();
            return convertToStatus(plan);
        }
        
        return null;
    }

    /**
     * Delete execution plan
     */
    public void deleteExecutionPlan(String planId) throws ExecutionException, InterruptedException {
        logger.info("Deleting execution plan: {}", planId);
        executionPlanRepository.deleteById(planId);
    }

    /**
     * Check if execution plan exists
     */
    public boolean executionPlanExists(String planId) throws ExecutionException, InterruptedException {
        return executionPlanRepository.existsById(planId);
    }

    /**
     * Get execution plan count
     */
    public long getExecutionPlanCount() throws ExecutionException, InterruptedException {
        return executionPlanRepository.count();
    }

    /**
     * Get execution plan count by status
     */
    public long getExecutionPlanCountByStatus(ExecutionStatus status) throws ExecutionException, InterruptedException {
        return executionPlanRepository.countByStatus(status);
    }

    /**
     * Convert ExecutionPlanDocument to ExecutionPlanStatus
     */
    private ExecutionPlanStatus convertToStatus(ExecutionPlanDocument doc) {
        ExecutionPlanStatus status = new ExecutionPlanStatus();
        status.setPlanId(doc.getId());
        status.setStatus(doc.getStatus().name());
        status.setTotalSteps(doc.getTotalSteps() != null ? doc.getTotalSteps() : 0);
        status.setCompletedSteps(doc.getCompletedSteps() != null ? doc.getCompletedSteps() : 0);
        status.setActiveAgents(doc.getActiveAgents() != null ? doc.getActiveAgents() : 0);
        status.setTotalAgents(doc.getTotalAgents() != null ? doc.getTotalAgents() : 0);
        // Convert Instant to LocalDateTime
        status.setCreatedAt(doc.getCreatedAt() != null ? 
            java.time.LocalDateTime.ofInstant(doc.getCreatedAt(), java.time.ZoneId.systemDefault()) : null);
        status.setUpdatedAt(doc.getUpdatedAt() != null ? 
            java.time.LocalDateTime.ofInstant(doc.getUpdatedAt(), java.time.ZoneId.systemDefault()) : null);
        status.setStartedAt(doc.getStartTime() != null ? 
            java.time.LocalDateTime.ofInstant(doc.getStartTime(), java.time.ZoneId.systemDefault()) : null);
        return status;
    }

    /**
     * Get execution plan statistics
     */
    public ExecutionPlanStatistics getExecutionPlanStatistics() throws ExecutionException, InterruptedException {
        logger.debug("Calculating execution plan statistics");
        
        long totalPlans = executionPlanRepository.count();
        long pendingPlans = executionPlanRepository.countByStatus(ExecutionStatus.PENDING);
        long runningPlans = executionPlanRepository.countByStatus(ExecutionStatus.RUNNING);
        long completedPlans = executionPlanRepository.countByStatus(ExecutionStatus.COMPLETED);
        long failedPlans = executionPlanRepository.countByStatus(ExecutionStatus.FAILED);
        long pausedPlans = executionPlanRepository.countByStatus(ExecutionStatus.PAUSED);
        long cancelledPlans = executionPlanRepository.countByStatus(ExecutionStatus.CANCELLED);
        
        return new ExecutionPlanStatistics(totalPlans, pendingPlans, runningPlans, 
                completedPlans, failedPlans, pausedPlans, cancelledPlans);
    }

    /**
     * Execution plan statistics data class
     */
    public static class ExecutionPlanStatistics {
        private final long totalPlans;
        private final long pendingPlans;
        private final long runningPlans;
        private final long completedPlans;
        private final long failedPlans;
        private final long pausedPlans;
        private final long cancelledPlans;

        public ExecutionPlanStatistics(long totalPlans, long pendingPlans, long runningPlans, 
                                     long completedPlans, long failedPlans, long pausedPlans, long cancelledPlans) {
            this.totalPlans = totalPlans;
            this.pendingPlans = pendingPlans;
            this.runningPlans = runningPlans;
            this.completedPlans = completedPlans;
            this.failedPlans = failedPlans;
            this.pausedPlans = pausedPlans;
            this.cancelledPlans = cancelledPlans;
        }

        // Getters
        public long getTotalPlans() { return totalPlans; }
        public long getPendingPlans() { return pendingPlans; }
        public long getRunningPlans() { return runningPlans; }
        public long getCompletedPlans() { return completedPlans; }
        public long getFailedPlans() { return failedPlans; }
        public long getPausedPlans() { return pausedPlans; }
        public long getCancelledPlans() { return cancelledPlans; }
        
        public double getSuccessRate() {
            if (totalPlans == 0) return 0.0;
            return (double) completedPlans / totalPlans * 100.0;
        }
        
        public double getFailureRate() {
            if (totalPlans == 0) return 0.0;
            return (double) failedPlans / totalPlans * 100.0;
        }
    }
}