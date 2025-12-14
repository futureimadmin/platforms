package com.nebula.shared.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of loop types for loop execution steps.
 */
public enum LoopType {
    WHILE("while"),
    FOR("for"),
    FOREACH("foreach");
    
    private final String value;
    
    LoopType(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    public static LoopType fromValue(String value) {
        for (LoopType type : LoopType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown loop type: " + value);
    }
}