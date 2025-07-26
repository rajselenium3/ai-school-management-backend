package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    // Basic queries
    Optional<Employee> findByEmployeeId(String employeeId);

    @Query("{'personalInfo.email': ?0}")
    Optional<Employee> findByPersonalInfoEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    @Query("{'personalInfo.email': ?0}")
    boolean existsByPersonalInfoEmail(String email);

    // Department and position queries
    @Query("{'employmentInfo.department': ?0}")
    List<Employee> findByEmploymentInfoDepartment(String department);

    @Query("{'employmentInfo.position': ?0}")
    List<Employee> findByEmploymentInfoPosition(String position);

    @Query("{'employmentInfo.employmentType': ?0}")
    List<Employee> findByEmploymentInfoEmploymentType(String employmentType);

    // Status queries
    List<Employee> findByStatus(String status);

    long countByStatus(String status);

    @Query("{'employmentInfo.department': ?0}")
    long countByEmploymentInfoDepartment(String department);

    @Query("{'employmentInfo.employmentType': ?0}")
    long countByEmploymentInfoEmploymentType(String employmentType);

    // Search queries
    @Query("{'$or': [{'personalInfo.firstName': {$regex: ?0, $options: 'i'}}, {'personalInfo.lastName': {$regex: ?0, $options: 'i'}}]}")
    Page<Employee> findByPersonalInfoFirstNameContainingIgnoreCaseOrPersonalInfoLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);

    @Query("{'personalInfo.firstName': {$regex: ?0, $options: 'i'}}")
    Page<Employee> findByPersonalInfoFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    // Complex queries for filtering
    @Query("{'employmentInfo.department': ?0, 'employmentInfo.employmentType': ?1}")
    List<Employee> findByEmploymentInfoDepartmentAndEmploymentInfoEmploymentType(String department, String employmentType);

    @Query("{'employmentInfo.department': ?0, 'status': ?1}")
    List<Employee> findByEmploymentInfoDepartmentAndStatus(String department, String status);

    @Query("{'employmentInfo.employmentType': ?0, 'status': ?1}")
    List<Employee> findByEmploymentInfoEmploymentTypeAndStatus(String employmentType, String status);

    // Login credential queries
    @Query("{'loginCredentials.username': ?0}")
    Optional<Employee> findByLoginCredentialsUsername(String username);

    @Query("{'loginCredentials.hasAccount': true}")
    List<Employee> findByLoginCredentialsHasAccountTrue();

    @Query("{'loginCredentials.isLocked': true}")
    List<Employee> findByLoginCredentialsIsLockedTrue();

    // Manager queries
    @Query("{'employmentInfo.reportingManager': ?0}")
    List<Employee> findByEmploymentInfoReportingManager(String manager);
}
