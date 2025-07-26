package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.SalaryStructure;
import com.eduai.schoolmanagement.entity.SalaryStructure.StructureStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryStructureRepository extends MongoRepository<SalaryStructure, String> {

    // Basic queries
    Optional<SalaryStructure> findByInstitutionIdAndGradeCodeAndIsActive(
        String institutionId, String gradeCode, Boolean isActive);

    List<SalaryStructure> findByInstitutionId(String institutionId);

    List<SalaryStructure> findByInstitutionIdAndIsActive(String institutionId, Boolean isActive);

    Page<SalaryStructure> findByInstitutionId(String institutionId, Pageable pageable);

    // Grade-specific queries
    List<SalaryStructure> findByInstitutionIdAndGradeCode(String institutionId, String gradeCode);

    Optional<SalaryStructure> findByInstitutionIdAndGradeCodeAndStatus(
        String institutionId, String gradeCode, StructureStatus status);

    List<SalaryStructure> findByInstitutionIdAndGradeCodeOrderByVersionDesc(
        String institutionId, String gradeCode);

    // Designation-specific queries
    List<SalaryStructure> findByInstitutionIdAndDesignation(String institutionId, String designation);

    List<SalaryStructure> findByInstitutionIdAndDesignationAndIsActive(
        String institutionId, String designation, Boolean isActive);

    Optional<SalaryStructure> findByInstitutionIdAndDesignationAndIsDefault(
        String institutionId, String designation, Boolean isDefault);

    // Employee type queries
    List<SalaryStructure> findByInstitutionIdAndEmployeeType(String institutionId, String employeeType);

    List<SalaryStructure> findByInstitutionIdAndEmployeeTypeAndIsActive(
        String institutionId, String employeeType, Boolean isActive);

    // Department queries
    List<SalaryStructure> findByInstitutionIdAndDepartment(String institutionId, String department);

    List<SalaryStructure> findByInstitutionIdAndDepartmentAndIsActive(
        String institutionId, String department, Boolean isActive);

    // Status-based queries
    List<SalaryStructure> findByInstitutionIdAndStatus(String institutionId, StructureStatus status);

    Page<SalaryStructure> findByInstitutionIdAndStatus(String institutionId, StructureStatus status, Pageable pageable);

    @Query("{ 'institutionId': ?0, 'status': 'PENDING_APPROVAL' }")
    List<SalaryStructure> findPendingApprovals(String institutionId);

    @Query("{ 'institutionId': ?0, 'status': 'ACTIVE' }")
    List<SalaryStructure> findActiveStructures(String institutionId);

    // Effective date queries
    @Query("{ 'institutionId': ?0, 'effectiveFromDate': { $lte: ?1 }, " +
           "$or: [ { 'effectiveToDate': null }, { 'effectiveToDate': { $gte: ?1 } } ] }")
    List<SalaryStructure> findEffectiveStructuresForDate(String institutionId, LocalDate date);

    @Query("{ 'institutionId': ?0, 'gradeCode': ?1, 'effectiveFromDate': { $lte: ?2 }, " +
           "$or: [ { 'effectiveToDate': null }, { 'effectiveToDate': { $gte: ?2 } } ], 'isActive': true }")
    Optional<SalaryStructure> findEffectiveStructureForGradeAndDate(
        String institutionId, String gradeCode, LocalDate date);

    List<SalaryStructure> findByInstitutionIdAndEffectiveFromDateBetween(
        String institutionId, LocalDate startDate, LocalDate endDate);

    List<SalaryStructure> findByInstitutionIdAndEffectiveToDateBetween(
        String institutionId, LocalDate startDate, LocalDate endDate);

    // Salary range queries
    @Query("{ 'institutionId': ?0, 'defaultBasicSalary': { $gte: ?1, $lte: ?2 } }")
    List<SalaryStructure> findBySalaryRange(String institutionId, Double minSalary, Double maxSalary);

    @Query("{ 'institutionId': ?0, 'minBasicSalary': { $lte: ?1 }, 'maxBasicSalary': { $gte: ?1 } }")
    List<SalaryStructure> findStructuresContainingSalary(String institutionId, Double salary);

    // Default structure queries
    List<SalaryStructure> findByInstitutionIdAndIsDefault(String institutionId, Boolean isDefault);

    Optional<SalaryStructure> findByInstitutionIdAndEmployeeTypeAndIsDefault(
        String institutionId, String employeeType, Boolean isDefault);

    Optional<SalaryStructure> findByInstitutionIdAndDepartmentAndIsDefault(
        String institutionId, String department, Boolean isDefault);

    // Version control queries
    List<SalaryStructure> findByInstitutionIdAndPreviousVersionId(String institutionId, String previousVersionId);

    Optional<SalaryStructure> findByInstitutionIdAndVersion(String institutionId, String version);

    @Query("{ 'institutionId': ?0, 'gradeCode': ?1 }")
    List<SalaryStructure> findAllVersionsForGrade(String institutionId, String gradeCode);

    @Query("{ 'institutionId': ?0, 'gradeCode': ?1, 'version': { $regex: '^?2' } }")
    List<SalaryStructure> findVersionsWithPrefix(String institutionId, String gradeCode, String versionPrefix);

    // Approval and audit queries
    List<SalaryStructure> findByInstitutionIdAndApprovedBy(String institutionId, String approvedBy);

    List<SalaryStructure> findByInstitutionIdAndApprovedDateBetween(
        String institutionId, LocalDateTime startDate, LocalDateTime endDate);

    List<SalaryStructure> findByInstitutionIdAndCreatedBy(String institutionId, String createdBy);

    List<SalaryStructure> findByInstitutionIdAndLastModifiedBy(String institutionId, String lastModifiedBy);

    // Component-based queries
    @Query("{ 'institutionId': ?0, 'components.componentCode': ?1 }")
    List<SalaryStructure> findByComponentCode(String institutionId, String componentCode);

    @Query("{ 'institutionId': ?0, 'allowances.?1': { $exists: true } }")
    List<SalaryStructure> findByAllowanceCode(String institutionId, String allowanceCode);

    @Query("{ 'institutionId': ?0, 'deductions.?1': { $exists: true } }")
    List<SalaryStructure> findByDeductionCode(String institutionId, String deductionCode);

    // PF and ESI eligibility queries
    @Query("{ 'institutionId': ?0, 'pfApplicable': true, 'isActive': true }")
    List<SalaryStructure> findPFApplicableStructures(String institutionId);

    @Query("{ 'institutionId': ?0, 'esiApplicable': true, 'isActive': true }")
    List<SalaryStructure> findESIApplicableStructures(String institutionId);

    @Query("{ 'institutionId': ?0, 'professionalTaxApplicable': true, 'isActive': true }")
    List<SalaryStructure> findProfessionalTaxApplicableStructures(String institutionId);

    // Overtime eligibility queries
    @Query("{ 'institutionId': ?0, 'overtimeEligible': true, 'isActive': true }")
    List<SalaryStructure> findOvertimeEligibleStructures(String institutionId);

    // Bonus structure queries
    @Query("{ 'institutionId': ?0, 'bonusStructure.annualBonusApplicable': true, 'isActive': true }")
    List<SalaryStructure> findAnnualBonusApplicableStructures(String institutionId);

    @Query("{ 'institutionId': ?0, 'bonusStructure.performanceBonusApplicable': true, 'isActive': true }")
    List<SalaryStructure> findPerformanceBonusApplicableStructures(String institutionId);

    @Query("{ 'institutionId': ?0, 'bonusStructure.attendanceBonusApplicable': true, 'isActive': true }")
    List<SalaryStructure> findAttendanceBonusApplicableStructures(String institutionId);

    // Increment structure queries
    @Query("{ 'institutionId': ?0, 'incrementStructure.nextIncrementDue': { $lte: ?1 }, 'isActive': true }")
    List<SalaryStructure> findStructuresDueForIncrement(String institutionId, LocalDate date);

    @Query("{ 'institutionId': ?0, 'incrementStructure.performanceLinked': true, 'isActive': true }")
    List<SalaryStructure> findPerformanceLinkedIncrementStructures(String institutionId);

    // Leave entitlement queries
    @Query("{ 'institutionId': ?0, 'annualLeaveEntitlement': { $gte: ?1 } }")
    List<SalaryStructure> findByMinimumLeaveEntitlement(String institutionId, Integer minLeaves);

    // Working conditions queries
    @Query("{ 'institutionId': ?0, 'standardWorkingDays': ?1 }")
    List<SalaryStructure> findByStandardWorkingDays(String institutionId, Integer workingDays);

    @Query("{ 'institutionId': ?0, 'standardWorkingHours': { $gte: ?1 } }")
    List<SalaryStructure> findByMinimumWorkingHours(String institutionId, Double minHours);

    // Statistical queries
    @Query(value = "{ 'institutionId': ?0, 'isActive': true }", count = true)
    long countActiveStructures(String institutionId);

    @Query(value = "{ 'institutionId': ?0, 'status': ?1 }", count = true)
    long countByStatus(String institutionId, StructureStatus status);

    @Query(value = "{ 'institutionId': ?0, 'employeeType': ?1, 'isActive': true }", count = true)
    long countActiveStructuresByEmployeeType(String institutionId, String employeeType);

    // Aggregation queries for analytics
    @Query("{ $group: { _id: '$employeeType', count: { $sum: 1 }, avgBasicSalary: { $avg: '$defaultBasicSalary' } } }")
    List<Object> getStructureStatsByEmployeeType(String institutionId);

    @Query("{ $group: { _id: '$department', count: { $sum: 1 }, maxSalary: { $max: '$maxBasicSalary' }, " +
           "minSalary: { $min: '$minBasicSalary' } } }")
    List<Object> getStructureStatsByDepartment(String institutionId);

    @Query("{ $group: { _id: '$gradeCode', avgSalary: { $avg: '$defaultBasicSalary' }, " +
           "structures: { $push: '$salaryStructureId' } } }")
    List<Object> getStructureStatsByGrade(String institutionId);

    // Search functionality
    @Query("{ 'institutionId': ?0, $or: [ { 'gradeCode': { $regex: ?1, $options: 'i' } }, " +
           "{ 'designation': { $regex: ?1, $options: 'i' } }, { 'department': { $regex: ?1, $options: 'i' } } ] }")
    List<SalaryStructure> searchStructures(String institutionId, String searchTerm);

    @Query("{ 'institutionId': ?0, 'gradeCode': { $regex: ?1, $options: 'i' } }")
    List<SalaryStructure> findByGradeCodeContaining(String institutionId, String gradeCodePart);

    @Query("{ 'institutionId': ?0, 'designation': { $regex: ?1, $options: 'i' } }")
    List<SalaryStructure> findByDesignationContaining(String institutionId, String designationPart);

    // Validation queries
    boolean existsByInstitutionIdAndGradeCodeAndIsActive(String institutionId, String gradeCode, Boolean isActive);

    boolean existsByInstitutionIdAndDesignationAndIsDefault(String institutionId, String designation, Boolean isDefault);

    @Query("{ 'institutionId': ?0, 'gradeCode': ?1, 'effectiveFromDate': { $lte: ?2 }, " +
           "'effectiveToDate': { $gte: ?2 }, 'status': 'ACTIVE' }")
    boolean existsActiveStructureForGradeAndDate(String institutionId, String gradeCode, LocalDate date);

    // Cleanup and maintenance
    void deleteByInstitutionIdAndStatus(String institutionId, StructureStatus status);

    @Query("{ 'institutionId': ?0, 'status': 'DRAFT', 'createdDate': { $lt: ?1 } }")
    List<SalaryStructure> findOldDraftStructures(String institutionId, LocalDateTime beforeDate);

    @Query("{ 'institutionId': ?0, 'effectiveToDate': { $lt: ?1 }, 'status': { $ne: 'SUPERSEDED' } }")
    List<SalaryStructure> findExpiredStructures(String institutionId, LocalDate date);

    // Bulk operations
    @Query("{ 'institutionId': ?0, 'gradeCode': { $in: ?1 }, 'isActive': true }")
    List<SalaryStructure> findActiveStructuresForGrades(String institutionId, List<String> gradeCodes);

    @Query("{ 'institutionId': ?0, 'designation': { $in: ?1 }, 'isActive': true }")
    List<SalaryStructure> findActiveStructuresForDesignations(String institutionId, List<String> designations);

    @Query("{ 'institutionId': ?0, 'employeeType': { $in: ?1 }, 'isActive': true }")
    List<SalaryStructure> findActiveStructuresForEmployeeTypes(String institutionId, List<String> employeeTypes);

    // Recent records for dashboard
    @Query("{ 'institutionId': ?0, 'createdDate': { $gte: ?1 } }")
    List<SalaryStructure> findRecentStructures(String institutionId, LocalDateTime since, Pageable pageable);

    @Query("{ 'institutionId': ?0, 'lastModifiedDate': { $gte: ?1 } }")
    List<SalaryStructure> findRecentlyModifiedStructures(String institutionId, LocalDateTime since, Pageable pageable);

    // Performance optimization with projections
    @Query(value = "{ 'institutionId': ?0, 'isActive': true }",
           fields = "{ 'gradeCode': 1, 'designation': 1, 'defaultBasicSalary': 1, 'employeeType': 1 }")
    List<SalaryStructure> findActiveStructuresSummary(String institutionId);

    @Query(value = "{ 'institutionId': ?0, 'status': 'ACTIVE' }",
           fields = "{ 'gradeCode': 1, 'designation': 1, 'minBasicSalary': 1, 'maxBasicSalary': 1 }")
    List<SalaryStructure> findSalaryRangesSummary(String institutionId);

    // Template and default queries
    @Query("{ 'institutionId': ?0, 'isDefault': true, 'employeeType': ?1 }")
    Optional<SalaryStructure> findDefaultTemplateForEmployeeType(String institutionId, String employeeType);

    @Query("{ 'institutionId': ?0, 'isActive': true, 'employeeType': ?1 }")
    List<SalaryStructure> findAvailableStructuresForEmployeeType(String institutionId, String employeeType);

    // Grade hierarchy queries (assuming grade codes follow a pattern)
    @Query("{ 'institutionId': ?0, 'gradeCode': { $regex: '^?1' }, 'isActive': true }")
    List<SalaryStructure> findStructuresInGradeFamily(String institutionId, String gradePrefix);

    @Query("{ 'institutionId': ?0, 'defaultBasicSalary': { $gt: ?1 }, 'isActive': true }")
    List<SalaryStructure> findHigherGradeStructures(String institutionId, Double currentSalary);

    @Query("{ 'institutionId': ?0, 'defaultBasicSalary': { $lt: ?1 }, 'isActive': true }")
    List<SalaryStructure> findLowerGradeStructures(String institutionId, Double currentSalary);
}
