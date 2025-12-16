package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Shared context for all agents in the execution plan
 */
public class SharedContext {
    @JsonProperty("variables")
    private Map<String, Object> variables;
    
    @JsonProperty("secrets")
    private List<String> secrets;
    
    // Constructors
    public SharedContext() {}
    
    public SharedContext(Map<String, Object> variables, List<String> secrets) {
        this.variables = variables;
        this.secrets = secrets;
    }
    
    // Getters and Setters
    public Map<String, Object> getVariables() { 
        return variables; 
    }
    
    public void setVariables(Map<String, Object> variables) { 
        this.variables = variables; 
    }
    
    public List<String> getSecrets() { 
        return secrets; 
    }
    
    public void setSecrets(List<String> secrets) { 
        this.secrets = secrets; 
    }
}
