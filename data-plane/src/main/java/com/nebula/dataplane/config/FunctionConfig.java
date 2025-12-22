package com.nebula.dataplane.config;

import com.nebula.dataplane.function.DataPlaneAgentFunction;
import com.nebula.dataplane.service.AgentExecutionService;
import com.nebula.dataplane.service.DataPlaneService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the Cloud Function.
 */
@Configuration
public class FunctionConfig {

    @Bean
    public DataPlaneAgentFunction dataPlaneAgentFunction(AgentExecutionService agentService, DataPlaneService dataPlaneService) {
        return new DataPlaneAgentFunction(agentService, dataPlaneService);
    }
}
