package com.nebula.shared.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of tool types available in the Nebula platform.
 */
public enum ToolType {
    DATABASE("database"),
    API("api"),
    FILE("file"),
    NOTIFICATION("notification"),
    INTEGRATION("integration"), 
    FUNCTION("function");
    
    private final String value;
    
    ToolType(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    public static ToolType fromValue(String value) {
        for (ToolType type : ToolType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown tool type: " + value);
    }
}