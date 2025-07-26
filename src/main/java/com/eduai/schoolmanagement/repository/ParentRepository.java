package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.Parent;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentRepository extends MongoRepository<Parent, String> {

    Optional<Parent> findByParentId(String parentId);

    Optional<Parent> findByUser(User user);

    Optional<Parent> findByUserEmail(String email);

    List<Parent> findByIsActiveTrue();

    List<Parent> findByChildrenContaining(Student student);

    @Query("{ 'children._id': ?0 }")
    List<Parent> findParentsByChildId(String childId);

    @Query("{ 'children.studentId': ?0 }")
    List<Parent> findParentsByChildStudentId(String studentId);

    @Query("{ 'user.email': ?0, 'isActive': true }")
    Optional<Parent> findActiveParentByEmail(String email);

    @Query("{ 'isPrimary': true, 'children._id': ?0 }")
    Optional<Parent> findPrimaryParentByChildId(String childId);

    List<Parent> findByCanMakePaymentsTrue();

    List<Parent> findByReceiveBillingNotificationsTrue();

    long countByIsActiveTrue();
}
