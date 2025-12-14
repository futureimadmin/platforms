package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nebula.shared.enums.LoopType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Represents a loop execution step where steps are executed repeatedly based on a condition.
 */
public class LoopStep extends ExecutionStep {
    
    @NotNull
    @JsonProperty("loopType")
    private LoopType loopType;
    
    @NotEmpty
    @Valid
    @JsonProperty("body")
    private List<ExecutionStep> body;
    
    @NotNull
    @Valid
    @JsonProperty("exitCondition")
    private ExitCondition exitCondition;
    
    @JsonProperty("iterationVariable")
    private String iterationVariable;
    
    @JsonProperty("collectionVariable")
    private String collectionVariable;
    
    // Constructors
    public LoopStep() {}
    
    public LoopStep(String stepId, String name, String description, 
                   LoopType loopType, List<ExecutionStep> body, ExitCondition exitCondition) {
        super(stepId, name, description);
        this.loopType = loopType;
        this.body = body;
        this.exitCondition = exitCondition;
    }
    
    // Getters and Setters
    public LoopType getLoopType() { return loopType; }
    public void setLoopType(LoopType loopType) { this.loopType = loopType; }
    
    public List<ExecutionStep> getBody() { return body; }
    public void setBody(List<ExecutionStep> body) { this.body = body; }
    
    public ExitCondition getExitCondition() { return exitCondition; }
    public void setExitCondition(ExitCondition exitCondition) { this.exitCondition = exitCondition; }
    
    public String getIterationVariable() { return iterationVariable; }
    public void setIterationVariable(String iterationVariable) { this.iterationVariable = iterationVariable; }
    
    public String getCollectionVariable() { return collectionVariable; }
    public void setCollectionVariable(String collectionVariable) { this.collectionVariable = collectionVariable; }
    
    @Override
    public String getType() {
        return "loop";
    }
    
    /**
     * Represents the exit condition for a loop
     */
    public static class ExitCondition {
        @NotNull
        @JsonProperty("expression")
        private String expression;
        
        @NotNull
        @JsonProperty("checkAgentId")
        private String checkAgentId;
        
        @JsonProperty("maxIterations")
        private Integer maxIterations;
        
        // Constructors
        public ExitCondition() {}
        
        public ExitCondition(String expression, String checkAgentId) {
            this.expression = expression;
            this.checkAgentId = checkAgentId;
        }
        
        public ExitCondition(String expression, String checkAgentId, Integer maxIterations) {
            this.expression = expression;
            this.checkAgentId = checkAgentId;
            this.maxIterations = maxIterations;
        }
        
        // Getters and Setters
        public String getExpression() { return expression; }
        public void setExpression(String expression) { this.expression = expression; }
        
        public String getCheckAgentId() { return checkAgentId; }
        public void setCheckAgentId(String checkAgentId) { this.checkAgentId = checkAgentId; }
        
        public Integer getMaxIterations() { return maxIterations; }
        public void setMaxIterations(Integer maxIterations) { this.maxIterations = maxIterations; }
    }
}