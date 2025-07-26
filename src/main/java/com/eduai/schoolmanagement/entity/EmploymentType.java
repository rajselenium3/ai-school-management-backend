package com.eduai.schoolmanagement.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "employment_types")
public class EmploymentType extends BaseEntity {

    @NotBlank(message = "Employment type code is required")
    @Indexed(unique = true)
    private String typeCode; // e.g., "FULL_TIME", "PART_TIME", "CONTRACT", "SUBSTITUTE"

    @NotBlank(message = "Employment type name is required")
    private String typeName; // e.g., "Full Time", "Part Time", "Contract", "Substitute"

    private String description; // Description of employment type

    @NotNull(message = "Minimum hours per week is required")
    @Min(value = 1, message = "Minimum hours must be at least 1")
    @Max(value = 168, message = "Maximum hours cannot exceed 168 per week")
    private Integer minHoursPerWeek; // Minimum hours per week

    @NotNull(message = "Maximum hours per week is required")
    @Min(value = 1, message = "Maximum hours must be at least 1")
    @Max(value = 168, message = "Maximum hours cannot exceed 168 per week")
    private Integer maxHoursPerWeek; // Maximum hours per week

    private Boolean eligibleForBenefits; // Whether eligible for benefits

    private Boolean requiresContract; // Whether requires a formal contract

    private Integer contractDurationMonths; // Contract duration in months (if applicable)

    private String color; // Color code for UI display

    private Integer displayOrder; // Order for display

    // Static factory methods for common employment types
    public static EmploymentType createFullTime() {
        EmploymentType type = new EmploymentType();
        type.setTypeCode("FULL_TIME");
        type.setTypeName("Full Time");
        type.setDescription("Full-time permanent employment");
        type.setMinHoursPerWeek(35);
        type.setMaxHoursPerWeek(40);
        type.setEligibleForBenefits(true);
        type.setRequiresContract(false);
        type.setColor("#4CAF50");
        type.setDisplayOrder(1);
        type.setActive(true);
        return type;
    }

    public static EmploymentType createPartTime() {
        EmploymentType type = new EmploymentType();
        type.setTypeCode("PART_TIME");
        type.setTypeName("Part Time");
        type.setDescription("Part-time employment");
        type.setMinHoursPerWeek(10);
        type.setMaxHoursPerWeek(34);
        type.setEligibleForBenefits(false);
        type.setRequiresContract(false);
        type.setColor("#FF9800");
        type.setDisplayOrder(2);
        type.setActive(true);
        return type;
    }

    public static EmploymentType createContract() {
        EmploymentType type = new EmploymentType();
        type.setTypeCode("CONTRACT");
        type.setTypeName("Contract");
        type.setDescription("Contract-based employment");
        type.setMinHoursPerWeek(20);
        type.setMaxHoursPerWeek(40);
        type.setEligibleForBenefits(false);
        type.setRequiresContract(true);
        type.setContractDurationMonths(12);
        type.setColor("#2196F3");
        type.setDisplayOrder(3);
        type.setActive(true);
        return type;
    }

    public static EmploymentType createSubstitute() {
        EmploymentType type = new EmploymentType();
        type.setTypeCode("SUBSTITUTE");
        type.setTypeName("Substitute");
        type.setDescription("Substitute teacher - as needed basis");
        type.setMinHoursPerWeek(0);
        type.setMaxHoursPerWeek(40);
        type.setEligibleForBenefits(false);
        type.setRequiresContract(false);
        type.setColor("#9C27B0");
        type.setDisplayOrder(4);
        type.setActive(true);
        return type;
    }

    public static EmploymentType createIntern() {
        EmploymentType type = new EmploymentType();
        type.setTypeCode("INTERN");
        type.setTypeName("Intern");
        type.setDescription("Internship position");
        type.setMinHoursPerWeek(15);
        type.setMaxHoursPerWeek(30);
        type.setEligibleForBenefits(false);
        type.setRequiresContract(true);
        type.setContractDurationMonths(6);
        type.setColor("#607D8B");
        type.setDisplayOrder(5);
        type.setActive(true);
        return type;
    }

    // Helper methods
    public boolean isFullTime() {
        return "FULL_TIME".equals(typeCode);
    }

    public boolean isPartTime() {
        return "PART_TIME".equals(typeCode);
    }

    public boolean isContract() {
        return "CONTRACT".equals(typeCode);
    }

    public boolean isSubstitute() {
        return "SUBSTITUTE".equals(typeCode);
    }

    public boolean requiresMinimumHours() {
        return minHoursPerWeek != null && minHoursPerWeek > 0;
    }

    public String getHoursRange() {
        if (minHoursPerWeek == null && maxHoursPerWeek == null) {
            return "Hours not specified";
        }
        if (minHoursPerWeek == null) {
            return "Up to " + maxHoursPerWeek + " hours/week";
        }
        if (maxHoursPerWeek == null) {
            return "Minimum " + minHoursPerWeek + " hours/week";
        }
        if (minHoursPerWeek.equals(maxHoursPerWeek)) {
            return minHoursPerWeek + " hours/week";
        }
        return minHoursPerWeek + "-" + maxHoursPerWeek + " hours/week";
    }
}
