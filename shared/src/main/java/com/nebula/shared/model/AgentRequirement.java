package com.nebula.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nebula.shared.enums.AgentType;
import com.nebula.shared.enums.ProgrammingLanguage;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

/**
 * Represents requirements for generating an agent.
 */
public class AgentRequirement {
    
    @NotBlank
    @JsonProperty("agentId")
    private String agentId;
    
    @NotBlank
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("type")
    private AgentType type;
    
    @JsonProperty("language")
    private ProgrammingLanguage language;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("capabilities")
    private List<String> capabilities;
    
    @JsonProperty("dependencies")
    private List<String> dependencies;
    
    @JsonProperty("configuration")
    private Map<String, Object> configuration;
    
    @JsonProperty("tools")
    private List<String> tools;
    
    @JsonProperty("priority")
    private Integer priority;
    
    // Constructors
    public AgentRequirement() {}
    
    public AgentRequirement(String agentId, String name, AgentType type, ProgrammingLanguage language) {
        this.agentId = agentId;
        this.name = name;
        this.type = type;
        this.language = language;
    }
    
    // Getters and Setters
    public String getAgentId() { return agentId; }
    public void setAgentId(String agentId) { this.agentId = agentId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public AgentType getType() { return type; }
    public void setType(AgentType type) { this.type = type; }
    
    public ProgrammingLanguage getLanguage() { return language; }
    public void setLanguage(ProgrammingLanguage language) { this.language = language; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
    
    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    
    public Map<String, Object> getConfiguration() { return configuration; }
    public void setConfiguration(Map<String, Object> configuration) { this.configuration = configuration; }
    
    public List<String> getTools() { return tools; }
    public void setTools(List<String> tools) { this.tools = tools; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
}