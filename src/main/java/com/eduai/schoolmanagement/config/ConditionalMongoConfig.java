package com.eduai.schoolmanagement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;
import java.net.Socket;

@Configuration
@Profile("dev")
@EnableConfigurationProperties(MongoProperties.class)
@Slf4j
public class ConditionalMongoConfig {

    @PostConstruct
    public void checkMongoAvailability() {
        boolean mongoAvailable = isMongoAvailable("localhost", 27017);

        if (mongoAvailable) {
            log.info("✅ MongoDB detected at localhost:27017");
            log.info("🚀 Application will connect to MongoDB");
        } else {
            log.warn("⚠️ MongoDB not available at localhost:27017");
            log.info("🔧 Application will start without MongoDB");
            printSetupInstructions();
        }
    }

    private boolean isMongoAvailable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new java.net.InetSocketAddress(host, port), 1000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void printSetupInstructions() {
        log.info("");
        log.info("🛠️ Quick MongoDB Setup Options:");
        log.info("─────────────────────────────────────");
        log.info("🐳 RECOMMENDED: Docker (Easiest)");
        log.info("   docker run -d -p 27017:27017 --name mongodb mongo:latest");
        log.info("");
        log.info("💻 Local Installation:");
        log.info("   Windows: https://www.mongodb.com/try/download/community");
        log.info("   Mac:     brew install mongodb-community && brew services start mongodb-community");
        log.info("   Linux:   sudo apt-get install mongodb && sudo systemctl start mongodb");
        log.info("");
        log.info("🌐 Use Cloud MongoDB:");
        log.info("   mvn spring-boot:run -Dspring.profiles.active=prod");
        log.info("─────────────────────────────────────");
        log.info("");
    }
}
