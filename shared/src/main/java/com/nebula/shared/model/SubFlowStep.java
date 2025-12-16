package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

/**
 * A step that invokes a nested execution flow as part of an execution.
 */
@JsonTypeName("SUBFLOW")
public class SubFlowStep extends ExecutionStep {

    @NotBlank
    @JsonProperty("flowId")
    private String flowId;

    @Valid
    @JsonProperty("flow")
    private ExecutionFlow flow;

    @JsonProperty("waitForCompletion")
    private boolean waitForCompletion = true;

    // Constructors
    public SubFlowStep() {
        super();
    }

    public SubFlowStep(String stepId, String name, String description, String flowId) {
        super(stepId, name, description);
        this.flowId = flowId;
    }
    
    public SubFlowStep(String stepId, String name, String description, ExecutionFlow flow) {
        super(stepId, name, description);
        this.flow = flow;
        this.flowId = stepId + "_flow";
    }

    // Getters and Setters
    public String getFlowId() { 
        return flowId; 
    }
    
    public void setFlowId(String flowId) { 
        this.flowId = flowId; 
    }

    public ExecutionFlow getFlow() { 
        return flow; 
    }
    
    public void setFlow(ExecutionFlow flow) { 
        this.flow = flow; 
    }
    
    public boolean isWaitForCompletion() {
        return waitForCompletion;
    }
    
    public void setWaitForCompletion(boolean waitForCompletion) {
        this.waitForCompletion = waitForCompletion;
    }

    @Override
    public String getType() {
        return "subflow";
    }
}
