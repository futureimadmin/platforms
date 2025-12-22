package com.nebula.controlplane.client;

import com.nebula.shared.model.ExecutionPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DataPlaneClient {
    private static final Logger logger = LoggerFactory.getLogger(DataPlaneClient.class);
    private final WebClient webClient;
    private final String baseUrl;

    public DataPlaneClient(WebClient.Builder webClientBuilder,
            @Value("${nebula.data-plane.base-url:http://localhost:8081}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.webClient = webClientBuilder
                .baseUrl(this.baseUrl)
                .build();
    }

    public Void sendPlan(String planId) {
        logger.info("Sending execution plan to data plane: {}", planId);
        
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/nebula-data-plane/api/v1/platform/create/{planId}")
                        .build(planId))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"planId\": \"" + planId + "\"}")
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    logger.error("Error triggering execution on data plane for plan: {}", planId, e);
                    return Mono.error(e);
                }).block();
    }
}
