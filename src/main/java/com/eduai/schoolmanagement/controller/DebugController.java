package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.config.DatabaseCleanupComponent;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/debug")
@ConditionalOnProperty(name = "app.mongodb.debug.enabled", havingValue = "true", matchIfMissing = false)
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired(required = false)
    private DatabaseCleanupComponent cleanupComponent;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();

        try {
            // Test MongoDB connection
            String dbName = mongoTemplate.getDb().getName();
            health.put("status", "UP");
            health.put("database", dbName);
            health.put("timestamp", System.currentTimeMillis());

            // Get collection count
            health.put("collections", mongoTemplate.getCollectionNames().size());

            logger.info("✅ Health check passed");
            return ResponseEntity.ok(health);

        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            logger.error("❌ Health check failed: {}", e.getMessage());
            return ResponseEntity.status(500).body(health);
        }
    }

    @GetMapping("/mongodb/info")
    public ResponseEntity<Map<String, Object>> mongodbInfo() {
        Map<String, Object> info = new HashMap<>();

        try {
            info.put("database", mongoTemplate.getDb().getName());
            info.put("collections", new ArrayList<>(mongoTemplate.getCollectionNames()));

            // Get students collection info
            if (mongoTemplate.collectionExists("students")) {
                MongoCollection<Document> students = mongoTemplate.getCollection("students");
                long count = students.countDocuments();
                info.put("studentsCount", count);

                // Get indexes info
                List<Map<String, Object>> indexes = new ArrayList<>();
                for (Document index : students.listIndexes()) {
                    Map<String, Object> indexInfo = new HashMap<>();
                    indexInfo.put("name", index.getString("name"));
                    indexInfo.put("key", index.get("key"));
                    indexInfo.put("unique", index.getBoolean("unique", false));
                    indexes.add(indexInfo);
                }
                info.put("studentsIndexes", indexes);
            }

            return ResponseEntity.ok(info);

        } catch (Exception e) {
            logger.error("❌ Error getting MongoDB info: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/mongodb/cleanup-indexes")
    public ResponseEntity<Map<String, String>> cleanupIndexes() {
        try {
            if (cleanupComponent != null) {
                cleanupComponent.dropConflictingIndexes();
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Index cleanup completed"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Cleanup component not available"
                ));
            }

        } catch (Exception e) {
            logger.error("❌ Error during index cleanup: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/test-student-creation")
    public ResponseEntity<Map<String, Object>> testStudentCreation() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Test if we can access the students collection
            boolean exists = mongoTemplate.collectionExists("students");
            result.put("studentsCollectionExists", exists);

            if (exists) {
                MongoCollection<Document> students = mongoTemplate.getCollection("students");
                long count = students.countDocuments();
                result.put("currentStudentCount", count);
            }

            result.put("status", "success");
            result.put("message", "Student collection test completed");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("❌ Error testing student creation: {}", e.getMessage());
            result.put("status", "error");
            result.put("message", e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}
