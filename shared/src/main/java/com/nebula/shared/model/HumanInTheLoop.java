package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Human-in-the-loop configuration for execution plans
 */
public class HumanInTheLoop {
    @JsonProperty("enabled")
    private Boolean enabled;
    
    @JsonProperty("approvalRequired")
    private List<String> approvalRequired;
    
    @Valid
    @JsonProperty("teamsIntegration")
    private TeamsIntegration teamsIntegration;
    
    // Constructors
    public HumanInTheLoop() {}
    
    public HumanInTheLoop(Boolean enabled, List<String> approvalRequired, TeamsIntegration teamsIntegration) {
        this.enabled = enabled != null ? enabled : false;
        this.approvalRequired = approvalRequired;
        this.teamsIntegration = teamsIntegration;
    }
    
    // Getters and Setters
    public Boolean isEnabled() { 
        return enabled != null ? enabled : false; 
    }
    
    public void setEnabled(Boolean enabled) { 
        this.enabled = enabled; 
    }
    
    public List<String> getApprovalRequired() { 
        return approvalRequired; 
    }
    
    public void setApprovalRequired(List<String> approvalRequired) { 
        this.approvalRequired = approvalRequired; 
    }
    
    public TeamsIntegration getTeamsIntegration() { 
        return teamsIntegration; 
    }
    
    public void setTeamsIntegration(TeamsIntegration teamsIntegration) { 
        this.teamsIntegration = teamsIntegration; 
    }
}
