package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for execution steps in the workflow.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SequentialStep.class, name = "SEQUENTIAL"),
    @JsonSubTypes.Type(value = ParallelStep.class, name = "PARALLEL"),
    @JsonSubTypes.Type(value = ConditionalStep.class, name = "CONDITIONAL"),
    @JsonSubTypes.Type(value = LoopStep.class, name = "LOOP"),
    @JsonSubTypes.Type(value = SubFlowStep.class, name = "SUBFLOW")
})
public abstract class ExecutionStep {
    
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$")
    @JsonProperty("stepId")
    private String stepId;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("agentId")
    private String agentId;
    
    @JsonProperty("action")
    private String action;
    
    @Valid
    @JsonProperty("inputMappings")
    private Map<String, Object> inputMappings = new HashMap<>();
    
    @Valid
    @JsonProperty("outputMappings")
    private Map<String, Object> outputMappings = new HashMap<>();
    
    @JsonProperty("dependencies")
    private List<String> dependencies;
    
    @JsonProperty("condition")
    private String condition;
    
    @Pattern(regexp = "^\\d+(s|m|h)$")
    @JsonProperty("timeout")
    private String timeout;
    
    @Valid
    @JsonProperty("retryPolicy")
    private RetryPolicy retryPolicy;
    
    @Valid
    @JsonProperty("errorHandling")
    private ErrorHandling errorHandling;
    
    @JsonProperty("metadata")
    private Map<String, Object> metadata = new HashMap<>();
    
    // For nested flows
    @Valid
    @JsonProperty("flow")
    private ExecutionFlow flow;
    
    // Constructors
    public ExecutionStep() {}
    
    public ExecutionStep(String stepId, String name, String description) {
        this.stepId = stepId;
        this.name = name;
        this.description = description;
    }
    
    // Getters and Setters
    public String getStepId() { return stepId; }
    public void setStepId(String stepId) { this.stepId = stepId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTimeout() { return timeout; }
    public void setTimeout(String timeout) { this.timeout = timeout; }
    
    public abstract String getType();
}
