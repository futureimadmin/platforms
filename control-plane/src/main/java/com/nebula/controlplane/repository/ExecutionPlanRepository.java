package com.nebula.controlplane.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.nebula.shared.domain.ExecutionPlanDocument;
import com.nebula.shared.model.ExecutionPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Repository for ExecutionPlan document operations in Firestore
 */
@Repository
public class ExecutionPlanRepository {

    @Autowired
    private final Firestore firestore;

    @Autowired
    private final ObjectMapper objectMapper;

    private static final String COLLECTION_NAME = "executionPlans";

    public ExecutionPlanRepository(Firestore firestore, ObjectMapper objectMapper) {
        this.firestore = firestore;
        this.objectMapper = objectMapper;
    }

    public Mono<ExecutionPlanDocument> save(ExecutionPlan plan) {
        return Mono.fromCallable(() -> {
            ExecutionPlanDocument doc = ExecutionPlanDocument.from(plan);
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
            doc.setId(docRef.getId());

            // Convert to Firestore compatible map
            Map<String, Object> firestoreDoc = objectMapper.convertValue(doc,
                    new TypeReference<Map<String, Object>>() {});

            // Save to Firestore
            docRef.set(firestoreDoc).get();
            return doc;
        });
    }

    public Mono<ExecutionPlan> findById(String id) {
        return Mono.fromCallable(() -> {
            DocumentSnapshot document = firestore.collection(COLLECTION_NAME)
                    .document(id)
                    .get()
                    .get();

            if (!document.exists()) {
                return null;
            }

            // Convert Firestore document to our wrapper
            ExecutionPlanDocument doc = objectMapper.convertValue(
                    document.getData(),
                    ExecutionPlanDocument.class
            );

            return doc.toExecutionPlan();
        });
    }

    public Mono<List<ExecutionPlan>> findAll() {
        return Mono.fromCallable(() -> {
            List<ExecutionPlan> plans = new ArrayList<>();
            
            try {
                firestore.collection(COLLECTION_NAME)
                        .get()
                        .get()
                        .getDocuments()
                        .forEach(document -> {
                            if (document.exists()) {
                                ExecutionPlanDocument doc = objectMapper.convertValue(
                                        document.getData(),
                                        ExecutionPlanDocument.class
                                );
                                plans.add(doc.toExecutionPlan());
                            }
                        });
            } catch (Exception e) {
                throw new RuntimeException("Error fetching execution plans from Firestore", e);
            }
            
            return plans;
        });
    }
}