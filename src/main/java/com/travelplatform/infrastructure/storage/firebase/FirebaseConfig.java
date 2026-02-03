package com.travelplatform.infrastructure.storage.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Firebase Configuration for Storage integration.
 * 
 * This class configures Firebase Storage for media file uploads.
 * It uses Google Cloud Storage client library under the hood.
 */
@ApplicationScoped
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @ConfigProperty(name = "firebase.project.id")
    String projectId;

    @ConfigProperty(name = "firebase.storage.bucket")
    String storageBucket;

    @ConfigProperty(name = "firebase.credentials.json")
    String credentialsJson;

    @ConfigProperty(name = "firebase.storage.url.expiration.minutes", defaultValue = "15")
    int urlExpirationMinutes;

    private FirebaseApp firebaseApp;
    private Storage storage;

    /**
     * Initialize Firebase App with credentials.
     */
    @Produces
    @Singleton
    public FirebaseApp initializeFirebaseApp() {
        if (firebaseApp != null) {
            return firebaseApp;
        }

        try {
            // Parse credentials JSON from configuration
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))
            );

            FirebaseOptions options = FirebaseOptions.builder()
                    .setProjectId(projectId)
                    .setCredentials(credentials)
                    .setStorageBucket(storageBucket)
                    .build();

            firebaseApp = FirebaseApp.initializeApp(options);
            log.info("Firebase App initialized successfully for project: {}", projectId);
            return firebaseApp;

        } catch (IOException e) {
            log.error("Failed to initialize Firebase App", e);
            throw new RuntimeException("Failed to initialize Firebase App", e);
        }
    }

    /**
     * Get Firebase Storage client.
     */
    @Produces
    @Singleton
    public StorageClient getStorageClient() {
        if (firebaseApp == null) {
            initializeFirebaseApp();
        }
        return StorageClient.getInstance(firebaseApp);
    }

    /**
     * Get Google Cloud Storage client.
     */
    @Produces
    @Singleton
    public Storage getStorage() {
        if (storage != null) {
            return storage;
        }

        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8))
            );

            storage = StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(credentials)
                    .build()
                    .getService();

            log.info("Google Cloud Storage client initialized successfully");
            return storage;

        } catch (IOException e) {
            log.error("Failed to initialize Google Cloud Storage client", e);
            throw new RuntimeException("Failed to initialize Google Cloud Storage client", e);
        }
    }

    /**
     * Get the storage bucket name.
     */
    public String getStorageBucket() {
        return storageBucket;
    }

    /**
     * Get the URL expiration time in minutes.
     */
    public int getUrlExpirationMinutes() {
        return urlExpirationMinutes;
    }

    /**
     * Get the project ID.
     */
    public String getProjectId() {
        return projectId;
    }
}
