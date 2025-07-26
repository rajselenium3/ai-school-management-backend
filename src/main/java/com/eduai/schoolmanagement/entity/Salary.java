package com.eduai.schoolmanagement.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Document(collection = "salaries")
@CompoundIndexes({
    @CompoundIndex(name = "employee_payroll_idx", def = "{'employeeId': 1, 'payrollMonth': 1, 'payrollYear': 1}"),
    @CompoundIndex(name = "institution_payroll_idx", def = "{'institutionId': 1, 'payrollMonth': 1, 'payrollYear': 1}"),
    @CompoundIndex(name = "department_payroll_idx", def = "{'departmentId': 1, 'payrollMonth': 1, 'payrollYear': 1}")
})
public class Salary {

    @Id
    private String salaryId;

    @Indexed
    private String employeeId;

    @Indexed
    private String institutionId;

    @Indexed
    private String departmentId;

    private String departmentName;
    private String employeeName;
    private String employeeCode;
    private String designation;
    private String employeeGrade;
    private String employeeType; // TEACHING, NON_TEACHING, ADMINISTRATIVE, CONTRACT

    // Payroll period
    @Indexed
    private Integer payrollMonth; // 1-12
    @Indexed
    private Integer payrollYear;
    private YearMonth payrollPeriod;
    private LocalDate payrollDate;
    private LocalDate salaryProcessedDate;

    // Basic salary structure
    private Double basicSalary;
    private Double grossSalary;
    private Double netSalary;
    private Double totalEarnings;
    private Double totalDeductions;

    // Salary components - Earnings
    private Map<String, Double> earnings; // HRA, DA, Medical, Transport, etc.
    private Double houseRentAllowance;
    private Double dearnessAllowance;
    private Double medicalAllowance;
    private Double transportAllowance;
    private Double specialAllowance;
    private Double performanceBonus;
    private Double overtimePay;
    private Double festivalBonus;
    private Double teachingAllowance; // For teaching staff

    // Salary components - Deductions
    private Map<String, Double> deductions; // PF, ESI, Tax, etc.
    private Double providentFund;
    private Double employeeStateInsurance;
    private Double incomeTax;
    private Double professionalTax;
    private Double loanDeduction;
    private Double advanceDeduction;
    private Double lateDeduction;
    private Double absentDeduction;
    private Double otherDeductions;

    // Tax calculations
    private Double taxableIncome;
    private Double taxExemptions;
    private Double tdsDeducted;
    private String taxSlab;

    // Attendance integration
    private Integer workingDays;
    private Integer presentDays;
    private Integer absentDays;
    private Integer leaveDays;
    private Integer paidLeaveDays;
    private Integer unpaidLeaveDays;
    private Double attendancePercentage;
    private Boolean salaryOnHold; // For attendance issues

    // Provident Fund details
    private Double pfEmployeeContribution;
    private Double pfEmployerContribution;
    private Double pfTotal;
    private String pfAccountNumber;

    // Bank details
    private String bankName;
    private String bankAccountNumber;
    private String ifscCode;
    private String bankBranch;

    // Status and approval
    private SalaryStatus status;
    private Boolean isProcessed;
    private Boolean isApproved;
    private Boolean isPaid;
    private String approvedBy;
    private LocalDateTime approvedDate;
    private String approvalComments;

    // Payment details
    private PaymentMethod paymentMethod;
    private String transactionId;
    private LocalDate paymentDate;
    private String paymentReference;

    // Salary revision
    private String salaryRevisionId;
    private LocalDate lastRevisionDate;
    private Double previousBasicSalary;
    private Double incrementAmount;
    private Double incrementPercentage;
    private String revisionReason;

    // Additional details
    private String financialYear;
    private String salarySlipNumber;
    private Boolean salarySlipGenerated;
    private String salarySlipPath; // Path to generated PDF
    private String currency;

    // Metadata
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private String createdBy;
    private String lastModifiedBy;
    private String processedBy;

    // Compliance and reporting
    private String panNumber;
    private String uanNumber; // Universal Account Number for PF
    private String esiNumber;
    private Boolean form16Generated;
    private Boolean tdsCompliant;

    public enum SalaryStatus {
        DRAFT("Draft"),
        PENDING_APPROVAL("Pending Approval"),
        APPROVED("Approved"),
        PROCESSED("Processed"),
        PAID("Paid"),
        ON_HOLD("On Hold"),
        CANCELLED("Cancelled"),
        REVISED("Revised");

        private final String displayName;

        SalaryStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum PaymentMethod {
        BANK_TRANSFER("Bank Transfer"),
        CASH("Cash"),
        CHEQUE("Cheque"),
        UPI("UPI"),
        NEFT("NEFT"),
        RTGS("RTGS");

        private final String displayName;

        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructors
    public Salary() {
        this.earnings = new HashMap<>();
        this.deductions = new HashMap<>();
        this.createdDate = LocalDateTime.now();
        this.lastModifiedDate = LocalDateTime.now();
        this.status = SalaryStatus.DRAFT;
        this.isProcessed = false;
        this.isApproved = false;
        this.isPaid = false;
        this.salaryOnHold = false;
        this.salarySlipGenerated = false;
        this.currency = "INR";
        this.paymentMethod = PaymentMethod.BANK_TRANSFER;
        this.form16Generated = false;
        this.tdsCompliant = true;
    }

    public Salary(String employeeId, String institutionId, Integer payrollMonth, Integer payrollYear) {
        this();
        this.employeeId = employeeId;
        this.institutionId = institutionId;
        this.payrollMonth = payrollMonth;
        this.payrollYear = payrollYear;
        this.payrollPeriod = YearMonth.of(payrollYear, payrollMonth);
        this.financialYear = calculateFinancialYear(payrollYear, payrollMonth);
    }

    // Getters and Setters
    public String getSalaryId() { return salaryId; }
    public void setSalaryId(String salaryId) { this.salaryId = salaryId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getInstitutionId() { return institutionId; }
    public void setInstitutionId(String institutionId) { this.institutionId = institutionId; }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getEmployeeGrade() { return employeeGrade; }
    public void setEmployeeGrade(String employeeGrade) { this.employeeGrade = employeeGrade; }

    public String getEmployeeType() { return employeeType; }
    public void setEmployeeType(String employeeType) { this.employeeType = employeeType; }

    public Integer getPayrollMonth() { return payrollMonth; }
    public void setPayrollMonth(Integer payrollMonth) { this.payrollMonth = payrollMonth; }

    public Integer getPayrollYear() { return payrollYear; }
    public void setPayrollYear(Integer payrollYear) { this.payrollYear = payrollYear; }

    public YearMonth getPayrollPeriod() { return payrollPeriod; }
    public void setPayrollPeriod(YearMonth payrollPeriod) { this.payrollPeriod = payrollPeriod; }

    public LocalDate getPayrollDate() { return payrollDate; }
    public void setPayrollDate(LocalDate payrollDate) { this.payrollDate = payrollDate; }

    public LocalDate getSalaryProcessedDate() { return salaryProcessedDate; }
    public void setSalaryProcessedDate(LocalDate salaryProcessedDate) { this.salaryProcessedDate = salaryProcessedDate; }

    public Double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(Double basicSalary) { this.basicSalary = basicSalary; }

    public Double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(Double grossSalary) { this.grossSalary = grossSalary; }

    public Double getNetSalary() { return netSalary; }
    public void setNetSalary(Double netSalary) { this.netSalary = netSalary; }

    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }

    public Double getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(Double totalDeductions) { this.totalDeductions = totalDeductions; }

    public Map<String, Double> getEarnings() { return earnings; }
    public void setEarnings(Map<String, Double> earnings) { this.earnings = earnings; }

    public Map<String, Double> getDeductions() { return deductions; }
    public void setDeductions(Map<String, Double> deductions) { this.deductions = deductions; }

    // Individual earning getters and setters
    public Double getHouseRentAllowance() { return houseRentAllowance; }
    public void setHouseRentAllowance(Double houseRentAllowance) { this.houseRentAllowance = houseRentAllowance; }

    public Double getDearnessAllowance() { return dearnessAllowance; }
    public void setDearnessAllowance(Double dearnessAllowance) { this.dearnessAllowance = dearnessAllowance; }

    public Double getMedicalAllowance() { return medicalAllowance; }
    public void setMedicalAllowance(Double medicalAllowance) { this.medicalAllowance = medicalAllowance; }

    public Double getTransportAllowance() { return transportAllowance; }
    public void setTransportAllowance(Double transportAllowance) { this.transportAllowance = transportAllowance; }

    public Double getSpecialAllowance() { return specialAllowance; }
    public void setSpecialAllowance(Double specialAllowance) { this.specialAllowance = specialAllowance; }

    public Double getPerformanceBonus() { return performanceBonus; }
    public void setPerformanceBonus(Double performanceBonus) { this.performanceBonus = performanceBonus; }

    public Double getOvertimePay() { return overtimePay; }
    public void setOvertimePay(Double overtimePay) { this.overtimePay = overtimePay; }

    public Double getFestivalBonus() { return festivalBonus; }
    public void setFestivalBonus(Double festivalBonus) { this.festivalBonus = festivalBonus; }

    public Double getTeachingAllowance() { return teachingAllowance; }
    public void setTeachingAllowance(Double teachingAllowance) { this.teachingAllowance = teachingAllowance; }

    // Individual deduction getters and setters
    public Double getProvidentFund() { return providentFund; }
    public void setProvidentFund(Double providentFund) { this.providentFund = providentFund; }

    public Double getEmployeeStateInsurance() { return employeeStateInsurance; }
    public void setEmployeeStateInsurance(Double employeeStateInsurance) { this.employeeStateInsurance = employeeStateInsurance; }

    public Double getIncomeTax() { return incomeTax; }
    public void setIncomeTax(Double incomeTax) { this.incomeTax = incomeTax; }

    public Double getProfessionalTax() { return professionalTax; }
    public void setProfessionalTax(Double professionalTax) { this.professionalTax = professionalTax; }

    public Double getLoanDeduction() { return loanDeduction; }
    public void setLoanDeduction(Double loanDeduction) { this.loanDeduction = loanDeduction; }

    public Double getAdvanceDeduction() { return advanceDeduction; }
    public void setAdvanceDeduction(Double advanceDeduction) { this.advanceDeduction = advanceDeduction; }

    public Double getLateDeduction() { return lateDeduction; }
    public void setLateDeduction(Double lateDeduction) { this.lateDeduction = lateDeduction; }

    public Double getAbsentDeduction() { return absentDeduction; }
    public void setAbsentDeduction(Double absentDeduction) { this.absentDeduction = absentDeduction; }

    public Double getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(Double otherDeductions) { this.otherDeductions = otherDeductions; }

    // Tax-related getters and setters
    public Double getTaxableIncome() { return taxableIncome; }
    public void setTaxableIncome(Double taxableIncome) { this.taxableIncome = taxableIncome; }

    public Double getTaxExemptions() { return taxExemptions; }
    public void setTaxExemptions(Double taxExemptions) { this.taxExemptions = taxExemptions; }

    public Double getTdsDeducted() { return tdsDeducted; }
    public void setTdsDeducted(Double tdsDeducted) { this.tdsDeducted = tdsDeducted; }

    public String getTaxSlab() { return taxSlab; }
    public void setTaxSlab(String taxSlab) { this.taxSlab = taxSlab; }

    // Attendance-related getters and setters
    public Integer getWorkingDays() { return workingDays; }
    public void setWorkingDays(Integer workingDays) { this.workingDays = workingDays; }

    public Integer getPresentDays() { return presentDays; }
    public void setPresentDays(Integer presentDays) { this.presentDays = presentDays; }

    public Integer getAbsentDays() { return absentDays; }
    public void setAbsentDays(Integer absentDays) { this.absentDays = absentDays; }

    public Integer getLeaveDays() { return leaveDays; }
    public void setLeaveDays(Integer leaveDays) { this.leaveDays = leaveDays; }

    public Integer getPaidLeaveDays() { return paidLeaveDays; }
    public void setPaidLeaveDays(Integer paidLeaveDays) { this.paidLeaveDays = paidLeaveDays; }

    public Integer getUnpaidLeaveDays() { return unpaidLeaveDays; }
    public void setUnpaidLeaveDays(Integer unpaidLeaveDays) { this.unpaidLeaveDays = unpaidLeaveDays; }

    public Double getAttendancePercentage() { return attendancePercentage; }
    public void setAttendancePercentage(Double attendancePercentage) { this.attendancePercentage = attendancePercentage; }

    public Boolean getSalaryOnHold() { return salaryOnHold; }
    public void setSalaryOnHold(Boolean salaryOnHold) { this.salaryOnHold = salaryOnHold; }

    // PF and bank details getters and setters
    public Double getPfEmployeeContribution() { return pfEmployeeContribution; }
    public void setPfEmployeeContribution(Double pfEmployeeContribution) { this.pfEmployeeContribution = pfEmployeeContribution; }

    public Double getPfEmployerContribution() { return pfEmployerContribution; }
    public void setPfEmployerContribution(Double pfEmployerContribution) { this.pfEmployerContribution = pfEmployerContribution; }

    public Double getPfTotal() { return pfTotal; }
    public void setPfTotal(Double pfTotal) { this.pfTotal = pfTotal; }

    public String getPfAccountNumber() { return pfAccountNumber; }
    public void setPfAccountNumber(String pfAccountNumber) { this.pfAccountNumber = pfAccountNumber; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getBankBranch() { return bankBranch; }
    public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }

    // Status and approval getters and setters
    public SalaryStatus getStatus() { return status; }
    public void setStatus(SalaryStatus status) { this.status = status; }

    public Boolean getIsProcessed() { return isProcessed; }
    public void setIsProcessed(Boolean isProcessed) { this.isProcessed = isProcessed; }

    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }

    public Boolean getIsPaid() { return isPaid; }
    public void setIsPaid(Boolean isPaid) { this.isPaid = isPaid; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedDate() { return approvedDate; }
    public void setApprovedDate(LocalDateTime approvedDate) { this.approvedDate = approvedDate; }

    public String getApprovalComments() { return approvalComments; }
    public void setApprovalComments(String approvalComments) { this.approvalComments = approvalComments; }

    // Payment details getters and setters
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

    // Salary revision getters and setters
    public String getSalaryRevisionId() { return salaryRevisionId; }
    public void setSalaryRevisionId(String salaryRevisionId) { this.salaryRevisionId = salaryRevisionId; }

    public LocalDate getLastRevisionDate() { return lastRevisionDate; }
    public void setLastRevisionDate(LocalDate lastRevisionDate) { this.lastRevisionDate = lastRevisionDate; }

    public Double getPreviousBasicSalary() { return previousBasicSalary; }
    public void setPreviousBasicSalary(Double previousBasicSalary) { this.previousBasicSalary = previousBasicSalary; }

    public Double getIncrementAmount() { return incrementAmount; }
    public void setIncrementAmount(Double incrementAmount) { this.incrementAmount = incrementAmount; }

    public Double getIncrementPercentage() { return incrementPercentage; }
    public void setIncrementPercentage(Double incrementPercentage) { this.incrementPercentage = incrementPercentage; }

    public String getRevisionReason() { return revisionReason; }
    public void setRevisionReason(String revisionReason) { this.revisionReason = revisionReason; }

    // Salary slip and additional details
    public String getFinancialYear() { return financialYear; }
    public void setFinancialYear(String financialYear) { this.financialYear = financialYear; }

    public String getSalarySlipNumber() { return salarySlipNumber; }
    public void setSalarySlipNumber(String salarySlipNumber) { this.salarySlipNumber = salarySlipNumber; }

    public Boolean getSalarySlipGenerated() { return salarySlipGenerated; }
    public void setSalarySlipGenerated(Boolean salarySlipGenerated) { this.salarySlipGenerated = salarySlipGenerated; }

    public String getSalarySlipPath() { return salarySlipPath; }
    public void setSalarySlipPath(String salarySlipPath) { this.salarySlipPath = salarySlipPath; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    // Metadata getters and setters
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastModifiedDate() { return lastModifiedDate; }
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) { this.lastModifiedDate = lastModifiedDate; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }

    // Compliance and reporting getters and setters
    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getUanNumber() { return uanNumber; }
    public void setUanNumber(String uanNumber) { this.uanNumber = uanNumber; }

    public String getEsiNumber() { return esiNumber; }
    public void setEsiNumber(String esiNumber) { this.esiNumber = esiNumber; }

    public Boolean getForm16Generated() { return form16Generated; }
    public void setForm16Generated(Boolean form16Generated) { this.form16Generated = form16Generated; }

    public Boolean getTdsCompliant() { return tdsCompliant; }
    public void setTdsCompliant(Boolean tdsCompliant) { this.tdsCompliant = tdsCompliant; }

    // Helper methods
    public void calculateTotalEarnings() {
        this.totalEarnings = (this.basicSalary != null ? this.basicSalary : 0.0) +
                           (this.houseRentAllowance != null ? this.houseRentAllowance : 0.0) +
                           (this.dearnessAllowance != null ? this.dearnessAllowance : 0.0) +
                           (this.medicalAllowance != null ? this.medicalAllowance : 0.0) +
                           (this.transportAllowance != null ? this.transportAllowance : 0.0) +
                           (this.specialAllowance != null ? this.specialAllowance : 0.0) +
                           (this.performanceBonus != null ? this.performanceBonus : 0.0) +
                           (this.overtimePay != null ? this.overtimePay : 0.0) +
                           (this.festivalBonus != null ? this.festivalBonus : 0.0) +
                           (this.teachingAllowance != null ? this.teachingAllowance : 0.0);
    }

    public void calculateTotalDeductions() {
        this.totalDeductions = (this.providentFund != null ? this.providentFund : 0.0) +
                             (this.employeeStateInsurance != null ? this.employeeStateInsurance : 0.0) +
                             (this.incomeTax != null ? this.incomeTax : 0.0) +
                             (this.professionalTax != null ? this.professionalTax : 0.0) +
                             (this.loanDeduction != null ? this.loanDeduction : 0.0) +
                             (this.advanceDeduction != null ? this.advanceDeduction : 0.0) +
                             (this.lateDeduction != null ? this.lateDeduction : 0.0) +
                             (this.absentDeduction != null ? this.absentDeduction : 0.0) +
                             (this.otherDeductions != null ? this.otherDeductions : 0.0);
    }

    public void calculateNetSalary() {
        calculateTotalEarnings();
        calculateTotalDeductions();
        this.netSalary = this.totalEarnings - this.totalDeductions;
        this.grossSalary = this.totalEarnings;
    }

    public void updateLastModified(String modifiedBy) {
        this.lastModifiedDate = LocalDateTime.now();
        this.lastModifiedBy = modifiedBy;
    }

    private String calculateFinancialYear(Integer year, Integer month) {
        if (month >= 4) { // April to March financial year
            return year + "-" + (year + 1);
        } else {
            return (year - 1) + "-" + year;
        }
    }

    public void addEarning(String component, Double amount) {
        if (this.earnings == null) {
            this.earnings = new HashMap<>();
        }
        this.earnings.put(component, amount);
    }

    public void addDeduction(String component, Double amount) {
        if (this.deductions == null) {
            this.deductions = new HashMap<>();
        }
        this.deductions.put(component, amount);
    }

    public Double calculatePfContribution(Double basicSalary) {
        // Standard PF calculation: 12% of basic salary
        if (basicSalary != null && basicSalary > 0) {
            return basicSalary * 0.12;
        }
        return 0.0;
    }

    public Double calculateESI(Double grossSalary) {
        // ESI calculation: 0.75% of gross salary if gross <= 25000
        if (grossSalary != null && grossSalary <= 25000) {
            return grossSalary * 0.0075;
        }
        return 0.0;
    }

    public Boolean isEligibleForBonus() {
        return this.presentDays != null && this.workingDays != null &&
               this.presentDays >= (this.workingDays * 0.8); // 80% attendance for bonus
    }

    @Override
    public String toString() {
        return "Salary{" +
                "salaryId='" + salaryId + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", payrollPeriod=" + payrollPeriod +
                ", basicSalary=" + basicSalary +
                ", netSalary=" + netSalary +
                ", status=" + status +
                '}';
    }
}
