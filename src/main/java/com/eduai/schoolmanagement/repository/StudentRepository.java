package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    Optional<Student> findByStudentId(String studentId);

    @Query("{'user.email': ?0}")
    Optional<Student> findByUserEmail(String email);

    List<Student> findByGrade(String grade);

    List<Student> findByGradeAndSection(String grade, String section);

    @Query("{'user.firstName': {$regex: ?0, $options: 'i'}}")
    Page<Student> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    @Query("{'user.lastName': {$regex: ?0, $options: 'i'}}")
    Page<Student> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    List<Student> findByAcademicStatus(String academicStatus);

    @Query("{'aiInsights.riskScore': {$gte: ?0}}")
    List<Student> findByRiskScoreGreaterThanEqual(double riskScore);

    @Query("{'aiInsights.performanceTrend': ?0}")
    List<Student> findByPerformanceTrend(String trend);

    long countByGrade(String grade);

    long countByAcademicStatus(String status);

    // Auto-ID generation support methods
    boolean existsByStudentId(String studentId);

    boolean existsByAdmissionNumber(String admissionNumber);

    @Query("{ 'grade': ?0, 'section': ?1, 'rollNumber': { $exists: true, $ne: null } }")
    List<Student> findByGradeAndSectionWithRollNumber(String grade, String section);

    @Query(value = "{ 'grade': ?0, 'section': ?1, 'rollNumber': { $exists: true, $ne: null } }",
           fields = "{ 'rollNumber': 1 }")
    List<Student> findRollNumbersByGradeAndSection(String grade, String section);

    default long findMaxRollNumberByGradeAndSection(String grade, String section) {
        List<Student> students = findRollNumbersByGradeAndSection(grade, section);
        return students.stream()
                .filter(s -> s.getRollNumber() != null && s.getRollNumber().matches("\\d+"))
                .mapToLong(s -> Long.parseLong(s.getRollNumber()))
                .max()
                .orElse(0L);
    }
}
