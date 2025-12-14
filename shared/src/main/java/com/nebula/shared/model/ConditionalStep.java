package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Represents a conditional execution step where execution depends on a condition.
 */
public class ConditionalStep extends ExecutionStep {
    
    @NotNull
    @Valid
    @JsonProperty("condition")
    private Condition condition;
    
    @NotNull
    @Valid
    @JsonProperty("thenStep")
    private ExecutionStep thenStep;
    
    @Valid
    @JsonProperty("elseStep")
    private ExecutionStep elseStep;
    
    // Constructors
    public ConditionalStep() {}
    
    public ConditionalStep(String stepId, String name, String description, 
                          Condition condition, ExecutionStep thenStep) {
        super(stepId, name, description);
        this.condition = condition;
        this.thenStep = thenStep;
    }
    
    public ConditionalStep(String stepId, String name, String description, 
                          Condition condition, ExecutionStep thenStep, ExecutionStep elseStep) {
        super(stepId, name, description);
        this.condition = condition;
        this.thenStep = thenStep;
        this.elseStep = elseStep;
    }
    
    // Getters and Setters
    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }
    
    public ExecutionStep getThenStep() { return thenStep; }
    public void setThenStep(ExecutionStep thenStep) { this.thenStep = thenStep; }
    
    public ExecutionStep getElseStep() { return elseStep; }
    public void setElseStep(ExecutionStep elseStep) { this.elseStep = elseStep; }
    
    @Override
    public String getType() {
        return "conditional";
    }
    
    /**
     * Represents a condition for conditional execution
     */
    public static class Condition {
        @NotNull
        @JsonProperty("expression")
        private String expression;
        
        @JsonProperty("variables")
        private List<String> variables;
        
        // Constructors
        public Condition() {}
        
        public Condition(String expression) {
            this.expression = expression;
        }
        
        public Condition(String expression, List<String> variables) {
            this.expression = expression;
            this.variables = variables;
        }
        
        // Getters and Setters
        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
        
        public List<String> getVariables() { return variables; }
        public void setVariables(List<String> variables) { this.variables = variables; }
    }
}