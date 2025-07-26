package com.eduai.schoolmanagement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Slf4j
public class MongoHealthCheck {

    @Autowired(required = false)
    private MongoTemplate mongoTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void checkMongoConnection() {
        if (mongoTemplate == null) {
            log.warn("⚠️ MongoDB template not available");
            printMongoSetupInstructions();
            return;
        }

        try {
            // Try to ping MongoDB
            mongoTemplate.getCollection("health_check");
            log.info("✅ MongoDB connection successful!");
            log.info("📊 Connected to database: {}", mongoTemplate.getDb().getName());
            log.info("🚀 AI School Management System backend is ready!");

        } catch (Exception e) {
            log.error("❌ MongoDB connection failed: {}", e.getMessage());
            printMongoSetupInstructions();
            log.info("🔄 Application will continue running, but database operations will fail");
        }
    }

    private void printMongoSetupInstructions() {
        log.info("");
        log.info("🛠️ MongoDB Setup Instructions:");
        log.info("─────────────────────────────────────");
        log.info("📥 Option 1: Install MongoDB locally");
        log.info("   Windows: https://www.mongodb.com/try/download/community");
        log.info("   Mac:     brew install mongodb-community && brew services start mongodb-community");
        log.info("   Linux:   sudo apt-get install mongodb && sudo systemctl start mongodb");
        log.info("");
        log.info("🌐 Option 2: Use MongoDB Atlas (Cloud)");
        log.info("   Run: mvn spring-boot:run -Dspring.profiles.active=prod");
        log.info("");
        log.info("🐳 Option 3: Docker MongoDB");
        log.info("   Run: docker run -d -p 27017:27017 --name mongodb mongo:latest");
        log.info("─────────────────────────────────────");
        log.info("");
    }
}
