package com.eduai.schoolmanagement.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.eduai.schoolmanagement.entity.Fee;

@Repository
public interface FeeRepository extends MongoRepository<Fee, String> {

    List<Fee> findByFeeType(String feeType);

    List<Fee> findByCategory(String category);

    List<Fee> findByAcademicYear(String academicYear);

    List<Fee> findByStatus(String status);

    List<Fee> findByMandatory(boolean mandatory);

    @Query("{'applicableGrades': {$in: [?0]}}")
    List<Fee> findByApplicableGrade(String grade);

    @Query("{'applicableDepartments': {$in: [?0]}}")
    List<Fee> findByApplicableDepartment(String department);

    @Query("{'dueDate': {$gte: ?0, $lte: ?1}}")
    List<Fee> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("{'dueDate': {$lt: ?0}}")
    List<Fee> findOverdueFees(LocalDate currentDate);

    @Query("{'feeName': {$regex: ?0, $options: 'i'}}")
    List<Fee> findByFeeNameContainingIgnoreCase(String feeName);

    @Query("{'academicYear': ?0, 'status': 'ACTIVE'}")
    List<Fee> findActiveFeesForAcademicYear(String academicYear);

    @Query("{'academicYear': ?0, 'applicableGrades': {$in: [?1]}, 'status': 'ACTIVE'}")
    List<Fee> findActiveFeesForGradeAndYear(String academicYear, String grade);

    @Query("{'scholarshipEligible': true, 'status': 'ACTIVE'}")
    List<Fee> findScholarshipEligibleFees();

    long countByFeeType(String feeType);

    long countByAcademicYear(String academicYear);

    long countByStatus(String status);
}
