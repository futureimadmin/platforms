package com.nebula.controlplane.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Configuration for Google Cloud Firestore
 */
@Configuration
public class FirestoreConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirestoreConfig.class);
    private static final String INIT_FLAG = "_initialized";

    @Value("${spring.cloud.gcp.project-id:nebula-platform}")
    private String projectId;

    @Value("${spring.cloud.gcp.firestore.database-id:(default)}")
    private String databaseId;

    @Value("${spring.cloud.gcp.credentials.location:}")
    private String credentialsLocation;

    // List of required collections in Firestore
    private static final List<String> REQUIRED_COLLECTIONS = Arrays.asList(
        "executionPlans",  // Must match COLLECTION_NAME in ExecutionPlanRepository
        "agents",
        "execution_logs"
    );

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
        
        // Initialize collections
        initializeCollections(firestore);
        
        return firestore;
    }
    
    private void initializeCollections(Firestore firestore) {
        try {
            // Check if we've already initialized the collections
            boolean isInitialized = List.of(firestore.listCollections())
                    .stream()
                    .filter(collection -> REQUIRED_COLLECTIONS.contains(collection))
                    .count() == REQUIRED_COLLECTIONS.size();

            if (!isInitialized) {
                logger.info("Initializing Firestore collections");
                
                // Create all required collections by writing an initialization document
                for (String collection : REQUIRED_COLLECTIONS) {
                    logger.info("Initializing collection: {}", collection);
                    if(firestore.collection(collection).getId() != null) {
                        continue;
                    }
                    // Create a document with initialization metadata instead of an empty map
                    Map<String, Object> initData = new HashMap<>();
                    initData.put("initializedAt", Timestamp.now());
                    initData.put("purpose", "Collection initialization document");
                    firestore.collection(collection).document("flows").set(initData).get();
                }

                // Mark as initialized
                firestore.collection("_metadata")
                        .document("init")
                        .set(new HashMap<String, Object>() {{
                            put("key", INIT_FLAG);
                            put("initializedAt", Timestamp.now());
                        }});
                for (String collection : REQUIRED_COLLECTIONS) {
                    firestore.collection(collection)
                            .document("init")
                            .set(new HashMap<String, Object>() {{
                                put("key", INIT_FLAG);
                                put("initializedAt", Timestamp.now());
                            }});
                }
                logger.info("Firestore collections initialized successfully");
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error initializing Firestore collections", e);
            throw new RuntimeException("Failed to initialize Firestore collections", e);
        }
    }
}