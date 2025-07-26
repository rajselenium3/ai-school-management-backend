package com.eduai.schoolmanagement.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Document(collection = "salary_structures")
@CompoundIndexes({
    @CompoundIndex(name = "institution_grade_idx", def = "{'institutionId': 1, 'gradeCode': 1}"),
    @CompoundIndex(name = "institution_designation_idx", def = "{'institutionId': 1, 'designation': 1}"),
    @CompoundIndex(name = "effective_date_idx", def = "{'effectiveFromDate': 1, 'effectiveToDate': 1}")
})
public class SalaryStructure {

    @Id
    private String salaryStructureId;

    @Indexed
    private String institutionId;

    @Indexed
    private String gradeCode; // A1, A2, B1, B2, etc.
    private String gradeName;
    private String designation;
    private String employeeType; // TEACHING, NON_TEACHING, ADMINISTRATIVE, CONTRACT
    private String department;

    // Effective dates
    @Indexed
    private LocalDate effectiveFromDate;
    private LocalDate effectiveToDate;
    private Boolean isActive;
    private Boolean isDefault;

    // Basic salary range
    private Double minBasicSalary;
    private Double maxBasicSalary;
    private Double defaultBasicSalary;

    // Salary components structure
    private List<SalaryComponent> components;

    // Allowances (as percentage of basic salary or fixed amount)
    private Map<String, AllowanceStructure> allowances;

    // Deductions (as percentage of basic/gross salary or fixed amount)
    private Map<String, DeductionStructure> deductions;

    // Increment rules
    private IncrementStructure incrementStructure;

    // Bonus and incentive structure
    private BonusStructure bonusStructure;

    // Working conditions
    private Integer standardWorkingDays;
    private Double standardWorkingHours;
    private Boolean overtimeEligible;
    private Double overtimeRate; // Hourly rate multiplier

    // Leave entitlements
    private Integer annualLeaveEntitlement;
    private Integer sickLeaveEntitlement;
    private Integer casualLeaveEntitlement;
    private Integer maternityLeaveEntitlement;

    // Tax and compliance
    private Boolean pfApplicable;
    private Boolean esiApplicable;
    private Boolean professionalTaxApplicable;
    private String taxExemptionCategory;

    // Approval and audit
    private StructureStatus status;
    private String approvedBy;
    private LocalDateTime approvedDate;
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    // Version control
    private String version;
    private String previousVersionId;
    private String revisionNotes;

    public enum StructureStatus {
        DRAFT("Draft"),
        PENDING_APPROVAL("Pending Approval"),
        APPROVED("Approved"),
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        SUPERSEDED("Superseded");

        private final String displayName;

        StructureStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static class SalaryComponent {
        private String componentCode;
        private String componentName;
        private ComponentType componentType;
        private SalaryComponent.CalculationType calculationType;
        private Double value; // Amount or percentage
        private Double minValue;
        private Double maxValue;
        private Boolean isMandatory;
        private Boolean isTaxable;
        private String description;

        public enum ComponentType {
            EARNING, DEDUCTION
        }

        public enum CalculationType {
            FIXED_AMOUNT("Fixed Amount"),
            PERCENTAGE_OF_BASIC("Percentage of Basic Salary"),
            PERCENTAGE_OF_GROSS("Percentage of Gross Salary"),
            CALCULATED("Calculated");

            private final String displayName;

            CalculationType(String displayName) {
                this.displayName = displayName;
            }

            public String getDisplayName() {
                return displayName;
            }
        }

        // Constructors
        public SalaryComponent() {}

        public SalaryComponent(String componentCode, String componentName, ComponentType componentType,
                             SalaryComponent.CalculationType calculationType, Double value) {
            this.componentCode = componentCode;
            this.componentName = componentName;
            this.componentType = componentType;
            this.calculationType = calculationType;
            this.value = value;
            this.isMandatory = false;
            this.isTaxable = true;
        }

        // Getters and Setters
        public String getComponentCode() { return componentCode; }
        public void setComponentCode(String componentCode) { this.componentCode = componentCode; }

        public String getComponentName() { return componentName; }
        public void setComponentName(String componentName) { this.componentName = componentName; }

        public ComponentType getComponentType() { return componentType; }
        public void setComponentType(ComponentType componentType) { this.componentType = componentType; }

        public CalculationType getCalculationType() { return calculationType; }
        public void setCalculationType(CalculationType calculationType) { this.calculationType = calculationType; }

        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }

        public Double getMinValue() { return minValue; }
        public void setMinValue(Double minValue) { this.minValue = minValue; }

        public Double getMaxValue() { return maxValue; }
        public void setMaxValue(Double maxValue) { this.maxValue = maxValue; }

        public Boolean getIsMandatory() { return isMandatory; }
        public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }

        public Boolean getIsTaxable() { return isTaxable; }
        public void setIsTaxable(Boolean isTaxable) { this.isTaxable = isTaxable; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class AllowanceStructure {
        private String allowanceCode;
        private String allowanceName;
        private SalaryComponent.CalculationType calculationType;
        private Double value;
        private Double maxLimit;
        private Boolean isTaxable;
        private Boolean isConditional;
        private String conditions; // e.g., "Applicable only for teaching staff"

        public AllowanceStructure() {}

        public AllowanceStructure(String allowanceCode, String allowanceName,
                                SalaryComponent.CalculationType calculationType, Double value) {
            this.allowanceCode = allowanceCode;
            this.allowanceName = allowanceName;
            this.calculationType = calculationType;
            this.value = value;
            this.isTaxable = true;
            this.isConditional = false;
        }

        // Getters and Setters
        public String getAllowanceCode() { return allowanceCode; }
        public void setAllowanceCode(String allowanceCode) { this.allowanceCode = allowanceCode; }

        public String getAllowanceName() { return allowanceName; }
        public void setAllowanceName(String allowanceName) { this.allowanceName = allowanceName; }

        public SalaryComponent.CalculationType getCalculationType() { return calculationType; }
        public void setCalculationType(SalaryComponent.CalculationType calculationType) { this.calculationType = calculationType; }

        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }

        public Double getMaxLimit() { return maxLimit; }
        public void setMaxLimit(Double maxLimit) { this.maxLimit = maxLimit; }

        public Boolean getIsTaxable() { return isTaxable; }
        public void setIsTaxable(Boolean isTaxable) { this.isTaxable = isTaxable; }

        public Boolean getIsConditional() { return isConditional; }
        public void setIsConditional(Boolean isConditional) { this.isConditional = isConditional; }

        public String getConditions() { return conditions; }
        public void setConditions(String conditions) { this.conditions = conditions; }
    }

    public static class DeductionStructure {
        private String deductionCode;
        private String deductionName;
        private SalaryComponent.CalculationType calculationType;
        private Double value;
        private Double maxLimit;
        private Boolean isMandatory;
        private Boolean isStatutory; // PF, ESI, etc.

        public DeductionStructure() {}

        public DeductionStructure(String deductionCode, String deductionName,
                                SalaryComponent.CalculationType calculationType, Double value) {
            this.deductionCode = deductionCode;
            this.deductionName = deductionName;
            this.calculationType = calculationType;
            this.value = value;
            this.isMandatory = false;
            this.isStatutory = false;
        }

        // Getters and Setters
        public String getDeductionCode() { return deductionCode; }
        public void setDeductionCode(String deductionCode) { this.deductionCode = deductionCode; }

        public String getDeductionName() { return deductionName; }
        public void setDeductionName(String deductionName) { this.deductionName = deductionName; }

        public SalaryComponent.CalculationType getCalculationType() { return calculationType; }
        public void setCalculationType(SalaryComponent.CalculationType calculationType) { this.calculationType = calculationType; }

        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }

        public Double getMaxLimit() { return maxLimit; }
        public void setMaxLimit(Double maxLimit) { this.maxLimit = maxLimit; }

        public Boolean getIsMandatory() { return isMandatory; }
        public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }

        public Boolean getIsStatutory() { return isStatutory; }
        public void setIsStatutory(Boolean isStatutory) { this.isStatutory = isStatutory; }
    }

    public static class IncrementStructure {
        private IncrementType incrementType;
        private Double incrementPercentage;
        private Double incrementAmount;
        private Integer incrementFrequencyMonths; // 12 for annual
        private LocalDate nextIncrementDue;
        private Double maxSalaryLimit;
        private Boolean performanceLinked;
        private String incrementCriteria;

        public enum IncrementType {
            PERCENTAGE("Percentage"),
            FIXED_AMOUNT("Fixed Amount"),
            GRADE_PROMOTION("Grade Promotion"),
            PERFORMANCE_BASED("Performance Based");

            private final String displayName;

            IncrementType(String displayName) {
                this.displayName = displayName;
            }

            public String getDisplayName() {
                return displayName;
            }
        }

        public IncrementStructure() {}

        // Getters and Setters
        public IncrementType getIncrementType() { return incrementType; }
        public void setIncrementType(IncrementType incrementType) { this.incrementType = incrementType; }

        public Double getIncrementPercentage() { return incrementPercentage; }
        public void setIncrementPercentage(Double incrementPercentage) { this.incrementPercentage = incrementPercentage; }

        public Double getIncrementAmount() { return incrementAmount; }
        public void setIncrementAmount(Double incrementAmount) { this.incrementAmount = incrementAmount; }

        public Integer getIncrementFrequencyMonths() { return incrementFrequencyMonths; }
        public void setIncrementFrequencyMonths(Integer incrementFrequencyMonths) { this.incrementFrequencyMonths = incrementFrequencyMonths; }

        public LocalDate getNextIncrementDue() { return nextIncrementDue; }
        public void setNextIncrementDue(LocalDate nextIncrementDue) { this.nextIncrementDue = nextIncrementDue; }

        public Double getMaxSalaryLimit() { return maxSalaryLimit; }
        public void setMaxSalaryLimit(Double maxSalaryLimit) { this.maxSalaryLimit = maxSalaryLimit; }

        public Boolean getPerformanceLinked() { return performanceLinked; }
        public void setPerformanceLinked(Boolean performanceLinked) { this.performanceLinked = performanceLinked; }

        public String getIncrementCriteria() { return incrementCriteria; }
        public void setIncrementCriteria(String incrementCriteria) { this.incrementCriteria = incrementCriteria; }
    }

    public static class BonusStructure {
        private Boolean annualBonusApplicable;
        private Double annualBonusPercentage;
        private Double annualBonusAmount;
        private Boolean performanceBonusApplicable;
        private Double maxPerformanceBonusPercentage;
        private Boolean attendanceBonusApplicable;
        private Double attendanceBonusThreshold; // Minimum attendance percentage
        private Double attendanceBonusAmount;
        private String bonusCriteria;

        public BonusStructure() {}

        // Getters and Setters
        public Boolean getAnnualBonusApplicable() { return annualBonusApplicable; }
        public void setAnnualBonusApplicable(Boolean annualBonusApplicable) { this.annualBonusApplicable = annualBonusApplicable; }

        public Double getAnnualBonusPercentage() { return annualBonusPercentage; }
        public void setAnnualBonusPercentage(Double annualBonusPercentage) { this.annualBonusPercentage = annualBonusPercentage; }

        public Double getAnnualBonusAmount() { return annualBonusAmount; }
        public void setAnnualBonusAmount(Double annualBonusAmount) { this.annualBonusAmount = annualBonusAmount; }

        public Boolean getPerformanceBonusApplicable() { return performanceBonusApplicable; }
        public void setPerformanceBonusApplicable(Boolean performanceBonusApplicable) { this.performanceBonusApplicable = performanceBonusApplicable; }

        public Double getMaxPerformanceBonusPercentage() { return maxPerformanceBonusPercentage; }
        public void setMaxPerformanceBonusPercentage(Double maxPerformanceBonusPercentage) { this.maxPerformanceBonusPercentage = maxPerformanceBonusPercentage; }

        public Boolean getAttendanceBonusApplicable() { return attendanceBonusApplicable; }
        public void setAttendanceBonusApplicable(Boolean attendanceBonusApplicable) { this.attendanceBonusApplicable = attendanceBonusApplicable; }

        public Double getAttendanceBonusThreshold() { return attendanceBonusThreshold; }
        public void setAttendanceBonusThreshold(Double attendanceBonusThreshold) { this.attendanceBonusThreshold = attendanceBonusThreshold; }

        public Double getAttendanceBonusAmount() { return attendanceBonusAmount; }
        public void setAttendanceBonusAmount(Double attendanceBonusAmount) { this.attendanceBonusAmount = attendanceBonusAmount; }

        public String getBonusCriteria() { return bonusCriteria; }
        public void setBonusCriteria(String bonusCriteria) { this.bonusCriteria = bonusCriteria; }
    }

    // Constructors
    public SalaryStructure() {
        this.components = new ArrayList<>();
        this.allowances = new HashMap<>();
        this.deductions = new HashMap<>();
        this.incrementStructure = new IncrementStructure();
        this.bonusStructure = new BonusStructure();
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.status = StructureStatus.DRAFT;
        this.isActive = false;
        this.isDefault = false;
        this.version = "1.0";
        this.pfApplicable = true;
        this.esiApplicable = true;
        this.professionalTaxApplicable = true;
        this.overtimeEligible = false;
        this.overtimeRate = 1.5;
    }

    public SalaryStructure(String institutionId, String gradeCode, String designation) {
        this();
        this.institutionId = institutionId;
        this.gradeCode = gradeCode;
        this.designation = designation;
    }

    // Main getters and setters
    public String getSalaryStructureId() { return salaryStructureId; }
    public void setSalaryStructureId(String salaryStructureId) { this.salaryStructureId = salaryStructureId; }

    public String getInstitutionId() { return institutionId; }
    public void setInstitutionId(String institutionId) { this.institutionId = institutionId; }

    public String getGradeCode() { return gradeCode; }
    public void setGradeCode(String gradeCode) { this.gradeCode = gradeCode; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getEmployeeType() { return employeeType; }
    public void setEmployeeType(String employeeType) { this.employeeType = employeeType; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDate getEffectiveFromDate() { return effectiveFromDate; }
    public void setEffectiveFromDate(LocalDate effectiveFromDate) { this.effectiveFromDate = effectiveFromDate; }

    public LocalDate getEffectiveToDate() { return effectiveToDate; }
    public void setEffectiveToDate(LocalDate effectiveToDate) { this.effectiveToDate = effectiveToDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }

    public Double getMinBasicSalary() { return minBasicSalary; }
    public void setMinBasicSalary(Double minBasicSalary) { this.minBasicSalary = minBasicSalary; }

    public Double getMaxBasicSalary() { return maxBasicSalary; }
    public void setMaxBasicSalary(Double maxBasicSalary) { this.maxBasicSalary = maxBasicSalary; }

    public Double getDefaultBasicSalary() { return defaultBasicSalary; }
    public void setDefaultBasicSalary(Double defaultBasicSalary) { this.defaultBasicSalary = defaultBasicSalary; }

    public List<SalaryComponent> getComponents() { return components; }
    public void setComponents(List<SalaryComponent> components) { this.components = components; }

    public Map<String, AllowanceStructure> getAllowances() { return allowances; }
    public void setAllowances(Map<String, AllowanceStructure> allowances) { this.allowances = allowances; }

    public Map<String, DeductionStructure> getDeductions() { return deductions; }
    public void setDeductions(Map<String, DeductionStructure> deductions) { this.deductions = deductions; }

    public IncrementStructure getIncrementStructure() { return incrementStructure; }
    public void setIncrementStructure(IncrementStructure incrementStructure) { this.incrementStructure = incrementStructure; }

    public BonusStructure getBonusStructure() { return bonusStructure; }
    public void setBonusStructure(BonusStructure bonusStructure) { this.bonusStructure = bonusStructure; }

    public StructureStatus getStatus() { return status; }
    public void setStatus(StructureStatus status) { this.status = status; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    // Helper methods
    public void addComponent(SalaryComponent component) {
        if (this.components == null) {
            this.components = new ArrayList<>();
        }
        this.components.add(component);
    }

    public void addAllowance(String code, AllowanceStructure allowance) {
        if (this.allowances == null) {
            this.allowances = new HashMap<>();
        }
        this.allowances.put(code, allowance);
    }

    public void addDeduction(String code, DeductionStructure deduction) {
        if (this.deductions == null) {
            this.deductions = new HashMap<>();
        }
        this.deductions.put(code, deduction);
    }

    public void updateLastModified(String modifiedBy) {
        this.lastModifiedDate = LocalDateTime.now();
        this.lastModifiedBy = modifiedBy;
    }

    public Boolean isCurrentlyActive() {
        LocalDate now = LocalDate.now();
        return this.isActive && this.status == StructureStatus.ACTIVE &&
               (this.effectiveFromDate == null || !this.effectiveFromDate.isAfter(now)) &&
               (this.effectiveToDate == null || !this.effectiveToDate.isBefore(now));
    }

    public Double calculateMaxPossibleSalary() {
        Double maxSalary = this.maxBasicSalary != null ? this.maxBasicSalary : this.defaultBasicSalary;

        if (maxSalary != null && this.allowances != null) {
            for (AllowanceStructure allowance : this.allowances.values()) {
                if (allowance.getCalculationType() == SalaryComponent.CalculationType.PERCENTAGE_OF_BASIC) {
                    maxSalary += (maxSalary * allowance.getValue() / 100);
                } else if (allowance.getCalculationType() == SalaryComponent.CalculationType.FIXED_AMOUNT) {
                    maxSalary += allowance.getValue();
                }
            }
        }

        return maxSalary;
    }

    @Override
    public String toString() {
        return "SalaryStructure{" +
                "salaryStructureId='" + salaryStructureId + '\'' +
                ", gradeCode='" + gradeCode + '\'' +
                ", designation='" + designation + '\'' +
                ", defaultBasicSalary=" + defaultBasicSalary +
                ", status=" + status +
                ", isActive=" + isActive +
                '}';
    }
}
