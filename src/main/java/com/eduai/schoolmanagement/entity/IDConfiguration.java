package com.eduai.schoolmanagement.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "id_configurations")
public class IDConfiguration extends BaseEntity {

    @NotBlank(message = "ID type is required")
    @Indexed(unique = true)
    private String idType; // STUDENT_ID, ADMISSION_NUMBER, ROLL_NUMBER, EMPLOYEE_ID

    @NotBlank(message = "Prefix is required")
    private String prefix; // e.g., "STU", "ADM", "EMP"

    @NotNull(message = "Length is required")
    @Min(value = 3, message = "Length must be at least 3")
    private Integer length; // Total length of the ID including prefix

    @NotNull(message = "Current counter is required")
    @Min(value = 0, message = "Counter cannot be negative")
    private Long currentCounter; // Current counter value

    private String separator; // Optional separator, e.g., "-", "/"

    private Boolean includeYear; // Whether to include current year

    private Boolean includeGradeSection; // For student IDs, include grade/section

    private String format; // Complete format pattern, e.g., "STU-{YEAR}-{GRADE}-{SECTION}-{COUNTER:4}"

    private String description; // Human readable description

    // Note: active field is inherited from BaseEntity

    // Static factory methods for default configurations
    public static IDConfiguration createStudentIDConfig() {
        IDConfiguration config = new IDConfiguration();
        config.setIdType("STUDENT_ID");
        config.setPrefix("STU");
        config.setLength(12);
        config.setCurrentCounter(0L);
        config.setSeparator("-");
        config.setIncludeYear(true);
        config.setIncludeGradeSection(true);
        config.setFormat("STU-{YEAR}-{GRADE}-{SECTION}-{COUNTER:4}");
        config.setDescription("Student ID format: STU-2024-10-A-0001");
        config.setActive(true);
        return config;
    }

    public static IDConfiguration createAdmissionNumberConfig() {
        IDConfiguration config = new IDConfiguration();
        config.setIdType("ADMISSION_NUMBER");
        config.setPrefix("ADM");
        config.setLength(10);
        config.setCurrentCounter(0L);
        config.setSeparator("");
        config.setIncludeYear(true);
        config.setIncludeGradeSection(false);
        config.setFormat("ADM{YEAR}{COUNTER:5}");
        config.setDescription("Admission Number format: ADM20240001");
        config.setActive(true);
        return config;
    }

    public static IDConfiguration createRollNumberConfig() {
        IDConfiguration config = new IDConfiguration();
        config.setIdType("ROLL_NUMBER");
        config.setPrefix("");
        config.setLength(3);
        config.setCurrentCounter(0L);
        config.setSeparator("");
        config.setIncludeYear(false);
        config.setIncludeGradeSection(false);
        config.setFormat("{COUNTER:3}");
        config.setDescription("Roll Number format: 001, 002, 003...");
        config.setActive(true);
        return config;
    }

    public static IDConfiguration createEmployeeIDConfig() {
        IDConfiguration config = new IDConfiguration();
        config.setIdType("EMPLOYEE_ID");
        config.setPrefix("EMP");
        config.setLength(8);
        config.setCurrentCounter(0L);
        config.setSeparator("");
        config.setIncludeYear(true);
        config.setIncludeGradeSection(false);
        config.setFormat("EMP{YEAR}{COUNTER:3}");
        config.setDescription("Employee ID format: EMP2024001");
        config.setActive(true);
        return config;
    }
}
