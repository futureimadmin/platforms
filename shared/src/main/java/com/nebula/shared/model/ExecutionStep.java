package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;

/**
 * Base class for execution steps
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SequentialStep.class, name = "sequential"),
    @JsonSubTypes.Type(value = ParallelStep.class, name = "parallel"),
    @JsonSubTypes.Type(value = ConditionalStep.class, name = "conditional"),
    @JsonSubTypes.Type(value = LoopStep.class, name = "loop")
})
public abstract class ExecutionStep {
    
    @NotNull
    @com.fasterxml.jackson.annotation.JsonProperty("stepId")
    private String stepId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("name")
    private String name;
    
    @com.fasterxml.jackson.annotation.JsonProperty("description")
    private String description;
    
    @com.fasterxml.jackson.annotation.JsonProperty("timeout")
    private String timeout;
    
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
