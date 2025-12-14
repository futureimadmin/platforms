package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

/**
 * Represents a sequential execution step where a single agent is executed.
 */
public class SequentialStep extends ExecutionStep {
    
    @NotBlank
    @JsonProperty("agentId")
    private String agentId;
    
    @JsonProperty("inputs")
    private Map<String, Object> inputs;
    
    @JsonProperty("outputs")
    private List<String> outputs;
    
    // Constructors
    public SequentialStep() {}
    
    public SequentialStep(String stepId, String name, String description, String agentId) {
        super(stepId, name, description);
        this.agentId = agentId;
    }
    
    public SequentialStep(String stepId, String name, String description, String agentId, 
                         Map<String, Object> inputs, List<String> outputs) {
        super(stepId, name, description);
        this.agentId = agentId;
        this.inputs = inputs;
        this.outputs = outputs;
    }
    
    // Getters and Setters
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public Map<String, Object> getInputs() { return inputs; }
    public void setInputs(Map<String, Object> inputs) { this.inputs = inputs; }
    
    public List<String> getOutputs() { return outputs; }
    public void setOutputs(List<String> outputs) { this.outputs = outputs; }
    
    @Override
    public String getType() {
        return "sequential";
    }

    @JsonProperty("requiresHumanApproval")
    private boolean requiresHumanApproval = false;
    
    public boolean isRequiresHumanApproval() {
        return requiresHumanApproval;
    }
    
    public void setRequiresHumanApproval(boolean requiresHumanApproval) {
        this.requiresHumanApproval = requiresHumanApproval;
    }
}