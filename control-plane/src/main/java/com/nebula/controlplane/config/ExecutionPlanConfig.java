package com.nebula.controlplane.config;

import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Schema;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class ExecutionPlanConfig {
    private final ResourceLoader resourceLoader;
    private Schema rootSchema;
    
    public ExecutionPlanConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @PostConstruct
    public void init() throws IOException {
        // Load the schema from the classpath
        Resource resource = resourceLoader.getResource("classpath:execution-plan.json");
        try (InputStream inputStream = resource.getInputStream()) {
            String schemaJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            rootSchema = Schema.fromJson(schemaJson);
        } catch (IOException e) {
            throw new IOException("Failed to load execution plan schema from classpath", e);
        }
    }

    public GenerateContentConfig generateContentConfig() {
        if (rootSchema == null) {
            throw new IllegalStateException("Schema not initialized. Make sure the application context is properly set up.");
        }
        return GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .responseSchema(rootSchema)
                .build();
    }
}
