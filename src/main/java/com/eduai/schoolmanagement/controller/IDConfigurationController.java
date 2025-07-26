package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.entity.IDConfiguration;
import com.eduai.schoolmanagement.repository.IDConfigurationRepository;
import com.eduai.schoolmanagement.service.IDGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/id-configurations")
@RequiredArgsConstructor
@Tag(name = "ID Configuration Management", description = "Auto-ID generation configuration and management")
@CrossOrigin(origins = "*")
public class IDConfigurationController {

    private final IDConfigurationRepository idConfigRepository;
    private final IDGenerationService idGenerationService;

    @GetMapping
    @Operation(summary = "Get all ID configurations")
    public ResponseEntity<List<IDConfiguration>> getAllConfigurations() {
        List<IDConfiguration> configurations = idConfigRepository.findAll();
        return ResponseEntity.ok(configurations);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active ID configurations")
    public ResponseEntity<List<IDConfiguration>> getActiveConfigurations() {
        List<IDConfiguration> configurations = idConfigRepository.findByActiveTrue();
        return ResponseEntity.ok(configurations);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ID configuration by ID")
    public ResponseEntity<IDConfiguration> getConfigurationById(@PathVariable String id) {
        Optional<IDConfiguration> configuration = idConfigRepository.findById(id);
        return configuration.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{idType}")
    @Operation(summary = "Get ID configuration by type")
    public ResponseEntity<IDConfiguration> getConfigurationByType(@PathVariable String idType) {
        Optional<IDConfiguration> configuration = idConfigRepository.findByIdTypeAndActive(idType, true);
        return configuration.map(ResponseEntity::ok)
                           .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new ID configuration")
    public ResponseEntity<IDConfiguration> createConfiguration(@Valid @RequestBody IDConfiguration configuration) {
        try {
            // Check if configuration already exists for this ID type
            if (idConfigRepository.existsByIdType(configuration.getIdType())) {
                return ResponseEntity.badRequest().build();
            }

            IDConfiguration savedConfiguration = idConfigRepository.save(configuration);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedConfiguration);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update ID configuration")
    public ResponseEntity<IDConfiguration> updateConfiguration(
            @PathVariable String id,
            @Valid @RequestBody IDConfiguration configurationDetails) {
        try {
            Optional<IDConfiguration> configurationOpt = idConfigRepository.findById(id);
            if (configurationOpt.isPresent()) {
                IDConfiguration configuration = configurationOpt.get();

                configuration.setPrefix(configurationDetails.getPrefix());
                configuration.setLength(configurationDetails.getLength());
                configuration.setSeparator(configurationDetails.getSeparator());
                configuration.setIncludeYear(configurationDetails.getIncludeYear());
                configuration.setIncludeGradeSection(configurationDetails.getIncludeGradeSection());
                configuration.setFormat(configurationDetails.getFormat());
                configuration.setDescription(configurationDetails.getDescription());
                configuration.setActive(configurationDetails.isActive());

                IDConfiguration updatedConfiguration = idConfigRepository.save(configuration);
                return ResponseEntity.ok(updatedConfiguration);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete ID configuration")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable String id) {
        if (idConfigRepository.existsById(id)) {
            idConfigRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/reset-counter")
    @Operation(summary = "Reset counter for ID type")
    public ResponseEntity<Object> resetCounter(
            @RequestParam String idType,
            @RequestParam Long newCounter) {
        try {
            idGenerationService.resetCounter(idType, newCounter);
            return ResponseEntity.ok(Map.of(
                "message", "Counter reset successfully",
                "idType", idType,
                "newCounter", newCounter
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/preview")
    @Operation(summary = "Preview next ID that would be generated")
    public ResponseEntity<Object> previewNextId(
            @RequestParam String idType,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String section) {
        try {
            String nextId = idGenerationService.previewNextId(idType, grade, section);
            return ResponseEntity.ok(Map.of(
                "nextId", nextId,
                "idType", idType,
                "grade", grade != null ? grade : "N/A",
                "section", section != null ? section : "N/A"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate new ID")
    public ResponseEntity<Object> generateId(
            @RequestParam String idType,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String section) {
        try {
            String generatedId;
            switch (idType.toUpperCase()) {
                case "STUDENT_ID":
                    if (grade == null) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Grade is required for Student ID"));
                    }
                    generatedId = idGenerationService.generateStudentId(grade, section);
                    break;
                case "ADMISSION_NUMBER":
                    generatedId = idGenerationService.generateAdmissionNumber();
                    break;
                case "ROLL_NUMBER":
                    if (grade == null || section == null) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Grade and Section are required for Roll Number"));
                    }
                    generatedId = idGenerationService.generateRollNumber(grade, section);
                    break;
                case "EMPLOYEE_ID":
                    generatedId = idGenerationService.generateEmployeeId();
                    break;
                default:
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid ID type"));
            }

            return ResponseEntity.ok(Map.of(
                "generatedId", generatedId,
                "idType", idType,
                "grade", grade != null ? grade : "N/A",
                "section", section != null ? section : "N/A"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/validate-duplicate")
    @Operation(summary = "Check if ID already exists")
    public ResponseEntity<Object> validateDuplicate(
            @RequestParam String idType,
            @RequestParam String id) {
        boolean isDuplicate = idGenerationService.isIdDuplicate(idType, id);
        return ResponseEntity.ok(Map.of(
            "isDuplicate", isDuplicate,
            "idType", idType,
            "id", id
        ));
    }

    @PostMapping("/initialize-defaults")
    @Operation(summary = "Initialize default ID configurations")
    public ResponseEntity<Object> initializeDefaults() {
        try {
            idGenerationService.initializeDefaultConfigurations();
            return ResponseEntity.ok(Map.of("message", "Default configurations initialized successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get ID generation statistics")
    public ResponseEntity<Object> getStatistics() {
        List<IDConfiguration> configurations = idConfigRepository.findByActiveTrue();
        return ResponseEntity.ok(Map.of(
            "totalConfigurations", configurations.size(),
            "configurations", configurations.stream().map(config -> Map.of(
                "idType", config.getIdType(),
                "currentCounter", config.getCurrentCounter(),
                "format", config.getFormat(),
                "active", config.isActive()
            )).toList()
        ));
    }
}
