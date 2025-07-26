package com.eduai.schoolmanagement.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("prod") // Only run in production to avoid startup issues in development
public class IndexConfiguration implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(IndexConfiguration.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        createIndexesSafely();
    }

    private void createIndexesSafely() {
        try {
            // Create Student collection indexes
            createStudentIndexes();

            // Create User collection indexes
            createUserIndexes();

            // Create other collection indexes as needed

            logger.info("✅ All indexes created successfully");

        } catch (Exception e) {
            logger.error("❌ Error creating indexes: {}", e.getMessage());
            // Don't fail the application start if index creation fails
        }
    }

    private void createStudentIndexes() {
        try {
            MongoCollection<Document> students = mongoTemplate.getCollection("students");

            // Create studentId unique index (handle conflicts)
            createIndexSafely(students, "studentId",
                Indexes.ascending("studentId"),
                new IndexOptions().unique(true).name("studentId_unique"));

            // Create compound indexes using Document
            Document institutionStudentIndex = new Document()
                .append("institutionId", 1)
                .append("studentId", 1);
            createIndexSafely(students, "institution_student",
                institutionStudentIndex,
                new IndexOptions().name("institution_student_idx"));

            Document gradeSectionIndex = new Document()
                .append("grade", 1)
                .append("section", 1);
            createIndexSafely(students, "grade_section",
                gradeSectionIndex,
                new IndexOptions().name("grade_section_idx"));

            logger.info("✅ Student indexes created/verified");

        } catch (Exception e) {
            logger.warn("⚠️ Student index creation warning: {}", e.getMessage());
        }
    }

    private void createUserIndexes() {
        try {
            MongoCollection<Document> users = mongoTemplate.getCollection("users");

            // Create username unique index
            createIndexSafely(users, "username",
                Indexes.ascending("username"),
                new IndexOptions().unique(true).name("username_unique"));

            // Create email unique index
            createIndexSafely(users, "email",
                Indexes.ascending("email"),
                new IndexOptions().unique(true).name("email_unique"));

            logger.info("✅ User indexes created/verified");

        } catch (Exception e) {
            logger.warn("⚠️ User index creation warning: {}", e.getMessage());
        }
    }

    private void createIndexSafely(MongoCollection<Document> collection, String indexName,
                                 Bson keys, IndexOptions options) {
        try {
            // Check if index already exists
            boolean indexExists = false;
            for (Document index : collection.listIndexes()) {
                if (indexName.equals(index.getString("name")) ||
                    options.getName().equals(index.getString("name"))) {
                    indexExists = true;
                    logger.debug("Index {} already exists, skipping creation", options.getName());
                    break;
                }
            }

            if (!indexExists) {
                collection.createIndex(keys, options);
                logger.info("✅ Created index: {}", options.getName());
            }

        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("IndexKeySpecsConflict")) {
                logger.warn("⚠️ Index {} already exists with different specification, skipping", options.getName());
            } else {
                logger.error("❌ Failed to create index {}: {}", options.getName(), e.getMessage());
            }
        }
    }
}
