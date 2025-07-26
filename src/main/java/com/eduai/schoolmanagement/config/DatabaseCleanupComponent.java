package com.eduai.schoolmanagement.config;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@ConditionalOnProperty(name = "app.mongodb.cleanup.enabled", havingValue = "true", matchIfMissing = false)
public class DatabaseCleanupComponent {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseCleanupComponent.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostConstruct
    public void logDatabaseInfo() {
        try {
            logger.info("🔍 MongoDB Database Information:");
            logger.info("Database Name: {}", mongoTemplate.getDb().getName());

            // List all collections
            logger.info("📚 Available Collections:");
            for (String collectionName : mongoTemplate.getCollectionNames()) {
                logger.info("  - {}", collectionName);

                // Log indexes for students collection specifically
                if ("students".equals(collectionName)) {
                    logCollectionIndexes(collectionName);
                }
            }

        } catch (Exception e) {
            logger.error("❌ Error getting database info: {}", e.getMessage());
        }
    }

    private void logCollectionIndexes(String collectionName) {
        try {
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
            logger.info("📊 Indexes for collection '{}':", collectionName);

            for (Document index : collection.listIndexes()) {
                String indexName = index.getString("name");
                Document key = index.get("key", Document.class);
                Boolean unique = index.getBoolean("unique");

                logger.info("  - Index: '{}', Key: {}, Unique: {}",
                    indexName, key, unique != null ? unique : false);
            }

        } catch (Exception e) {
            logger.error("❌ Error listing indexes for {}: {}", collectionName, e.getMessage());
        }
    }

    public void dropConflictingIndexes() {
        try {
            logger.info("🧹 Checking for conflicting indexes...");

            MongoCollection<Document> students = mongoTemplate.getCollection("students");

            // Drop specific problematic indexes if they exist
            try {
                students.dropIndex("studentId");
                logger.info("✅ Dropped conflicting 'studentId' index");
            } catch (Exception e) {
                logger.debug("Index 'studentId' not found or already dropped");
            }

        } catch (Exception e) {
            logger.error("❌ Error during index cleanup: {}", e.getMessage());
        }
    }
}
