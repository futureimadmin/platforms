package com.nebula.dataplane.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CodingOutput {
    @JsonProperty("codeFiles")
    private List<CodeFile> codeFiles;

    // Getters and Setters
    public List<CodeFile> getCodeFiles() {
        return codeFiles;
    }

    public void setCodeFiles(List<CodeFile> codeFiles) {
        this.codeFiles = codeFiles;
    }
}