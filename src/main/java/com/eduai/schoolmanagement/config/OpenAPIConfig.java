package com.eduai.schoolmanagement.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI School Management System API")
                        .description("Comprehensive AI-powered school management system backend API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EduAI Team")
                                .email("support@eduai.com")
                                .url("https://eduai.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.eduai.com")
                                .description("Production Server")
                ));
    }
}
