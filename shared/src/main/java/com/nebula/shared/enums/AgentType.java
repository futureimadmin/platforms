package com.nebula.shared.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of agent types in the Nebula platform.
 */
public enum AgentType {
    CONTROL_PLANE("control-plane"),
    DATA_PLANE("data-plane"),
    TOOL("tool"),
    HUMAN_INTERFACE("human-interface"), 
    DATA_AGENT("data-agent"), 
    TOOL_AGENT("tool-agent"), 
    ORCHESTRATION_AGENT("orchestration-agent");
    
    private final String value;
    
    AgentType(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    public static AgentType fromValue(String value) {
        for (AgentType type : AgentType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown agent type: " + value);
    }
}