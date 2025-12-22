package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an execution step in the workflow.
 * Uses a flat structure with a type field to determine the step's behavior.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionStep {
    
    @NotNull(message = "Step ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Step ID must contain only alphanumeric characters, hyphens, and underscores")
    @JsonProperty("stepId")
    private String stepId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @NotNull(message = "Step type is required")
    @JsonProperty("type")
    private String type;  // task, conditional, loop, parallel, sequential, hierarchical, hybrid
    
    @Pattern(regexp = "^[a-zA-Z0-9-_]*$", message = "Agent ID must contain only alphanumeric characters, hyphens, and underscores")
    @JsonProperty("agentId")
    private String agentId;

    @Pattern(regexp = "^[a-zA-Z0-9-_]*$", message = "Instruction or Prompt to be given to the agent, Create the best instruction/prompt for agent")
    @JsonProperty("instruction")
    private String instruction;
    
    @JsonProperty("action")
    private String action;
    
    @Valid
    @JsonProperty("inputMappings")
    private Map<String, Object> inputMappings = new HashMap<>();
    
    @Valid
    @JsonProperty("outputMappings")
    private Map<String, Object> outputMappings = new HashMap<>();
    
    @JsonProperty("parameters")
    @JsonIgnore
    @JsonDeserialize(using = ParametersDeserializer.class)
    private Map<String, Object> parameters = new HashMap<>();
    
    @JsonProperty("enabled")
    private boolean enabled = true;
    
    @Pattern(regexp = "^\\d+(s|m|h)?$", message = "Timeout must be a number followed by optional 's' (seconds), 'm' (minutes), or 'h' (hours)")
    @JsonProperty("timeout")
    private String timeout;
    
    @Valid
    @JsonProperty("retryPolicy")
    private Map<String, Object> retryPolicy = new HashMap<>();
    
    @JsonProperty("dependencies")
    private List<String> dependencies = new ArrayList<>();
    
    @JsonProperty("condition")
    private String condition;
    
    @Valid
    @JsonProperty("errorHandling")
    private Map<String, Object> errorHandling = new HashMap<>();
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata = new HashMap<>();
    
    @Valid
    @JsonProperty("flow")
    private ExecutionFlow flow;
    
    // Constructors
    public ExecutionStep() {}
    
    public ExecutionStep(String stepId, String type) {
        this.stepId = stepId;
        this.type = type;
    }
    
    // Getters and Setters
    public String getStepId() { 
        return stepId; 
    }
    
    public void setStepId(String stepId) { 
        this.stepId = stepId; 
    }
    
    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public Map<String, Object> getInputMappings() {
        return inputMappings;
    }
    
    public void setInputMappings(Map<String, Object> inputMappings) {
        this.inputMappings = inputMappings != null ? inputMappings : new HashMap<>();
    }
    
    public Map<String, Object> getOutputMappings() {
        return outputMappings;
    }
    
    public void setOutputMappings(Map<String, Object> outputMappings) {
        this.outputMappings = outputMappings != null ? outputMappings : new HashMap<>();
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters != null ? parameters : new HashMap<>();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getTimeout() { 
        return timeout; 
    }
    
    public void setTimeout(String timeout) { 
        this.timeout = timeout; 
    }
    
    public Map<String, Object> getRetryPolicy() {
        return retryPolicy;
    }
    
    public void setRetryPolicy(Map<String, Object> retryPolicy) {
        this.retryPolicy = retryPolicy != null ? retryPolicy : new HashMap<>();
    }
    
    public List<String> getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies != null ? dependencies : new ArrayList<>();
    }
    
    public void addDependency(String dependency) {
        if (this.dependencies == null) {
            this.dependencies = new ArrayList<>();
        }
        this.dependencies.add(dependency);
    }
    
    public String getCondition() {
        return condition;
    }
    
    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    public Map<String, Object> getErrorHandling() {
        return errorHandling;
    }
    
    public void setErrorHandling(Map<String, Object> errorHandling) {
        this.errorHandling = errorHandling != null ? errorHandling : new HashMap<>();
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
    
    public ExecutionFlow getFlow() {
        return flow;
    }
    
    public void setFlow(ExecutionFlow flow) {
        this.flow = flow;
    }
    
    // Helper methods for common operations
    public void addParameter(String key, Object value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(key, value);
    }
    
    public void addInputMapping(String key, Object value) {
        if (this.inputMappings == null) {
            this.inputMappings = new HashMap<>();
        }
        this.inputMappings.put(key, value);
    }
    
    public void addOutputMapping(String key, Object value) {
        if (this.outputMappings == null) {
            this.outputMappings = new HashMap<>();
        }
        this.outputMappings.put(key, value);
    }
    
    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
    }
}
