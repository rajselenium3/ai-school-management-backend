package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.entity.AccessCode;
import com.eduai.schoolmanagement.service.AccessCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/access-codes")
@RequiredArgsConstructor
@Tag(name = "Access Code Management", description = "Manage access codes for student and parent portal registration")
@CrossOrigin(origins = "*")
public class AccessCodeController {

    private final AccessCodeService accessCodeService;

    // ====================
    // ACCESS CODE MANAGEMENT
    // ====================

    @GetMapping
    @Operation(summary = "Get all access codes")
    public ResponseEntity<List<AccessCode>> getAllAccessCodes() {
        List<AccessCode> accessCodes = accessCodeService.getAllAccessCodes();
        return ResponseEntity.ok(accessCodes);
    }

    @GetMapping("/{accessCode}")
    @Operation(summary = "Get access code by code")
    public ResponseEntity<AccessCode> getAccessCodeByCode(@PathVariable String accessCode) {
        Optional<AccessCode> code = accessCodeService.getAccessCodeByCode(accessCode);
        return code.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get access codes for a student")
    public ResponseEntity<List<AccessCode>> getAccessCodesByStudentId(@PathVariable String studentId) {
        List<AccessCode> accessCodes = accessCodeService.getAccessCodesByStudentId(studentId);
        return ResponseEntity.ok(accessCodes);
    }

    @GetMapping("/parent/{parentEmail}")
    @Operation(summary = "Get access codes for a parent")
    public ResponseEntity<List<AccessCode>> getAccessCodesByParentEmail(@PathVariable String parentEmail) {
        List<AccessCode> accessCodes = accessCodeService.getAccessCodesByParentEmail(parentEmail);
        return ResponseEntity.ok(accessCodes);
    }

    @GetMapping("/valid")
    @Operation(summary = "Get all valid unused access codes")
    public ResponseEntity<List<AccessCode>> getValidUnusedCodes() {
        List<AccessCode> accessCodes = accessCodeService.getValidUnusedCodes();
        return ResponseEntity.ok(accessCodes);
    }

    @GetMapping("/valid/{codeType}")
    @Operation(summary = "Get valid unused access codes by type")
    public ResponseEntity<List<AccessCode>> getValidUnusedCodesByType(@PathVariable String codeType) {
        List<AccessCode> accessCodes = accessCodeService.getValidUnusedCodesByType(codeType);
        return ResponseEntity.ok(accessCodes);
    }

    // ====================
    // ACCESS CODE GENERATION
    // ====================

    @PostMapping("/generate/student/{studentId}")
    @Operation(summary = "Generate access code for a student")
    public ResponseEntity<AccessCode> generateStudentAccessCode(@PathVariable String studentId) {
        try {
            AccessCode accessCode = accessCodeService.generateStudentAccessCode(studentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(accessCode);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/generate/student-and-parents/{studentId}")
    @Operation(summary = "Generate access codes for a student and all linked parents")
    public ResponseEntity<List<AccessCode>> generateStudentAndParentAccessCodes(@PathVariable String studentId) {
        try {
            List<AccessCode> accessCodes = accessCodeService.generateStudentAndParentAccessCodes(studentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(accessCodes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/generate/parents/{studentId}")
    @Operation(summary = "Generate access codes for all parents of a student")
    public ResponseEntity<List<AccessCode>> generateParentAccessCodes(@PathVariable String studentId) {
        try {
            List<AccessCode> accessCodes = accessCodeService.generateParentAccessCodes(studentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(accessCodes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/generate/parent")
    @Operation(summary = "Generate access code for a specific parent and student")
    public ResponseEntity<AccessCode> generateParentAccessCode(
            @RequestParam String parentEmail,
            @RequestParam String studentId) {
        try {
            AccessCode accessCode = accessCodeService.generateParentAccessCode(parentEmail, studentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(accessCode);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ====================
    // ACCESS CODE VALIDATION AND USAGE
    // ====================

    @PostMapping("/validate")
    @Operation(summary = "Validate an access code")
    public ResponseEntity<Object> validateAccessCode(
            @RequestParam String accessCode,
            @RequestParam String userType) {
        boolean isValid = accessCodeService.validateAccessCode(accessCode, userType);
        return ResponseEntity.ok(Map.of(
            "valid", isValid,
            "accessCode", accessCode,
            "userType", userType
        ));
    }

    @PostMapping("/use")
    @Operation(summary = "Use an access code")
    public ResponseEntity<Object> useAccessCode(
            @RequestParam String accessCode,
            @RequestParam String usedBy,
            @RequestParam String userType) {
        Map<String, Object> result = accessCodeService.useAccessCode(accessCode, usedBy, userType);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // ====================
    // ACCESS CODE REGENERATION
    // ====================

    @PutMapping("/regenerate/{accessCodeId}")
    @Operation(summary = "Regenerate an access code")
    public ResponseEntity<AccessCode> regenerateAccessCode(@PathVariable String accessCodeId) {
        try {
            AccessCode accessCode = accessCodeService.regenerateAccessCode(accessCodeId);
            return ResponseEntity.ok(accessCode);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/regenerate/student/{studentId}")
    @Operation(summary = "Regenerate access code for a student")
    public ResponseEntity<AccessCode> regenerateStudentAccessCode(@PathVariable String studentId) {
        try {
            AccessCode accessCode = accessCodeService.regenerateStudentAccessCode(studentId);
            return ResponseEntity.ok(accessCode);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/regenerate/parents/{studentId}")
    @Operation(summary = "Regenerate access codes for all parents of a student")
    public ResponseEntity<List<AccessCode>> regenerateParentAccessCodes(@PathVariable String studentId) {
        try {
            List<AccessCode> accessCodes = accessCodeService.regenerateParentAccessCodes(studentId);
            return ResponseEntity.ok(accessCodes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ====================
    // ACCESS CODE MANAGEMENT
    // ====================

    @PutMapping("/revoke/{accessCodeId}")
    @Operation(summary = "Revoke an access code")
    public ResponseEntity<Object> revokeAccessCode(
            @PathVariable String accessCodeId,
            @RequestParam String reason) {
        try {
            accessCodeService.revokeAccessCode(accessCodeId, reason);
            return ResponseEntity.ok(Map.of(
                "message", "Access code revoked successfully",
                "accessCodeId", accessCodeId,
                "reason", reason
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/cleanup-expired")
    @Operation(summary = "Clean up expired and old access codes")
    public ResponseEntity<Object> cleanupExpiredCodes() {
        try {
            accessCodeService.cleanupExpiredCodes();
            return ResponseEntity.ok(Map.of("message", "Expired codes cleaned up successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ====================
    // STATISTICS AND REPORTING
    // ====================

    @GetMapping("/statistics")
    @Operation(summary = "Get access code statistics")
    public ResponseEntity<Object> getAccessCodeStatistics() {
        Map<String, Object> statistics = accessCodeService.getAccessCodeStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/reports/generated")
    @Operation(summary = "Get access codes generated within date range")
    public ResponseEntity<List<AccessCode>> getAccessCodesGeneratedBetween(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<AccessCode> accessCodes = accessCodeService.getAccessCodesGeneratedBetween(start, end);
            return ResponseEntity.ok(accessCodes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/reports/used")
    @Operation(summary = "Get access codes used within date range")
    public ResponseEntity<List<AccessCode>> getAccessCodesUsedBetween(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<AccessCode> accessCodes = accessCodeService.getAccessCodesUsedBetween(start, end);
            return ResponseEntity.ok(accessCodes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ====================
    // BULK OPERATIONS
    // ====================

    @PostMapping("/bulk/generate-for-students")
    @Operation(summary = "Generate access codes for multiple students")
    public ResponseEntity<Object> bulkGenerateForStudents(@RequestBody List<String> studentIds) {
        try {
            int totalGenerated = 0;
            for (String studentId : studentIds) {
                try {
                    List<AccessCode> codes = accessCodeService.generateStudentAndParentAccessCodes(studentId);
                    totalGenerated += codes.size();
                } catch (Exception e) {
                    // Log error but continue with other students
                }
            }
            return ResponseEntity.ok(Map.of(
                "message", "Bulk generation completed",
                "studentsProcessed", studentIds.size(),
                "totalCodesGenerated", totalGenerated
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
