package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends MongoRepository<Teacher, String> {

    // Basic queries
    Optional<Teacher> findByEmployeeId(String employeeId);
    boolean existsByEmployeeId(String employeeId);

    Optional<Teacher> findByUserEmail(String email);
    boolean existsByUserEmail(String email);

    // Department and designation queries
    List<Teacher> findByDepartment(String department);
    List<Teacher> findByDesignation(String designation);
    List<Teacher> findByDepartmentAndDesignation(String department, String designation);

    // Employment type queries
    List<Teacher> findByEmploymentType(String employmentType);

    // Performance queries
    List<Teacher> findByPerformanceScoreGreaterThan(double score);
    List<Teacher> findByStudentRatingGreaterThan(double rating);

    @Query("{'performanceScore': {'$gte': ?0}, 'studentRating': {'$gte': ?1}}")
    List<Teacher> findByPerformanceAndRating(double minPerformance, double minRating);

    // Subject and class queries
    @Query("{'subjectsHandled': {'$in': [?0]}}")
    List<Teacher> findBySubjectsHandled(String subject);

    @Query("{'classesAssigned': {'$in': [?0]}}")
    List<Teacher> findByClassesAssigned(String className);

    // Date range queries
    List<Teacher> findByJoiningDateBetween(LocalDate startDate, LocalDate endDate);
    List<Teacher> findByJoiningDateAfter(LocalDate date);
    List<Teacher> findByJoiningDateBefore(LocalDate date);

    // Search queries
    @Query("{'$or': [{'user.firstName': {'$regex': ?0, '$options': 'i'}}, " +
           "{'user.lastName': {'$regex': ?0, '$options': 'i'}}, " +
           "{'user.email': {'$regex': ?0, '$options': 'i'}}, " +
           "{'employeeId': {'$regex': ?0, '$options': 'i'}}]}")
    List<Teacher> searchByNameEmailOrEmployeeId(String searchTerm);

    @Query("{'$or': [{'user.firstName': {'$regex': ?0, '$options': 'i'}}, " +
           "{'user.lastName': {'$regex': ?0, '$options': 'i'}}, " +
           "{'user.email': {'$regex': ?0, '$options': 'i'}}, " +
           "{'employeeId': {'$regex': ?0, '$options': 'i'}}]}")
    Page<Teacher> searchByNameEmailOrEmployeeIdPageable(String searchTerm, Pageable pageable);

    List<Teacher> findByUserFirstNameContainingIgnoreCase(String firstName);
    List<Teacher> findByUserLastNameContainingIgnoreCase(String lastName);

    // Paginated search methods
    Page<Teacher> findByUserFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
    Page<Teacher> findByUserLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    // Workload queries
    List<Teacher> findByTotalClassesAssignedGreaterThan(int classes);
    List<Teacher> findByTotalStudentsHandledGreaterThan(int students);

    @Query("{'totalClassesAssigned': {'$lte': ?0}}")
    List<Teacher> findTeachersWithLowWorkload(int maxClasses);

    // Qualification and experience queries
    @Query("{'qualifications': {'$regex': ?0, '$options': 'i'}}")
    List<Teacher> findByQualificationsContaining(String qualification);

    @Query("{'specialization': {'$regex': ?0, '$options': 'i'}}")
    List<Teacher> findBySpecializationContaining(String specialization);

    @Query("{'certifications': {'$in': [?0]}}")
    List<Teacher> findByCertificationsContaining(String certification);

    // Salary range queries
    @Query("{'salaryInfo.basicSalary': {'$gte': ?0, '$lte': ?1}}")
    List<Teacher> findBySalaryRange(double minSalary, double maxSalary);

    @Query("{'salaryInfo.salaryGrade': ?0}")
    List<Teacher> findBySalaryGrade(String grade);

    // Active employees
    @Query("{'employmentType': {'$in': ['FULL_TIME', 'PART_TIME']}}")
    List<Teacher> findActiveTeachers();

    @Query("{'employmentType': {'$in': ['CONTRACT', 'TEMPORARY']}}")
    List<Teacher> findContractTeachers();

    // Count queries
    long countByDepartment(String department);
    long countByEmploymentType(String employmentType);
    long countByDesignation(String designation);

    // Top performers
    @Query(value = "{}", sort = "{'performanceScore': -1}")
    List<Teacher> findTopPerformers();

    @Query(value = "{}", sort = "{'studentRating': -1}")
    List<Teacher> findHighestRatedTeachers();

    // Recent joiners
    @Query(value = "{'joiningDate': {'$gte': ?0}}", sort = "{'joiningDate': -1}")
    List<Teacher> findRecentJoiners(LocalDate fromDate);

    // Experience-based queries
    @Query("{'joiningDate': {'$lte': ?0}}")
    List<Teacher> findSeniorTeachers(LocalDate beforeDate);

    // Department statistics
    @Query(value = "{'department': ?0}", count = true)
    long countTeachersByDepartment(String department);

    // Login credential queries
    @Query("{'loginCredentials.hasAccount': ?0}")
    List<Teacher> findByHasAccount(boolean hasAccount);

    @Query("{'loginCredentials.isLocked': ?0}")
    List<Teacher> findByAccountLocked(boolean locked);
}
