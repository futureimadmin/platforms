package com.nebula.controlplane.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

/**
 * Configuration for Google Cloud Firestore
 */
@Configuration
public class FirestoreConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirestoreConfig.class);

    @Value("${spring.cloud.gcp.project-id:nebula-platform}")
    private String projectId;

    @Value("${spring.cloud.gcp.firestore.database-id:(default)}")
    private String databaseId;

    @Value("${spring.cloud.gcp.credentials.location:}")
    private String credentialsLocation;

    @Bean
    @Primary
    public Firestore firestore() throws IOException {
        logger.info("Initializing Firestore with project ID: {} and database ID: {}", projectId, databaseId);

        FirestoreOptions.Builder optionsBuilder = FirestoreOptions.newBuilder()
                .setProjectId(projectId)
                .setDatabaseId(databaseId);

        // Use service account credentials if specified, otherwise use default credentials
        if (credentialsLocation != null && !credentialsLocation.isEmpty()) {
            logger.info("Using service account credentials from: {}", credentialsLocation);
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    getClass().getResourceAsStream(credentialsLocation)
            );
            optionsBuilder.setCredentials(credentials);
        } else {
            logger.info("Using default Google Cloud credentials");
            optionsBuilder.setCredentials(GoogleCredentials.getApplicationDefault());
        }

        Firestore firestore = optionsBuilder.build().getService();
        logger.info("Firestore initialized successfully");
        return firestore;
    }
}