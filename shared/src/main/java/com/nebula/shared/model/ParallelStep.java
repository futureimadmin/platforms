package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Represents a parallel execution step where multiple agents are executed simultaneously.
 */
public class ParallelStep extends ExecutionStep {
    
    @NotEmpty
    @Valid
    @JsonProperty("parallelAgents")
    private List<ParallelAgent> parallelAgents;
    
    @JsonProperty("waitForAll")
    private Boolean waitForAll = true;
    
    // Constructors
    public ParallelStep() {}
    
    public ParallelStep(String stepId, String name, String description, List<ParallelAgent> parallelAgents) {
        super(stepId, name, description);
        this.parallelAgents = parallelAgents;
    }
    
    public ParallelStep(String stepId, String name, String description, List<ParallelAgent> parallelAgents, Boolean waitForAll) {
        super(stepId, name, description);
        this.parallelAgents = parallelAgents;
        this.waitForAll = waitForAll;
    }
    
    // Getters and Setters
    public List<ParallelAgent> getParallelAgents() { return parallelAgents; }
    public void setParallelAgents(List<ParallelAgent> parallelAgents) { this.parallelAgents = parallelAgents; }
    
    public Boolean getWaitForAll() { return waitForAll; }
    public void setWaitForAll(Boolean waitForAll) { this.waitForAll = waitForAll; }
    
    @Override
    public String getType() {
        return "parallel";
    }
    
    /**
     * Represents an agent to be executed in parallel
     */
    public static class ParallelAgent {
        @NotNull
        @JsonProperty("agentId")
        private String agentId;
        
        @JsonProperty("inputs")
        private Map<String, Object> inputs;
        
        // Constructors
        public ParallelAgent() {}
        
        public ParallelAgent(String agentId) {
            this.agentId = agentId;
        }
        
        public ParallelAgent(String agentId, Map<String, Object> inputs) {
            this.agentId = agentId;
            this.inputs = inputs;
        }
        
        // Getters and Setters
        public String getAgentId() { return agentId; }
        public void setAgentId(String agentId) { this.agentId = agentId; }
        
        public Map<String, Object> getInputs() { return inputs; }
        public void setInputs(Map<String, Object> inputs) { this.inputs = inputs; }
    }
}