package com.nebula.controlplane.service;

import com.nebula.shared.model.ExecutionPlan;
import com.nebula.shared.model.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Master Agent Service - The core orchestrator of the Nebula platform.
 * Responsible for:
 * 1. Receiving user prompts
 * 2. Using LLM to create execution plans
 * 3. Generating required agents
 * 4. Orchestrating agent execution
 * 5. Managing human-in-the-loop interactions
 */
@Service
public class MasterAgentService {
    
    private static final Logger logger = LoggerFactory.getLogger(MasterAgentService.class);
    
    @Autowired
    private LLMService llmService;
    
    @Autowired
    private ExecutionPlanService executionPlanService;
    
    @Autowired
    private AgentGenerationService agentGenerationService;
    
    @Autowired
    private ExecutionOrchestrationService executionOrchestrationService;
    
    @Autowired
    private HumanInTheLoopService humanInTheLoopService;
    
    /**
     * Process a user prompt and orchestrate the entire execution
     */
    public CompletableFuture<String> processPrompt(String userPrompt, Map<String, Object> context) {
        logger.info("Processing user prompt: {}", userPrompt);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Step 1: Use LLM to analyze the prompt and create execution plan
                logger.info("Creating execution plan using LLM...");
                ExecutionPlan executionPlan = llmService.createExecutionPlan(userPrompt, context);
                
                // Step 2: Save the execution plan
                executionPlan = executionPlanService.createExecutionPlan(executionPlan);
                logger.info("Execution plan created with ID: {}", executionPlan.getPlanId());
                
                // Step 3: Generate required agents using LLM
                logger.info("Generating agents for execution plan...");
                List<Agent> generatedAgents = agentGenerationService.generateAgents(executionPlan);
                
                // Step 4: Start execution orchestration
                logger.info("Starting execution orchestration...");
                String executionResult = executionOrchestrationService.executePlan(executionPlan, generatedAgents);
                
                logger.info("Execution completed successfully");
                return executionResult;
                
            } catch (Exception e) {
                logger.error("Error processing prompt", e);
                throw new RuntimeException("Failed to process prompt: " + e.getMessage(), e);
            }
        });
    }
    
    /**
     * Get status of an execution plan
     */
    public ExecutionPlanStatus getExecutionStatus(String planId) {
        return executionPlanService.getExecutionStatus(planId);
    }
    
    /**
     * Get list of all agents for an execution plan
     */
    public List<Agent> getAgentsForPlan(String planId) {
        return agentGenerationService.getGeneratedAgents(planId);
    }
    
    /**
     * Stop execution of a plan
     */
    public CompletableFuture<Void> stopExecution(String planId) {
        logger.info("Stopping execution for plan: {}", planId);
        return executionOrchestrationService.stopExecution(planId);
    }
    
    /**
     * Handle human approval for a step
     */
    public CompletableFuture<Void> handleHumanApproval(String planId, String stepId, boolean approved, String feedback) {
        logger.info("Handling human approval for plan: {}, step: {}, approved: {}", planId, stepId, approved);
        return humanInTheLoopService.handleHumanApproval(planId, stepId, approved, feedback);
    }
    
    /**
     * Join a Microsoft Teams meeting for human interaction
     */
    public CompletableFuture<Void> joinTeamsMeeting(String planId, String meetingId) {
        logger.info("Joining Teams meeting for plan: {}, meeting: {}", planId, meetingId);
        return humanInTheLoopService.joinTeamsMeetingForApproval(planId, meetingId);
    }
    
    /**
     * Process speech input from Teams meeting
     */
    public CompletableFuture<String> processSpeechInput(String planId, String speechText) {
        logger.info("Processing speech input for plan: {}", planId);
        return humanInTheLoopService.processSpeechForApproval(planId, speechText);
    }
    
    /**
     * Execution plan status
     */
    public static class ExecutionPlanStatus {
        private String planId;
        private String status;
        private int totalSteps;
        private int completedSteps;
        private int totalAgents;
        private int activeAgents;
        private String currentStep;
        private Map<String, Object> context;
        
        // Constructors, getters, and setters
        public ExecutionPlanStatus() {}
        
        public ExecutionPlanStatus(String planId, String status, int totalSteps, int completedSteps, 
                                 int totalAgents, int activeAgents, String currentStep) {
            this.planId = planId;
            this.status = status;
            this.totalSteps = totalSteps;
            this.completedSteps = completedSteps;
            this.totalAgents = totalAgents;
            this.activeAgents = activeAgents;
            this.currentStep = currentStep;
        }
        
        // Getters and Setters
        public String getPlanId() { return planId; }
        public void setPlanId(String planId) { this.planId = planId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public int getTotalSteps() { return totalSteps; }
        public void setTotalSteps(int totalSteps) { this.totalSteps = totalSteps; }
        
        public int getCompletedSteps() { return completedSteps; }
        public void setCompletedSteps(int completedSteps) { this.completedSteps = completedSteps; }
        
        public int getTotalAgents() { return totalAgents; }
        public void setTotalAgents(int totalAgents) { this.totalAgents = totalAgents; }
        
        public int getActiveAgents() { return activeAgents; }
        public void setActiveAgents(int activeAgents) { this.activeAgents = activeAgents; }
        
        public String getCurrentStep() { return currentStep; }
        public void setCurrentStep(String currentStep) { this.currentStep = currentStep; }
        
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }
}