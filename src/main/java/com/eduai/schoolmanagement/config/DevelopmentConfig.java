package com.eduai.schoolmanagement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

@Configuration
@Profile("dev")
@Slf4j
public class DevelopmentConfig {

    @PostConstruct
    public void logDevelopmentInfo() {
        log.info("🚀 AI School Management System - Development Mode");
        log.info("🔧 Running in DEVELOPMENT profile");
        log.info("📊 MongoDB Connection: Will attempt localhost:27017");
        log.info("💡 If MongoDB is not available, install it locally or switch to production profile");
        log.info("🛠️ To install MongoDB locally:");
        log.info("   - Windows: Download from https://www.mongodb.com/try/download/community");
        log.info("   - Mac: brew install mongodb-community");
        log.info("   - Linux: sudo apt-get install mongodb");
        log.info("🎯 Alternative: Use production profile with MongoDB Atlas");
        log.info("   Command: mvn spring-boot:run -Dspring.profiles.active=prod");
    }
}
