package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Salary;
import com.eduai.schoolmanagement.entity.SalaryStructure;
import com.eduai.schoolmanagement.entity.Salary.SalaryStatus;
import com.eduai.schoolmanagement.entity.Salary.PaymentMethod;
import com.eduai.schoolmanagement.repository.SalaryRepository;
import com.eduai.schoolmanagement.repository.SalaryStructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private SalaryStructureRepository salaryStructureRepository;

    // Constants for calculations
    private static final double PF_RATE = 0.12; // 12%
    private static final double ESI_RATE = 0.0075; // 0.75%
    private static final double ESI_SALARY_LIMIT = 25000.0;
    private static final double PROFESSIONAL_TAX_MONTHLY = 200.0;
    private static final int DAYS_IN_MONTH = 30; // Standard for salary calculation

    // Basic CRUD operations
    public Salary createSalary(Salary salary) throws Exception {
        validateSalary(salary);

        // Check if salary already exists for this employee and period
        Optional<Salary> existingSalary = salaryRepository
            .findByEmployeeIdAndPayrollMonthAndPayrollYearAndInstitutionId(
                salary.getEmployeeId(), salary.getPayrollMonth(),
                salary.getPayrollYear(), salary.getInstitutionId());

        if (existingSalary.isPresent()) {
            throw new Exception("Salary already exists for employee " + salary.getEmployeeId() +
                              " for period " + salary.getPayrollMonth() + "/" + salary.getPayrollYear());
        }

        // Auto-calculate salary components if not provided
        if (salary.getBasicSalary() != null) {
            calculateSalaryComponents(salary);
        }

        salary.setCreatedDate(LocalDateTime.now());
        salary.setLastModifiedDate(LocalDateTime.now());

        return salaryRepository.save(salary);
    }

    public Salary updateSalary(String salaryId, Salary updatedSalary) throws Exception {
        Optional<Salary> existingOpt = salaryRepository.findById(salaryId);
        if (!existingOpt.isPresent()) {
            throw new Exception("Salary record not found with ID: " + salaryId);
        }

        Salary existing = existingOpt.get();

        // Prevent modification of processed salaries without proper authorization
        if (existing.getIsProcessed() && !updatedSalary.getLastModifiedBy().equals("SYSTEM_ADMIN")) {
            throw new Exception("Cannot modify processed salary. Contact administrator.");
        }

        // Update allowed fields
        existing.setBasicSalary(updatedSalary.getBasicSalary());
        existing.setHouseRentAllowance(updatedSalary.getHouseRentAllowance());
        existing.setMedicalAllowance(updatedSalary.getMedicalAllowance());
        existing.setTransportAllowance(updatedSalary.getTransportAllowance());
        existing.setSpecialAllowance(updatedSalary.getSpecialAllowance());
        existing.setPerformanceBonus(updatedSalary.getPerformanceBonus());
        existing.setOvertimePay(updatedSalary.getOvertimePay());
        existing.setLoanDeduction(updatedSalary.getLoanDeduction());
        existing.setAdvanceDeduction(updatedSalary.getAdvanceDeduction());
        existing.setOtherDeductions(updatedSalary.getOtherDeductions());

        // Recalculate salary components
        calculateSalaryComponents(existing);
        existing.updateLastModified(updatedSalary.getLastModifiedBy());

        return salaryRepository.save(existing);
    }

    // Salary calculation methods
    public void calculateSalaryComponents(Salary salary) {
        if (salary.getBasicSalary() == null || salary.getBasicSalary() <= 0) {
            return;
        }

        Double basicSalary = salary.getBasicSalary();

        // Calculate allowances if not provided
        if (salary.getHouseRentAllowance() == null) {
            salary.setHouseRentAllowance(basicSalary * 0.40); // 40% of basic
        }
        if (salary.getDearnessAllowance() == null) {
            salary.setDearnessAllowance(basicSalary * 0.12); // 12% of basic
        }
        if (salary.getMedicalAllowance() == null) {
            salary.setMedicalAllowance(1250.0); // Fixed amount
        }
        if (salary.getTransportAllowance() == null) {
            salary.setTransportAllowance(800.0); // Fixed amount
        }

        // Calculate statutory deductions
        calculateProvidentFund(salary);
        calculateESI(salary);
        calculateProfessionalTax(salary);
        calculateIncomeTax(salary);

        // Calculate attendance-based deductions
        calculateAttendanceDeductions(salary);

        // Calculate totals
        salary.calculateNetSalary();
    }

    private void calculateProvidentFund(Salary salary) {
        if (salary.getBasicSalary() != null && salary.getProvidentFund() == null) {
            Double pfAmount = salary.getBasicSalary() * PF_RATE;
            salary.setProvidentFund(pfAmount);
            salary.setPfEmployeeContribution(pfAmount);
            salary.setPfEmployerContribution(pfAmount); // Equal contribution
            salary.setPfTotal(pfAmount * 2);
        }
    }

    private void calculateESI(Salary salary) {
        if (salary.getGrossSalary() != null && salary.getGrossSalary() <= ESI_SALARY_LIMIT
            && salary.getEmployeeStateInsurance() == null) {
            salary.setEmployeeStateInsurance(salary.getGrossSalary() * ESI_RATE);
        }
    }

    private void calculateProfessionalTax(Salary salary) {
        if (salary.getProfessionalTax() == null) {
            salary.setProfessionalTax(PROFESSIONAL_TAX_MONTHLY);
        }
    }

    private void calculateIncomeTax(Salary salary) {
        // Simplified income tax calculation
        // In real implementation, this would be based on tax slabs and annual income
        if (salary.getIncomeTax() == null) {
            Double monthlyTaxableIncome = salary.getTotalEarnings() - salary.getProvidentFund() - 50000/12; // Standard deduction
            if (monthlyTaxableIncome > 20833) { // Above 2.5L annually
                salary.setIncomeTax(monthlyTaxableIncome * 0.05); // 5% tax rate (simplified)
            } else {
                salary.setIncomeTax(0.0);
            }
        }
    }

    private void calculateAttendanceDeductions(Salary salary) {
        if (salary.getAbsentDays() != null && salary.getAbsentDays() > 0 && salary.getBasicSalary() != null) {
            Double dailySalary = salary.getBasicSalary() / DAYS_IN_MONTH;
            Double absentDeduction = dailySalary * salary.getAbsentDays();
            salary.setAbsentDeduction(absentDeduction);
        }

        if (salary.getUnpaidLeaveDays() != null && salary.getUnpaidLeaveDays() > 0 && salary.getBasicSalary() != null) {
            Double dailySalary = salary.getBasicSalary() / DAYS_IN_MONTH;
            Double unpaidLeaveDeduction = dailySalary * salary.getUnpaidLeaveDays();
            // Add to absent deduction or create separate field
            Double currentAbsentDeduction = salary.getAbsentDeduction() != null ? salary.getAbsentDeduction() : 0.0;
            salary.setAbsentDeduction(currentAbsentDeduction + unpaidLeaveDeduction);
        }
    }

    // Bulk salary generation
    public List<Salary> generateMonthlySalaries(String institutionId, Integer payrollMonth,
                                               Integer payrollYear, List<String> employeeIds,
                                               String processedBy) throws Exception {
        List<Salary> generatedSalaries = new ArrayList<>();

        for (String employeeId : employeeIds) {
            try {
                // Check if salary already exists
                Optional<Salary> existingSalary = salaryRepository
                    .findByEmployeeIdAndPayrollMonthAndPayrollYearAndInstitutionId(
                        employeeId, payrollMonth, payrollYear, institutionId);

                if (existingSalary.isPresent()) {
                    continue; // Skip if already generated
                }

                // Create salary from structure or template
                Salary salary = createSalaryFromStructure(employeeId, institutionId, payrollMonth, payrollYear);
                if (salary != null) {
                    salary.setCreatedBy(processedBy);
                    salary.setProcessedBy(processedBy);
                    generatedSalaries.add(salaryRepository.save(salary));
                }
            } catch (Exception e) {
                System.err.println("Error generating salary for employee " + employeeId + ": " + e.getMessage());
            }
        }

        return generatedSalaries;
    }

    private Salary createSalaryFromStructure(String employeeId, String institutionId,
                                           Integer payrollMonth, Integer payrollYear) {
        // In real implementation, this would fetch employee details and salary structure
        // For now, creating with demo data
        Salary salary = new Salary(employeeId, institutionId, payrollMonth, payrollYear);

        // Set demo basic salary based on employee type (would come from salary structure)
        salary.setBasicSalary(50000.0); // Demo value
        salary.setEmployeeName("Employee " + employeeId);
        salary.setDesignation("Staff");
        salary.setEmployeeType("TEACHING");
        salary.setWorkingDays(DAYS_IN_MONTH);
        salary.setPresentDays(DAYS_IN_MONTH);
        salary.setAbsentDays(0);
        salary.setAttendancePercentage(100.0);

        calculateSalaryComponents(salary);
        return salary;
    }

    // Salary processing workflow
    public Salary processSalary(String salaryId, String processedBy) throws Exception {
        Optional<Salary> salaryOpt = salaryRepository.findById(salaryId);
        if (!salaryOpt.isPresent()) {
            throw new Exception("Salary record not found");
        }

        Salary salary = salaryOpt.get();

        if (salary.getIsProcessed()) {
            throw new Exception("Salary is already processed");
        }

        // Validate salary calculations
        calculateSalaryComponents(salary);

        // Generate salary slip number
        String slipNumber = generateSalarySlipNumber(salary);
        salary.setSalarySlipNumber(slipNumber);

        // Update status
        salary.setStatus(SalaryStatus.PROCESSED);
        salary.setIsProcessed(true);
        salary.setSalaryProcessedDate(LocalDate.now());
        salary.setProcessedBy(processedBy);
        salary.updateLastModified(processedBy);

        return salaryRepository.save(salary);
    }

    public Salary approveSalary(String salaryId, String approvedBy, String comments) throws Exception {
        Optional<Salary> salaryOpt = salaryRepository.findById(salaryId);
        if (!salaryOpt.isPresent()) {
            throw new Exception("Salary record not found");
        }

        Salary salary = salaryOpt.get();

        if (!salary.getIsProcessed()) {
            throw new Exception("Salary must be processed before approval");
        }

        if (salary.getIsApproved()) {
            throw new Exception("Salary is already approved");
        }

        salary.setStatus(SalaryStatus.APPROVED);
        salary.setIsApproved(true);
        salary.setApprovedBy(approvedBy);
        salary.setApprovedDate(LocalDateTime.now());
        salary.setApprovalComments(comments);
        salary.updateLastModified(approvedBy);

        return salaryRepository.save(salary);
    }

    public Salary paySalary(String salaryId, PaymentMethod paymentMethod, String transactionId,
                           String paidBy) throws Exception {
        Optional<Salary> salaryOpt = salaryRepository.findById(salaryId);
        if (!salaryOpt.isPresent()) {
            throw new Exception("Salary record not found");
        }

        Salary salary = salaryOpt.get();

        if (!salary.getIsApproved()) {
            throw new Exception("Salary must be approved before payment");
        }

        if (salary.getIsPaid()) {
            throw new Exception("Salary is already paid");
        }

        salary.setStatus(SalaryStatus.PAID);
        salary.setIsPaid(true);
        salary.setPaymentMethod(paymentMethod);
        salary.setTransactionId(transactionId);
        salary.setPaymentDate(LocalDate.now());
        salary.setPaymentReference("PAY-" + salary.getSalarySlipNumber());
        salary.updateLastModified(paidBy);

        return salaryRepository.save(salary);
    }

    // Query operations
    public Optional<Salary> getSalary(String salaryId) {
        return salaryRepository.findById(salaryId);
    }

    public List<Salary> getSalariesByEmployee(String employeeId, String institutionId) {
        return salaryRepository.findByEmployeeIdAndInstitutionId(employeeId, institutionId);
    }

    public List<Salary> getSalariesForPeriod(String institutionId, Integer payrollMonth, Integer payrollYear) {
        return salaryRepository.findByInstitutionIdAndPayrollMonthAndPayrollYear(
            institutionId, payrollMonth, payrollYear);
    }

    public Page<Salary> getSalariesByInstitution(String institutionId, Pageable pageable) {
        return salaryRepository.findByInstitutionId(institutionId, pageable);
    }

    public List<Salary> getPendingApprovals(String institutionId) {
        return salaryRepository.findPendingApprovals(institutionId);
    }

    public List<Salary> getUnpaidSalaries(String institutionId) {
        return salaryRepository.findApprovedUnpaidSalaries(institutionId);
    }

    // Analytics and reporting
    public Map<String, Object> getPayrollSummary(String institutionId, Integer payrollMonth, Integer payrollYear) {
        Map<String, Object> summary = new HashMap<>();

        List<Salary> salaries = salaryRepository.findByInstitutionIdAndPayrollMonthAndPayrollYear(
            institutionId, payrollMonth, payrollYear);

        // Overall statistics
        long totalEmployees = salaries.size();
        long processedCount = salaries.stream().filter(Salary::getIsProcessed).count();
        long approvedCount = salaries.stream().filter(Salary::getIsApproved).count();
        long paidCount = salaries.stream().filter(Salary::getIsPaid).count();

        double totalGrossSalary = salaries.stream()
            .filter(s -> s.getGrossSalary() != null)
            .mapToDouble(Salary::getGrossSalary).sum();
        double totalNetSalary = salaries.stream()
            .filter(s -> s.getNetSalary() != null)
            .mapToDouble(Salary::getNetSalary).sum();
        double totalDeductions = salaries.stream()
            .filter(s -> s.getTotalDeductions() != null)
            .mapToDouble(Salary::getTotalDeductions).sum();

        summary.put("totalEmployees", totalEmployees);
        summary.put("processedCount", processedCount);
        summary.put("approvedCount", approvedCount);
        summary.put("paidCount", paidCount);
        summary.put("totalGrossSalary", totalGrossSalary);
        summary.put("totalNetSalary", totalNetSalary);
        summary.put("totalDeductions", totalDeductions);

        // Department-wise breakdown
        Map<String, Map<String, Object>> departmentBreakdown = salaries.stream()
            .filter(s -> s.getDepartmentName() != null)
            .collect(Collectors.groupingBy(
                Salary::getDepartmentName,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                        Map<String, Object> deptStats = new HashMap<>();
                        deptStats.put("count", list.size());
                        deptStats.put("totalSalary", list.stream()
                            .filter(s -> s.getNetSalary() != null)
                            .mapToDouble(Salary::getNetSalary).sum());
                        deptStats.put("avgSalary", list.stream()
                            .filter(s -> s.getNetSalary() != null)
                            .mapToDouble(Salary::getNetSalary).average().orElse(0.0));
                        return deptStats;
                    }
                )
            ));

        summary.put("departmentBreakdown", departmentBreakdown);

        // Status breakdown
        Map<SalaryStatus, Long> statusBreakdown = salaries.stream()
            .collect(Collectors.groupingBy(Salary::getStatus, Collectors.counting()));
        summary.put("statusBreakdown", statusBreakdown);

        return summary;
    }

    public Map<String, Object> getEmployeeSalaryAnalytics(String employeeId, String institutionId,
                                                         String financialYear) {
        Map<String, Object> analytics = new HashMap<>();

        List<Salary> salaries = salaryRepository.findByEmployeeAndFinancialYear(
            institutionId, financialYear, employeeId);

        if (salaries.isEmpty()) {
            return analytics;
        }

        // Annual totals
        double annualGross = salaries.stream()
            .filter(s -> s.getGrossSalary() != null)
            .mapToDouble(Salary::getGrossSalary).sum();
        double annualNet = salaries.stream()
            .filter(s -> s.getNetSalary() != null)
            .mapToDouble(Salary::getNetSalary).sum();
        double annualTax = salaries.stream()
            .filter(s -> s.getIncomeTax() != null)
            .mapToDouble(Salary::getIncomeTax).sum();
        double annualPF = salaries.stream()
            .filter(s -> s.getProvidentFund() != null)
            .mapToDouble(Salary::getProvidentFund).sum();

        analytics.put("annualGrossSalary", annualGross);
        analytics.put("annualNetSalary", annualNet);
        analytics.put("annualTaxDeducted", annualTax);
        analytics.put("annualPFContribution", annualPF);
        analytics.put("monthsProcessed", salaries.size());

        // Monthly trends
        Map<String, Double> monthlyTrends = salaries.stream()
            .collect(Collectors.toMap(
                s -> s.getPayrollMonth() + "/" + s.getPayrollYear(),
                s -> s.getNetSalary() != null ? s.getNetSalary() : 0.0,
                (existing, replacement) -> existing
            ));
        analytics.put("monthlyTrends", monthlyTrends);

        // Calculate effective tax rate
        if (annualGross > 0) {
            analytics.put("effectiveTaxRate", (annualTax / annualGross) * 100);
        }

        return analytics;
    }

    public List<Map<String, Object>> getDepartmentPayrollReport(String institutionId, Integer payrollMonth,
                                                               Integer payrollYear) {
        List<Map<String, Object>> report = new ArrayList<>();

        List<Salary> salaries = salaryRepository.findByInstitutionIdAndPayrollMonthAndPayrollYear(
            institutionId, payrollMonth, payrollYear);

        Map<String, List<Salary>> departmentGroups = salaries.stream()
            .filter(s -> s.getDepartmentName() != null)
            .collect(Collectors.groupingBy(Salary::getDepartmentName));

        for (Map.Entry<String, List<Salary>> entry : departmentGroups.entrySet()) {
            String department = entry.getKey();
            List<Salary> deptSalaries = entry.getValue();

            Map<String, Object> deptReport = new HashMap<>();
            deptReport.put("department", department);
            deptReport.put("employeeCount", deptSalaries.size());
            deptReport.put("totalGrossSalary", deptSalaries.stream()
                .filter(s -> s.getGrossSalary() != null)
                .mapToDouble(Salary::getGrossSalary).sum());
            deptReport.put("totalNetSalary", deptSalaries.stream()
                .filter(s -> s.getNetSalary() != null)
                .mapToDouble(Salary::getNetSalary).sum());
            deptReport.put("totalDeductions", deptSalaries.stream()
                .filter(s -> s.getTotalDeductions() != null)
                .mapToDouble(Salary::getTotalDeductions).sum());
            deptReport.put("avgSalary", deptSalaries.stream()
                .filter(s -> s.getNetSalary() != null)
                .mapToDouble(Salary::getNetSalary).average().orElse(0.0));

            report.add(deptReport);
        }

        return report;
    }

    // Utility methods
    private void validateSalary(Salary salary) throws Exception {
        if (salary.getEmployeeId() == null || salary.getEmployeeId().trim().isEmpty()) {
            throw new Exception("Employee ID is required");
        }
        if (salary.getInstitutionId() == null || salary.getInstitutionId().trim().isEmpty()) {
            throw new Exception("Institution ID is required");
        }
        if (salary.getPayrollMonth() == null || salary.getPayrollMonth() < 1 || salary.getPayrollMonth() > 12) {
            throw new Exception("Valid payroll month (1-12) is required");
        }
        if (salary.getPayrollYear() == null || salary.getPayrollYear() < 2000) {
            throw new Exception("Valid payroll year is required");
        }
    }

    private String generateSalarySlipNumber(Salary salary) {
        return "SL-" + salary.getInstitutionId().substring(0, 3).toUpperCase() + "-" +
               salary.getPayrollYear() + String.format("%02d", salary.getPayrollMonth()) + "-" +
               salary.getEmployeeId().substring(0, Math.min(4, salary.getEmployeeId().length())).toUpperCase() + "-" +
               System.currentTimeMillis() % 10000;
    }

    // Delete operations
    public void deleteSalary(String salaryId) throws Exception {
        Optional<Salary> salaryOpt = salaryRepository.findById(salaryId);
        if (!salaryOpt.isPresent()) {
            throw new Exception("Salary record not found");
        }

        Salary salary = salaryOpt.get();
        if (salary.getIsPaid()) {
            throw new Exception("Cannot delete paid salary");
        }

        salaryRepository.delete(salary);
    }

    // Export functionality
    public String exportPayrollToCsv(String institutionId, Integer payrollMonth, Integer payrollYear) {
        List<Salary> salaries = salaryRepository.findByInstitutionIdAndPayrollMonthAndPayrollYear(
            institutionId, payrollMonth, payrollYear);

        StringBuilder csv = new StringBuilder();
        csv.append("Employee ID,Employee Name,Designation,Basic Salary,Gross Salary,Total Deductions,Net Salary,Status\n");

        for (Salary salary : salaries) {
            csv.append(salary.getEmployeeId()).append(",")
               .append(salary.getEmployeeName() != null ? salary.getEmployeeName() : "").append(",")
               .append(salary.getDesignation() != null ? salary.getDesignation() : "").append(",")
               .append(salary.getBasicSalary() != null ? salary.getBasicSalary() : "").append(",")
               .append(salary.getGrossSalary() != null ? salary.getGrossSalary() : "").append(",")
               .append(salary.getTotalDeductions() != null ? salary.getTotalDeductions() : "").append(",")
               .append(salary.getNetSalary() != null ? salary.getNetSalary() : "").append(",")
               .append(salary.getStatus() != null ? salary.getStatus() : "")
               .append("\n");
        }

        return csv.toString();
    }

    // Search functionality
    public List<Salary> searchSalaries(String institutionId, String searchTerm, Integer payrollMonth, Integer payrollYear) {
        if (payrollMonth != null && payrollYear != null) {
            return salaryRepository.searchSalaryRecordsForPeriod(institutionId, payrollMonth, payrollYear, searchTerm);
        } else {
            return salaryRepository.searchSalaryRecords(institutionId, searchTerm);
        }
    }
}
