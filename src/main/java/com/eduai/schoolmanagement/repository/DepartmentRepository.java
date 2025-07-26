package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.Department;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends MongoRepository<Department, String> {

    Optional<Department> findByDepartmentCode(String departmentCode);

    Optional<Department> findByDepartmentCodeAndActive(String departmentCode, Boolean active);

    List<Department> findByActiveTrue();

    List<Department> findByActiveTrueOrderByDisplayOrder();

    List<Department> findByActiveTrueOrderByDepartmentName();

    @Query("{ 'maxTeachers': { $gt: '$currentTeachers' }, 'active': true }")
    List<Department> findDepartmentsWithAvailablePositions();

    @Query("{ 'currentTeachers': { $gte: '$maxTeachers' }, 'active': true }")
    List<Department> findFullDepartments();

    List<Department> findByHeadOfDepartment(String headOfDepartment);

    boolean existsByDepartmentCode(String departmentCode);

    boolean existsByDepartmentCodeAndActive(String departmentCode, Boolean active);

    @Query("{ 'contactEmail': ?0, 'active': true }")
    Optional<Department> findByContactEmail(String contactEmail);

    long countByActiveTrue();

    @Query("{ 'active': true }")
    @org.springframework.data.mongodb.repository.Aggregation(pipeline = {
        "{ $match: { 'active': true } }",
        "{ $group: { _id: null, total: { $sum: '$maxTeachers' } } }"
    })
    Integer getTotalTeacherCapacity();

    @Query("{ 'active': true }")
    @org.springframework.data.mongodb.repository.Aggregation(pipeline = {
        "{ $match: { 'active': true } }",
        "{ $group: { _id: null, total: { $sum: '$currentTeachers' } } }"
    })
    Integer getTotalCurrentTeachers();
}
