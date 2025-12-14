package com.nebula.shared.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of programming languages supported for agent implementation.
 */
public enum ProgrammingLanguage {
    JAVA("java"),
    PYTHON("python"),
    JAVASCRIPT("javascript"),
    TYPESCRIPT("typescript"),
    GO("go"),
    RUST("rust");
    
    private final String value;
    
    ProgrammingLanguage(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    public static ProgrammingLanguage fromValue(String value) {
        for (ProgrammingLanguage language : ProgrammingLanguage.values()) {
            if (language.value.equals(value)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Unknown programming language: " + value);
    }
}