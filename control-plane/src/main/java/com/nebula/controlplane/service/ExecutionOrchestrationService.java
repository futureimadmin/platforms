package com.nebula.controlplane.service;

import com.nebula.shared.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Service for orchestrating execution of agents and execution plans
 */
@Service
public class ExecutionOrchestrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExecutionOrchestrationService.class);
    
    @Autowired
    private ExecutionPlanService executionPlanService;
    
    @Autowired
    private HumanInTheLoopService humanInTheLoopService;
    
    // Thread pool for executing agents
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    // Active executions tracking
    private final Map<String, CompletableFuture<ExecutionResult>> activeExecutions = new ConcurrentHashMap<>();
    private final Map<String, ExecutionContext> executionContexts = new ConcurrentHashMap<>();
    
    /**
     * Start execution of an execution plan
     */
    public CompletableFuture<ExecutionResult> startExecution(String planId) {
        logger.info("Starting execution for plan: {}", planId);
        
        ExecutionPlan plan = executionPlanService.getExecutionPlan(planId);
        if (plan == null) {
            throw new RuntimeException("Execution plan not found: " + planId);
        }
        
        // Check if already executing
        if (activeExecutions.containsKey(planId)) {
            throw new RuntimeException("Execution already in progress for plan: " + planId);
        }
        
        // Create execution context
        ExecutionContext context = new ExecutionContext();
        context.setPlanId(planId);
        context.setPlan(plan);
        context.setStartTime(LocalDateTime.now());
        context.setStatus("RUNNING");
        context.setSharedData(new ConcurrentHashMap<>());
        
        // Set total steps
        if (plan.getExecutionFlow() != null && plan.getExecutionFlow().getSteps() != null) {
            context.setTotalSteps(plan.getExecutionFlow().getSteps().size());
        }
        
        executionContexts.put(planId, context);
        
        // Update plan status
        executionPlanService.updateExecutionStatus(planId, "RUNNING");
        
        // Start execution asynchronously
        CompletableFuture<ExecutionResult> executionFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return executeExecutionPlan(context);
            } catch (Exception e) {
                logger.error("Execution failed for plan {}: {}", planId, e.getMessage(), e);
                executionPlanService.updateExecutionStatus(planId, "FAILED");
                
                ExecutionResult result = new ExecutionResult();
                result.setPlanId(planId);
                result.setSuccess(false);
                result.setErrorMessage(e.getMessage());
                result.setCompletedAt(LocalDateTime.now());
                return result;
            }
        }, executorService);
        
        activeExecutions.put(planId, executionFuture);
        
        // Clean up when execution completes
        executionFuture.whenComplete((result, throwable) -> {
            activeExecutions.remove(planId);
            executionContexts.remove(planId);
        });
        
        logger.info("Execution started for plan: {}", planId);
        return executionFuture;
    }
    
    /**
     * Stop execution of a plan
     */
    public boolean stopExecution(String planId) {
        logger.info("Stopping execution for plan: {}", planId);
        
        CompletableFuture<ExecutionResult> executionFuture = activeExecutions.get(planId);
        if (executionFuture == null) {
            logger.warn("No active execution found for plan: {}", planId);
            return false;
        }
        
        // Cancel the execution
        boolean cancelled = executionFuture.cancel(true);
        
        if (cancelled) {
            executionPlanService.updateExecutionStatus(planId, "CANCELLED");
            activeExecutions.remove(planId);
            executionContexts.remove(planId);
        }
        
        logger.info("Execution stop {} for plan: {}", cancelled ? "successful" : "failed", planId);
        return cancelled;
    }
    
    /**
     * Pause execution of a plan
     */
    public boolean pauseExecution(String planId) {
        logger.info("Pausing execution for plan: {}", planId);
        
        ExecutionContext context = executionContexts.get(planId);
        if (context == null) {
            logger.warn("No execution context found for plan: {}", planId);
            return false;
        }
        
        context.setStatus("PAUSED");
        executionPlanService.updateExecutionStatus(planId, "PAUSED");
        
        logger.info("Execution paused for plan: {}", planId);
        return true;
    }
    
    /**
     * Resume execution of a plan
     */
    public boolean resumeExecution(String planId) {
        logger.info("Resuming execution for plan: {}", planId);
        
        ExecutionContext context = executionContexts.get(planId);
        if (context == null) {
            logger.warn("No execution context found for plan: {}", planId);
            return false;
        }
        
        context.setStatus("RUNNING");
        executionPlanService.updateExecutionStatus(planId, "RUNNING");
        
        logger.info("Execution resumed for plan: {}", planId);
        return true;
    }
    
    /**
     * Get execution status
     */
    public ExecutionPlanStatus getExecutionStatus(String planId) {
        logger.debug("Getting execution status for plan: {}", planId);
        
        ExecutionContext context = executionContexts.get(planId);
        if (context == null) {
            return null;
        }
        
        ExecutionPlanStatus status = new ExecutionPlanStatus();
        status.setPlanId(planId);
        status.setStatus(context.getStatus());
        status.setStartTime(context.getStartTime());
        status.setCurrentStep(context.getCurrentStep());
        status.setCompletedSteps(context.getCompletedStepsCount());
        status.setTotalSteps(context.getTotalSteps());
        status.setActiveAgents(context.getActiveAgents());
        
        return status;
    }
    
    /**
     * Execute the execution plan
     */
    private ExecutionResult executeExecutionPlan(ExecutionContext context) {
        logger.info("Executing plan: {}", context.getPlanId());
        
        ExecutionPlan plan = context.getPlan();
        ExecutionResult result = new ExecutionResult();
        result.setPlanId(context.getPlanId());
        result.setStartedAt(context.getStartTime());
        
        try {
            // Execute the main execution flow
            boolean success = executeExecutionFlow(plan.getExecutionFlow(), context);
            
            result.setSuccess(success);
            result.setCompletedAt(LocalDateTime.now());
            
            if (success) {
                executionPlanService.updateExecutionStatus(context.getPlanId(), "COMPLETED");
                logger.info("Execution completed successfully for plan: {}", context.getPlanId());
            } else {
                executionPlanService.updateExecutionStatus(context.getPlanId(), "FAILED");
                logger.error("Execution failed for plan: {}", context.getPlanId());
            }
            
        } catch (Exception e) {
            logger.error("Execution error for plan {}: {}", context.getPlanId(), e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            result.setCompletedAt(LocalDateTime.now());
            executionPlanService.updateExecutionStatus(context.getPlanId(), "FAILED");
        }
        
        return result;
    }
    
    /**
     * Execute an execution flow
     */
    private boolean executeExecutionFlow(ExecutionFlow flow, ExecutionContext context) {
        logger.debug("Executing flow type: {}", flow.getType());
        
        switch (flow.getType()) {
            case SEQUENTIAL:
                return executeSequentialFlow(flow, context);
            case PARALLEL:
                return executeParallelFlow(flow, context);
            case CONDITIONAL:
                return executeConditionalFlow(flow, context);
            case LOOP:
                return executeLoopFlow(flow, context);
            default:
                logger.error("Unknown execution flow type: {}", flow.getType());
                return false;
        }
    }
    
    /**
     * Execute sequential flow
     */
    private boolean executeSequentialFlow(ExecutionFlow flow, ExecutionContext context) {
        logger.debug("Executing sequential flow with {} steps", flow.getSteps().size());
        
        for (Object stepObj : flow.getSteps()) {
            // Check if execution is paused
            if ("PAUSED".equals(context.getStatus())) {
                logger.info("Execution paused, waiting...");
                waitForResume(context);
            }
            
            // Check if execution is cancelled
            if (Thread.currentThread().isInterrupted()) {
                logger.info("Execution cancelled");
                return false;
            }
            
            boolean stepSuccess = executeStep(stepObj, context);
            if (!stepSuccess) {
                logger.error("Step execution failed in sequential flow");
                return false;
            }
            
            context.incrementCompletedSteps();
            executionPlanService.updateStepCompletion(context.getPlanId(), stepObj.toString(), true);
        }
        
        return true;
    }
    
    /**
     * Execute parallel flow
     */
    private boolean executeParallelFlow(ExecutionFlow flow, ExecutionContext context) {
        logger.debug("Executing parallel flow with {} steps", flow.getSteps().size());
        
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        
        for (Object stepObj : flow.getSteps()) {
            CompletableFuture<Boolean> stepFuture = CompletableFuture.supplyAsync(() -> {
                return executeStep(stepObj, context);
            }, executorService);
            
            futures.add(stepFuture);
        }
        
        // Wait for all steps to complete
        try {
            CompletableFuture<Void> allSteps = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            allSteps.get(30, TimeUnit.MINUTES); // 30 minute timeout
            
            // Check if all steps succeeded
            boolean allSuccess = futures.stream()
                .allMatch(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        logger.error("Error getting step result: {}", e.getMessage());
                        return false;
                    }
                });
            
            context.setCompletedStepsCount(context.getCompletedStepsCount() + flow.getSteps().size());
            return allSuccess;
            
        } catch (Exception e) {
            logger.error("Error executing parallel flow: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Execute conditional flow
     */
    private boolean executeConditionalFlow(ExecutionFlow flow, ExecutionContext context) {
        logger.debug("Executing conditional flow");
        
        // For now, execute the first step (condition evaluation would be implemented here)
        if (!flow.getSteps().isEmpty()) {
            return executeStep(flow.getSteps().get(0), context);
        }
        
        return true;
    }
    
    /**
     * Execute loop flow
     */
    private boolean executeLoopFlow(ExecutionFlow flow, ExecutionContext context) {
        logger.debug("Executing loop flow");
        
        // Simple loop implementation - execute steps multiple times
        int maxIterations = 5; // Could be configurable
        
        for (int i = 0; i < maxIterations; i++) {
            for (Object stepObj : flow.getSteps()) {
                boolean stepSuccess = executeStep(stepObj, context);
                if (!stepSuccess) {
                    logger.error("Step execution failed in loop iteration {}", i);
                    return false;
                }
            }
            
            // Check loop condition (simplified)
            if (shouldExitLoop(context)) {
                break;
            }
        }
        
        return true;
    }
    
    /**
     * Execute a single step
     */
    private boolean executeStep(Object stepObj, ExecutionContext context) {
        logger.debug("Executing step: {}", stepObj.getClass().getSimpleName());
        
        try {
            if (stepObj instanceof SequentialStep) {
                return executeSequentialStep((SequentialStep) stepObj, context);
            } else if (stepObj instanceof ParallelStep) {
                return executeParallelStep((ParallelStep) stepObj, context);
            } else if (stepObj instanceof ConditionalStep) {
                return executeConditionalStep((ConditionalStep) stepObj, context);
            } else if (stepObj instanceof LoopStep) {
                return executeLoopStep((LoopStep) stepObj, context);
            } else {
                logger.warn("Unknown step type: {}", stepObj.getClass());
                return true; // Continue execution
            }
        } catch (Exception e) {
            logger.error("Error executing step: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Execute sequential step
     */
    private boolean executeSequentialStep(SequentialStep step, ExecutionContext context) {
        logger.debug("Executing sequential step: {}", step.getStepId());

        context.setCurrentStep(step.getStepId());

        // Check if human approval is required
        if (step.isRequiresHumanApproval()) {
            return handleHumanApproval(step, context);
        }

        // Execute the step logic
        return executeStepLogic(step, context);
    }
    
    /**
     * Execute parallel step
     */
    private boolean executeParallelStep(ParallelStep step, ExecutionContext context) {
        logger.debug("Executing parallel step: {}", step.getName());
        
        context.setCurrentStep(step.getName());
        return executeStepLogic(step, context);
    }
    
    /**
     * Execute conditional step
     */
    private boolean executeConditionalStep(ConditionalStep step, ExecutionContext context) {
        logger.debug("Executing conditional step: {}", step.getName());
        
        context.setCurrentStep(step.getName());
        
        // Evaluate condition (simplified)
        boolean conditionMet = evaluateCondition(step.getCondition(), context);
        
        if (conditionMet) {
            return executeStepLogic(step, context);
        } else {
            logger.debug("Condition not met for step: {}", step.getName());
            return true; // Skip step but continue execution
        }
    }
    
    /**
     * Execute loop step
     */
    private boolean executeLoopStep(LoopStep step, ExecutionContext context) {
        logger.debug("Executing loop step: {}", step.getName());
        
        context.setCurrentStep(step.getName());
        return executeStepLogic(step, context);
    }
    
    /**
     * Execute step logic (placeholder for actual agent execution)
     */
    private boolean executeStepLogic(Object step, ExecutionContext context) {
        logger.debug("Executing step logic for: {}", step.getClass().getSimpleName());
        
        try {
            // Simulate step execution
            Thread.sleep(1000); // Simulate work
            
            // In real implementation, this would:
            // 1. Find the appropriate agent for this step
            // 2. Execute the agent with the step parameters
            // 3. Handle the agent response
            // 4. Update shared context
            
            return true;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Handle human approval
     */
    private boolean handleHumanApproval(SequentialStep step, ExecutionContext context) {
        logger.info("Requesting human approval for step: {}", step.getName());
        
        executionPlanService.updateExecutionStatus(context.getPlanId(), "WAITING_FOR_APPROVAL");
        
        // Request approval through human-in-the-loop service
        boolean approved = humanInTheLoopService.requestApproval(
            context.getPlanId(), 
            step.getName(), 
            step.getDescription(),
            context.getSharedData()
        );
        
        if (approved) {
            executionPlanService.updateExecutionStatus(context.getPlanId(), "RUNNING");
            return executeStepLogic(step, context);
        } else {
            logger.info("Human approval denied for step: {}", step.getName());
            return false;
        }
    }
    
    /**
     * Helper methods
     */
    private void waitForResume(ExecutionContext context) {
        while ("PAUSED".equals(context.getStatus())) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private boolean shouldExitLoop(ExecutionContext context) {
        // Simplified loop exit condition
        return context.getCompletedStepsCount() > 10;
    }
    
    private boolean evaluateCondition(Object condition, ExecutionContext context) {
        // Simplified condition evaluation
        return true;
    }
    
    /**
     * Execute a plan with generated agents
     */
    public String executePlan(ExecutionPlan executionPlan, List<Agent> generatedAgents) {
        logger.info("Executing plan: {} with {} agents", executionPlan.getPlanId(), generatedAgents.size());
        
        try {
            CompletableFuture<ExecutionResult> executionFuture = startExecution(executionPlan.getPlanId());
            ExecutionResult result = executionFuture.get(30, TimeUnit.MINUTES);
            
            if (result.isSuccess()) {
                return "Execution completed successfully for plan: " + executionPlan.getPlanId();
            } else {
                return "Execution failed for plan: " + executionPlan.getPlanId() + ". Error: " + result.getErrorMessage();
            }
        } catch (Exception e) {
            logger.error("Error executing plan: {}", e.getMessage(), e);
            return "Execution failed for plan: " + executionPlan.getPlanId() + ". Error: " + e.getMessage();
        }
    }
    
    /**
     * Stop execution and return a CompletableFuture<Void>
     */
    public CompletableFuture<Void> stopExecutionAsync(String planId) {
        return CompletableFuture.runAsync(() -> {
            stopExecution(planId);
        });
    }
}