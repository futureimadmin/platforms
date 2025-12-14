package com.nebula.controlplane.service;

import com.nebula.shared.model.ExecutionPlan;
import com.nebula.shared.model.Agent;
import com.nebula.shared.model.ExecutionPlanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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
                List<Agent> generatedAgents = agentGenerationService.generateAgents(userPrompt, new HashMap<>());
                
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
        return executionOrchestrationService.stopExecutionAsync(planId);
    }
    
    /**
     * Handle human approval for a step
     */
    public CompletableFuture<Void> handleHumanApproval(String planId, String stepId, boolean approved, String feedback) {
        logger.info("Handling human approval for plan: {}, step: {}, approved: {}", planId, stepId, approved);
        boolean result = humanInTheLoopService.handleHumanApproval(planId, stepId, approved, feedback);
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Join a Microsoft Teams meeting for human interaction
     */
    public CompletableFuture<Void> joinTeamsMeeting(String planId, String meetingId) {
        logger.info("Joining Teams meeting for plan: {}, meeting: {}", planId, meetingId);
        return humanInTheLoopService.joinTeamsMeetingForApproval(planId, meetingId)
            .thenApply(result -> null);
    }
    
    /**
     * Process speech input from Teams meeting
     */
    public CompletableFuture<String> processSpeechInput(String planId, String speechText) {
        logger.info("Processing speech input for plan: {}", planId);
        return humanInTheLoopService.processSpeechForApproval(planId, speechText);
    }
    
}