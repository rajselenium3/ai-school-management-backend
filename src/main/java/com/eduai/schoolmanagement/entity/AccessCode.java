package com.eduai.schoolmanagement.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "access_codes")
public class AccessCode extends BaseEntity {

    @NotBlank(message = "Access code is required")
    @Indexed(unique = true)
    private String accessCode; // Unique access code

    @NotBlank(message = "Code type is required")
    private String codeType; // STUDENT, PARENT

    @NotBlank(message = "User type is required")
    private String userType; // STUDENT_PORTAL, PARENT_PORTAL

    // Reference to the entity this code is for
    private String entityId; // Student ID or Parent ID

    private String entityType; // STUDENT, PARENT

    // Student/Parent information for code generation
    private String studentId; // If this is for a student
    private String parentEmail; // If this is for a parent
    private String studentName; // Student name for reference
    private String parentName; // Parent name for reference

    @NotNull(message = "Generated date is required")
    private LocalDateTime generatedDate; // When the code was generated

    private LocalDateTime expiryDate; // When the code expires (optional)

    private LocalDateTime usedDate; // When the code was used

    private Boolean isUsed; // Whether the code has been used

    private Boolean isExpired; // Whether the code has expired

    private String generatedBy; // Who generated the code (system, admin, etc.)

    private String usedBy; // Who used the code

    private Integer maxUsageCount; // Maximum number of times this code can be used

    private Integer currentUsageCount; // Current usage count

    private String status; // ACTIVE, USED, EXPIRED, REVOKED

    private String notes; // Additional notes

    // Static factory methods
    public static AccessCode createStudentAccessCode(String studentId, String studentName) {
        AccessCode accessCode = new AccessCode();
        accessCode.setAccessCode(generateRandomCode("STU"));
        accessCode.setCodeType("STUDENT");
        accessCode.setUserType("STUDENT_PORTAL");
        accessCode.setEntityId(studentId);
        accessCode.setEntityType("STUDENT");
        accessCode.setStudentId(studentId);
        accessCode.setStudentName(studentName);
        accessCode.setGeneratedDate(LocalDateTime.now());
        accessCode.setExpiryDate(LocalDateTime.now().plusDays(365)); // Valid for 1 year
        accessCode.setIsUsed(false);
        accessCode.setIsExpired(false);
        accessCode.setGeneratedBy("SYSTEM");
        accessCode.setMaxUsageCount(1);
        accessCode.setCurrentUsageCount(0);
        accessCode.setStatus("ACTIVE");
        accessCode.setActive(true);
        return accessCode;
    }

    public static AccessCode createParentAccessCode(String parentEmail, String parentName, String studentId, String studentName) {
        AccessCode accessCode = new AccessCode();
        accessCode.setAccessCode(generateRandomCode("PAR"));
        accessCode.setCodeType("PARENT");
        accessCode.setUserType("PARENT_PORTAL");
        accessCode.setEntityId(parentEmail); // Use email as entity ID for parents
        accessCode.setEntityType("PARENT");
        accessCode.setParentEmail(parentEmail);
        accessCode.setParentName(parentName);
        accessCode.setStudentId(studentId);
        accessCode.setStudentName(studentName);
        accessCode.setGeneratedDate(LocalDateTime.now());
        accessCode.setExpiryDate(LocalDateTime.now().plusDays(365)); // Valid for 1 year
        accessCode.setIsUsed(false);
        accessCode.setIsExpired(false);
        accessCode.setGeneratedBy("SYSTEM");
        accessCode.setMaxUsageCount(1);
        accessCode.setCurrentUsageCount(0);
        accessCode.setStatus("ACTIVE");
        accessCode.setActive(true);
        return accessCode;
    }

    // Helper method to generate random access code
    private static String generateRandomCode(String prefix) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder(prefix);
        code.append("-");

        // Generate 3 groups of 4 characters each
        for (int group = 0; group < 3; group++) {
            if (group > 0) code.append("-");
            for (int i = 0; i < 4; i++) {
                int index = (int) (Math.random() * chars.length());
                code.append(chars.charAt(index));
            }
        }

        return code.toString(); // e.g., STU-AB12-CD34-EF56
    }

    // Helper methods
    public boolean isValid() {
        return isActive() && !isUsed && !isExpired && !isExpiredByDate();
    }

    public boolean isExpiredByDate() {
        return expiryDate != null && LocalDateTime.now().isAfter(expiryDate);
    }

    public boolean canBeUsed() {
        return isValid() &&
               (maxUsageCount == null || currentUsageCount == null || currentUsageCount < maxUsageCount);
    }

    public void markAsUsed(String usedBy) {
        this.isUsed = true;
        this.usedDate = LocalDateTime.now();
        this.usedBy = usedBy;
        this.status = "USED";
        if (currentUsageCount == null) currentUsageCount = 0;
        this.currentUsageCount++;
    }

    public void markAsExpired() {
        this.isExpired = true;
        this.status = "EXPIRED";
    }

    public void revoke(String reason) {
        this.status = "REVOKED";
        this.notes = (notes != null ? notes + "; " : "") + "Revoked: " + reason;
        this.setActive(false);
    }

    public void regenerate() {
        this.accessCode = generateRandomCode(codeType.equals("STUDENT") ? "STU" : "PAR");
        this.generatedDate = LocalDateTime.now();
        this.expiryDate = LocalDateTime.now().plusDays(365);
        this.isUsed = false;
        this.isExpired = false;
        this.usedDate = null;
        this.usedBy = null;
        this.currentUsageCount = 0;
        this.status = "ACTIVE";
        this.setActive(true);
    }

    public long getDaysUntilExpiry() {
        if (expiryDate == null) return -1;
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(expiryDate) ?
               java.time.temporal.ChronoUnit.DAYS.between(now, expiryDate) : 0;
    }

    public String getFormattedAccessCode() {
        return accessCode;
    }

    public String getDisplayName() {
        if ("STUDENT".equals(codeType)) {
            return "Student Access Code for " + (studentName != null ? studentName : "Student");
        } else {
            return "Parent Access Code for " + (parentName != null ? parentName : "Parent") +
                   " (Child: " + (studentName != null ? studentName : "Student") + ")";
        }
    }
}
