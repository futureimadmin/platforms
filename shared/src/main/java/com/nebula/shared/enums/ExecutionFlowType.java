package com.nebula.shared.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of execution flow types in the Nebula platform.
 */
public enum ExecutionFlowType {
    SEQUENTIAL("sequential"),
    PARALLEL("parallel"),
    CONDITIONAL("conditional"),
    LOOP("loop"),
    HYBRID("hybrid"),
    HIERARCHICAL("hierarchical");
    
    private final String value;
    
    ExecutionFlowType(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    public static ExecutionFlowType fromValue(String value) {
        for (ExecutionFlowType type : ExecutionFlowType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown execution flow type: " + value);
    }
}