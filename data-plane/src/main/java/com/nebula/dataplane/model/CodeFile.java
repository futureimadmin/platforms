package com.nebula.dataplane.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CodeFile {
    private String fileName;
    private String content;

    // Getters and Setters
    @JsonProperty("fileName")
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}