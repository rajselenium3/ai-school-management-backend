package com.eduai.schoolmanagement.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "fees")
public class Fee extends BaseEntity {

    @NotBlank(message = "Fee name is required")
    private String feeName;

    private String description;

    @NotNull(message = "Fee amount is required")
    @Min(value = 0, message = "Fee amount must be positive")
    private Double amount;

    @NotBlank(message = "Fee type is required")
    private String feeType; // TUITION, LIBRARY, LAB, SPORTS, TRANSPORT, HOSTEL, EXAMINATION, MISCELLANEOUS

    @NotBlank(message = "Fee category is required")
    private String category; // MONTHLY, QUARTERLY, YEARLY, ONE_TIME

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    // Applicability
    private List<String> applicableGrades; // Which grades this fee applies to
    private List<String> applicableDepartments; // Which departments
    private boolean mandatory; // Is this fee mandatory

    // Payment schedule
    private LocalDate dueDate;
    private LocalDate lateFeeDueDate;
    private Double lateFeeAmount;
    private Double lateFeePercentage;

    // Discount and scholarship
    private List<DiscountRule> discountRules;
    private boolean scholarshipEligible;

    // Status and metadata
    private String status; // ACTIVE, INACTIVE, SUSPENDED
    private Map<String, Object> metadata; // Additional fee-specific data

    @Data
    public static class DiscountRule {
        private String discountType; // PERCENTAGE, FIXED_AMOUNT, SIBLING, MERIT
        private Double discountValue;
        private String criteria; // Criteria for applying discount
        private LocalDate validFrom;
        private LocalDate validTo;
        private boolean active;
    }
}
