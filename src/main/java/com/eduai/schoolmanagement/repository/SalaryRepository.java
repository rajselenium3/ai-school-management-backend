package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.Salary;
import com.eduai.schoolmanagement.entity.Salary.SalaryStatus;
import com.eduai.schoolmanagement.entity.Salary.PaymentMethod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends MongoRepository<Salary, String> {

    // Basic queries
    Optional<Salary> findByEmployeeIdAndPayrollMonthAndPayrollYearAndInstitutionId(
        String employeeId, Integer payrollMonth, Integer payrollYear, String institutionId);

    List<Salary> findByEmployeeIdAndInstitutionId(String employeeId, String institutionId);

    List<Salary> findByInstitutionId(String institutionId);

    Page<Salary> findByInstitutionId(String institutionId, Pageable pageable);

    // Payroll period queries
    List<Salary> findByInstitutionIdAndPayrollMonthAndPayrollYear(
        String institutionId, Integer payrollMonth, Integer payrollYear);

    List<Salary> findByInstitutionIdAndPayrollYearAndPayrollMonthBetween(
        String institutionId, Integer payrollYear, Integer startMonth, Integer endMonth);

    List<Salary> findByInstitutionIdAndPayrollPeriod(String institutionId, YearMonth payrollPeriod);

    Page<Salary> findByInstitutionIdAndPayrollMonthAndPayrollYear(
        String institutionId, Integer payrollMonth, Integer payrollYear, Pageable pageable);

    // Status-based queries
    List<Salary> findByInstitutionIdAndStatus(String institutionId, SalaryStatus status);

    List<Salary> findByInstitutionIdAndStatusAndPayrollMonthAndPayrollYear(
        String institutionId, SalaryStatus status, Integer payrollMonth, Integer payrollYear);

    Page<Salary> findByInstitutionIdAndStatus(String institutionId, SalaryStatus status, Pageable pageable);

    List<Salary> findByInstitutionIdAndIsProcessed(String institutionId, Boolean isProcessed);

    List<Salary> findByInstitutionIdAndIsApproved(String institutionId, Boolean isApproved);

    List<Salary> findByInstitutionIdAndIsPaid(String institutionId, Boolean isPaid);

    List<Salary> findByInstitutionIdAndSalaryOnHold(String institutionId, Boolean salaryOnHold);

    // Department-wise queries
    List<Salary> findByInstitutionIdAndDepartmentIdAndPayrollMonthAndPayrollYear(
        String institutionId, String departmentId, Integer payrollMonth, Integer payrollYear);

    List<Salary> findByInstitutionIdAndDepartmentId(String institutionId, String departmentId);

    Page<Salary> findByInstitutionIdAndDepartmentId(String institutionId, String departmentId, Pageable pageable);

    // Employee type queries
    List<Salary> findByInstitutionIdAndEmployeeTypeAndPayrollMonthAndPayrollYear(
        String institutionId, String employeeType, Integer payrollMonth, Integer payrollYear);

    List<Salary> findByInstitutionIdAndEmployeeType(String institutionId, String employeeType);

    // Grade-wise queries
    List<Salary> findByInstitutionIdAndEmployeeGradeAndPayrollMonthAndPayrollYear(
        String institutionId, String employeeGrade, Integer payrollMonth, Integer payrollYear);

    List<Salary> findByInstitutionIdAndEmployeeGrade(String institutionId, String employeeGrade);

    // Salary range queries
    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'netSalary': { $gte: ?3, $lte: ?4 } }")
    List<Salary> findBySalaryRange(String institutionId, Integer payrollMonth, Integer payrollYear,
                                   Double minSalary, Double maxSalary);

    @Query("{ 'institutionId': ?0, 'basicSalary': { $gte: ?1, $lte: ?2 } }")
    List<Salary> findByBasicSalaryRange(String institutionId, Double minBasicSalary, Double maxBasicSalary);

    // Date-based queries
    List<Salary> findByInstitutionIdAndPayrollDateBetween(String institutionId, LocalDate startDate, LocalDate endDate);

    List<Salary> findByInstitutionIdAndSalaryProcessedDateBetween(String institutionId, LocalDate startDate, LocalDate endDate);

    List<Salary> findByInstitutionIdAndPaymentDateBetween(String institutionId, LocalDate startDate, LocalDate endDate);

    // Financial year queries
    List<Salary> findByInstitutionIdAndFinancialYear(String institutionId, String financialYear);

    @Query("{ 'institutionId': ?0, 'financialYear': ?1, 'employeeId': ?2 }")
    List<Salary> findByEmployeeAndFinancialYear(String institutionId, String financialYear, String employeeId);

    // Approval workflow queries
    List<Salary> findByInstitutionIdAndApprovedBy(String institutionId, String approvedBy);

    List<Salary> findByInstitutionIdAndApprovedDateBetween(String institutionId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'institutionId': ?0, 'status': 'PENDING_APPROVAL' }")
    List<Salary> findPendingApprovals(String institutionId);

    @Query("{ 'institutionId': ?0, 'isApproved': false, 'status': { $ne: 'DRAFT' } }")
    List<Salary> findUnapprovedSalaries(String institutionId);

    // Payment-related queries
    List<Salary> findByInstitutionIdAndPaymentMethod(String institutionId, PaymentMethod paymentMethod);

    List<Salary> findByInstitutionIdAndTransactionId(String institutionId, String transactionId);

    @Query("{ 'institutionId': ?0, 'isPaid': false, 'isApproved': true }")
    List<Salary> findApprovedUnpaidSalaries(String institutionId);

    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'isPaid': false }")
    List<Salary> findUnpaidSalariesForPeriod(String institutionId, Integer payrollMonth, Integer payrollYear);

    // Statistical queries
    @Query(value = "{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2 }", count = true)
    long countByInstitutionAndPayrollPeriod(String institutionId, Integer payrollMonth, Integer payrollYear);

    @Query(value = "{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'status': ?3 }", count = true)
    long countByInstitutionAndPayrollPeriodAndStatus(String institutionId, Integer payrollMonth,
                                                    Integer payrollYear, SalaryStatus status);

    @Query(value = "{ 'institutionId': ?0, 'departmentId': ?1, 'payrollMonth': ?2, 'payrollYear': ?3 }", count = true)
    long countByDepartmentAndPayrollPeriod(String institutionId, String departmentId,
                                          Integer payrollMonth, Integer payrollYear);

    // Salary aggregation queries
    @Query("{ $group: { _id: '$departmentId', totalSalary: { $sum: '$netSalary' }, count: { $sum: 1 } } }")
    List<Object> getSalaryByDepartment(String institutionId, Integer payrollMonth, Integer payrollYear);

    @Query("{ $group: { _id: '$employeeGrade', avgSalary: { $avg: '$netSalary' }, maxSalary: { $max: '$netSalary' }, minSalary: { $min: '$netSalary' } } }")
    List<Object> getSalaryStatsByGrade(String institutionId, Integer payrollMonth, Integer payrollYear);

    @Query("{ $group: { _id: '$employeeType', totalSalary: { $sum: '$netSalary' }, avgSalary: { $avg: '$netSalary' } } }")
    List<Object> getSalaryByEmployeeType(String institutionId, Integer payrollMonth, Integer payrollYear);

    // Tax and compliance queries
    @Query("{ 'institutionId': ?0, 'payrollYear': ?1, 'tdsDeducted': { $gt: 0 } }")
    List<Salary> findSalariesWithTDS(String institutionId, Integer payrollYear);

    @Query("{ 'institutionId': ?0, 'financialYear': ?1, 'form16Generated': false }")
    List<Salary> findPendingForm16Generation(String institutionId, String financialYear);

    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'providentFund': { $gt: 0 } }")
    List<Salary> findSalariesWithPF(String institutionId, Integer payrollMonth, Integer payrollYear);

    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'employeeStateInsurance': { $gt: 0 } }")
    List<Salary> findSalariesWithESI(String institutionId, Integer payrollMonth, Integer payrollYear);

    // Attendance integration queries
    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'attendancePercentage': { $lt: ?3 } }")
    List<Salary> findLowAttendanceSalaries(String institutionId, Integer payrollMonth,
                                          Integer payrollYear, Double minAttendancePercentage);

    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'absentDeduction': { $gt: 0 } }")
    List<Salary> findSalariesWithAbsentDeductions(String institutionId, Integer payrollMonth, Integer payrollYear);

    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'unpaidLeaveDays': { $gt: 0 } }")
    List<Salary> findSalariesWithUnpaidLeave(String institutionId, Integer payrollMonth, Integer payrollYear);

    // Bonus and incentive queries
    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'performanceBonus': { $gt: 0 } }")
    List<Salary> findSalariesWithPerformanceBonus(String institutionId, Integer payrollMonth, Integer payrollYear);

    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'festivalBonus': { $gt: 0 } }")
    List<Salary> findSalariesWithFestivalBonus(String institutionId, Integer payrollMonth, Integer payrollYear);

    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'overtimePay': { $gt: 0 } }")
    List<Salary> findSalariesWithOvertimePay(String institutionId, Integer payrollMonth, Integer payrollYear);

    // Loan and advance queries
    @Query("{ 'institutionId': ?0, 'loanDeduction': { $gt: 0 } }")
    List<Salary> findSalariesWithLoanDeductions(String institutionId);

    @Query("{ 'institutionId': ?0, 'advanceDeduction': { $gt: 0 } }")
    List<Salary> findSalariesWithAdvanceDeductions(String institutionId);

    @Query("{ 'employeeId': ?0, 'institutionId': ?1, 'loanDeduction': { $gt: 0 } }")
    List<Salary> findEmployeeLoanDeductionHistory(String employeeId, String institutionId);

    // Salary slip queries
    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'salarySlipGenerated': false }")
    List<Salary> findPendingSalarySlipGeneration(String institutionId, Integer payrollMonth, Integer payrollYear);

    @Query("{ 'employeeId': ?0, 'institutionId': ?1, 'salarySlipGenerated': true }")
    List<Salary> findGeneratedSalarySlipsForEmployee(String employeeId, String institutionId);

    // Revision and increment queries
    @Query("{ 'institutionId': ?0, 'salaryRevisionId': { $exists: true, $ne: null } }")
    List<Salary> findRevisedSalaries(String institutionId);

    @Query("{ 'institutionId': ?0, 'lastRevisionDate': { $gte: ?1, $lte: ?2 } }")
    List<Salary> findSalariesRevisedBetween(String institutionId, LocalDate startDate, LocalDate endDate);

    @Query("{ 'institutionId': ?0, 'incrementAmount': { $gt: 0 } }")
    List<Salary> findSalariesWithIncrements(String institutionId);

    // Search and filtering
    @Query("{ 'institutionId': ?0, $or: [ { 'employeeName': { $regex: ?1, $options: 'i' } }, " +
           "{ 'employeeCode': { $regex: ?1, $options: 'i' } }, { 'designation': { $regex: ?1, $options: 'i' } } ] }")
    List<Salary> searchSalaryRecords(String institutionId, String searchTerm);

    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, $or: [ " +
           "{ 'employeeName': { $regex: ?3, $options: 'i' } }, { 'employeeCode': { $regex: ?3, $options: 'i' } } ] }")
    List<Salary> searchSalaryRecordsForPeriod(String institutionId, Integer payrollMonth,
                                             Integer payrollYear, String searchTerm);

    // Validation queries
    boolean existsByEmployeeIdAndPayrollMonthAndPayrollYearAndInstitutionId(
        String employeeId, Integer payrollMonth, Integer payrollYear, String institutionId);

    @Query("{ 'institutionId': ?0, 'salarySlipNumber': ?1 }")
    Optional<Salary> findBySalarySlipNumber(String institutionId, String salarySlipNumber);

    @Query("{ 'institutionId': ?0, 'transactionId': ?1 }")
    Optional<Salary> findByTransactionId(String institutionId, String transactionId);

    // Cleanup and maintenance queries
    void deleteByInstitutionIdAndPayrollDateBefore(String institutionId, LocalDate beforeDate);

    void deleteByEmployeeIdAndInstitutionId(String employeeId, String institutionId);

    @Query("{ 'institutionId': ?0, 'status': 'DRAFT', 'createdDate': { $lt: ?1 } }")
    List<Salary> findOldDraftSalaries(String institutionId, LocalDateTime beforeDate);

    // Bulk operations support
    @Query("{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'employeeId': { $in: ?3 } }")
    List<Salary> findByEmployeeIdsAndPayrollPeriod(String institutionId, Integer payrollMonth,
                                                   Integer payrollYear, List<String> employeeIds);

    @Query("{ 'institutionId': ?0, 'departmentId': { $in: ?1 }, 'payrollMonth': ?2, 'payrollYear': ?3 }")
    List<Salary> findByDepartmentIdsAndPayrollPeriod(String institutionId, List<String> departmentIds,
                                                     Integer payrollMonth, Integer payrollYear);

    // Recent records for dashboard
    @Query("{ 'institutionId': ?0, 'createdDate': { $gte: ?1 } }")
    List<Salary> findRecentSalaryRecords(String institutionId, LocalDateTime since, Pageable pageable);

    @Query("{ 'institutionId': ?0, 'lastModifiedDate': { $gte: ?1 } }")
    List<Salary> findRecentlyModifiedSalaries(String institutionId, LocalDateTime since, Pageable pageable);

    // Performance optimization queries with projections
    @Query(value = "{ 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2 }",
           fields = "{ 'employeeId': 1, 'employeeName': 1, 'netSalary': 1, 'status': 1 }")
    List<Salary> findSalarySummaryForPeriod(String institutionId, Integer payrollMonth, Integer payrollYear);

    @Query(value = "{ 'employeeId': ?0, 'institutionId': ?1 }",
           fields = "{ 'payrollMonth': 1, 'payrollYear': 1, 'netSalary': 1, 'basicSalary': 1, 'status': 1 }")
    List<Salary> findEmployeeSalarySummary(String employeeId, String institutionId);

    // Monthly totals and averages
    @Query("{ $match: { 'institutionId': ?0, 'payrollMonth': ?1, 'payrollYear': ?2, 'isPaid': true } }, " +
           "{ $group: { _id: null, totalPaidSalary: { $sum: '$netSalary' }, avgSalary: { $avg: '$netSalary' }, " +
           "count: { $sum: 1 }, totalBasicSalary: { $sum: '$basicSalary' } } }")
    Object getMonthlyPayrollSummary(String institutionId, Integer payrollMonth, Integer payrollYear);

    // Yearly analysis
    @Query("{ $match: { 'institutionId': ?0, 'payrollYear': ?1 } }, " +
           "{ $group: { _id: '$payrollMonth', totalSalary: { $sum: '$netSalary' }, count: { $sum: 1 } } }")
    List<Object> getYearlyPayrollTrend(String institutionId, Integer payrollYear);
}
