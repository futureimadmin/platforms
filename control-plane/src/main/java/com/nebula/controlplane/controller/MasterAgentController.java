package com.nebula.controlplane.controller;

import com.nebula.controlplane.service.MasterAgentService;
import com.nebula.shared.model.Agent;
import com.nebula.shared.model.ExecutionPlanStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for the Master Agent.
 * Provides endpoints for:
 * 1. Processing user prompts
 * 2. Managing execution plans
 * 3. Monitoring agent status
 * 4. Human-in-the-loop interactions
 */
@RestController
@RequestMapping("/api/v1/master-agent")
@CrossOrigin(origins = "*")
public class MasterAgentController {
    
    private static final Logger logger = LoggerFactory.getLogger(MasterAgentController.class);
    
    @Autowired
    private MasterAgentService masterAgentService;
    
    /**
     * Process a user prompt and create execution plan
     */
    @PostMapping("/process")
    public CompletableFuture<ResponseEntity<ProcessResponse>> processPrompt(@RequestBody ProcessRequest request) {
        logger.info("Received prompt processing request: {}", request.getPrompt());
        
        return masterAgentService.processPrompt(request.getPrompt(), request.getContext())
            .thenApply(result -> {
                ProcessResponse response = new ProcessResponse();
                response.setSuccess(true);
                response.setMessage("Prompt processed successfully");
                response.setResult(result);
                return ResponseEntity.ok(response);
            })
            .exceptionally(throwable -> {
                logger.error("Error processing prompt", throwable);
                ProcessResponse response = new ProcessResponse();
                response.setSuccess(false);
                response.setMessage("Error processing prompt: " + throwable.getMessage());
                return ResponseEntity.internalServerError().body(response);
            });
    }
    
    /**
     * Get execution status for a plan
     */
    @GetMapping("/execution/{planId}/status")
    public ResponseEntity<ExecutionPlanStatus> getExecutionStatus(@PathVariable String planId) {
        logger.info("Getting execution status for plan: {}", planId);
        
        try {
            ExecutionPlanStatus status = masterAgentService.getExecutionStatus(planId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("Error getting execution status", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get list of agents for an execution plan
     */
    @GetMapping("/execution/{planId}/agents")
    public ResponseEntity<List<Agent>> getAgentsForPlan(@PathVariable String planId) {
        logger.info("Getting agents for plan: {}", planId);
        
        try {
            List<Agent> agents = masterAgentService.getAgentsForPlan(planId);
            return ResponseEntity.ok(agents);
        } catch (Exception e) {
            logger.error("Error getting agents for plan", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Stop execution of a plan
     */
    @PostMapping("/execution/{planId}/stop")
    public CompletableFuture<ResponseEntity<ApiResponse>> stopExecution(@PathVariable String planId) {
        logger.info("Stopping execution for plan: {}", planId);
        
        return masterAgentService.stopExecution(planId)
            .thenApply(result -> {
                ApiResponse response = new ApiResponse();
                response.setSuccess(true);
                response.setMessage("Execution stopped successfully");
                return ResponseEntity.ok(response);
            })
            .exceptionally(throwable -> {
                logger.error("Error stopping execution", throwable);
                ApiResponse response = new ApiResponse();
                response.setSuccess(false);
                response.setMessage("Error stopping execution: " + throwable.getMessage());
                return ResponseEntity.internalServerError().body(response);
            });
    }
    
    /**
     * Handle human approval for a step
     */
    @PostMapping("/execution/{planId}/steps/{stepId}/approval")
    public CompletableFuture<ResponseEntity<ApiResponse>> handleHumanApproval(
            @PathVariable String planId,
            @PathVariable String stepId,
            @RequestBody ApprovalRequest request) {
        
        logger.info("Handling human approval for plan: {}, step: {}, approved: {}", 
                   planId, stepId, request.isApproved());
        
        return masterAgentService.handleHumanApproval(planId, stepId, request.isApproved(), request.getFeedback())
            .thenApply(result -> {
                ApiResponse response = new ApiResponse();
                response.setSuccess(true);
                response.setMessage("Approval processed successfully");
                return ResponseEntity.ok(response);
            })
            .exceptionally(throwable -> {
                logger.error("Error processing approval", throwable);
                ApiResponse response = new ApiResponse();
                response.setSuccess(false);
                response.setMessage("Error processing approval: " + throwable.getMessage());
                return ResponseEntity.internalServerError().body(response);
            });
    }
    
    /**
     * Join Microsoft Teams meeting
     */
    @PostMapping("/execution/{planId}/teams/join")
    public CompletableFuture<ResponseEntity<ApiResponse>> joinTeamsMeeting(
            @PathVariable String planId,
            @RequestBody TeamsJoinRequest request) {
        
        logger.info("Joining Teams meeting for plan: {}, meeting: {}", planId, request.getMeetingId());
        
        return masterAgentService.joinTeamsMeeting(planId, request.getMeetingId())
            .thenApply(result -> {
                ApiResponse response = new ApiResponse();
                response.setSuccess(true);
                response.setMessage("Joined Teams meeting successfully");
                return ResponseEntity.ok(response);
            })
            .exceptionally(throwable -> {
                logger.error("Error joining Teams meeting", throwable);
                ApiResponse response = new ApiResponse();
                response.setSuccess(false);
                response.setMessage("Error joining Teams meeting: " + throwable.getMessage());
                return ResponseEntity.internalServerError().body(response);
            });
    }
    
    /**
     * Process speech input from Teams meeting
     */
    @PostMapping("/execution/{planId}/teams/speech")
    public CompletableFuture<ResponseEntity<SpeechResponse>> processSpeechInput(
            @PathVariable String planId,
            @RequestBody SpeechRequest request) {
        
        logger.info("Processing speech input for plan: {}", planId);
        
        return masterAgentService.processSpeechInput(planId, request.getSpeechText())
            .thenApply(result -> {
                SpeechResponse response = new SpeechResponse();
                response.setSuccess(true);
                response.setMessage("Speech processed successfully");
                response.setResponse(result);
                return ResponseEntity.ok(response);
            })
            .exceptionally(throwable -> {
                logger.error("Error processing speech input", throwable);
                SpeechResponse response = new SpeechResponse();
                response.setSuccess(false);
                response.setMessage("Error processing speech: " + throwable.getMessage());
                return ResponseEntity.internalServerError().body(response);
            });
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> healthCheck() {
        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setMessage("Master Agent is healthy");
        return ResponseEntity.ok(response);
    }
    
    // Request/Response DTOs
    
    public static class ProcessRequest {
        private String prompt;
        private Map<String, Object> context;
        
        // Getters and Setters
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
        
        public Map<String, Object> getContext() { return context; }
        public void setContext(Map<String, Object> context) { this.context = context; }
    }
    
    public static class ProcessResponse extends ApiResponse {
        private String result;
        
        // Getters and Setters
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
    }
    
    public static class ApprovalRequest {
        private boolean approved;
        private String feedback;
        
        // Getters and Setters
        public boolean isApproved() { return approved; }
        public void setApproved(boolean approved) { this.approved = approved; }
        
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
    }
    
    public static class TeamsJoinRequest {
        private String meetingId;
        
        // Getters and Setters
        public String getMeetingId() { return meetingId; }
        public void setMeetingId(String meetingId) { this.meetingId = meetingId; }
    }
    
    public static class SpeechRequest {
        private String speechText;
        
        // Getters and Setters
        public String getSpeechText() { return speechText; }
        public void setSpeechText(String speechText) { this.speechText = speechText; }
    }
    
    public static class SpeechResponse extends ApiResponse {
        private String response;
        
        // Getters and Setters
        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
    }
    
    public static class ApiResponse {
        private boolean success;
        private String message;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}