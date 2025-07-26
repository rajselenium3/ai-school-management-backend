package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.Family;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyRepository extends MongoRepository<Family, String> {

    // Basic queries
    Optional<Family> findByFamilyName(String familyName);

    boolean existsByFamilyName(String familyName);

    // Student-related queries
    @Query("{'studentIds': ?0}")
    Optional<Family> findByStudentIdsContaining(String studentId);

    @Query("{'studentIds': {$in: ?0}}")
    List<Family> findByStudentIdsIn(List<String> studentIds);

    // Contact queries
    @Query("{'primaryContact.email': ?0}")
    Optional<Family> findByPrimaryContactEmail(String email);

    @Query("{'secondaryContact.email': ?0}")
    Optional<Family> findBySecondaryContactEmail(String email);

    @Query("{'$or': [{'primaryContact.email': ?0}, {'secondaryContact.email': ?0}]}")
    List<Family> findByPrimaryContactEmailOrSecondaryContactEmail(String email);

    // Search queries
    @Query("{'familyName': {$regex: ?0, $options: 'i'}}")
    List<Family> findByFamilyNameContainingIgnoreCase(String familyName);

    @Query("{'$or': [{'primaryContact.name': {$regex: ?0, $options: 'i'}}, {'secondaryContact.name': {$regex: ?0, $options: 'i'}}]}")
    List<Family> findByPrimaryContactNameContainingIgnoreCaseOrSecondaryContactNameContainingIgnoreCase(String name);

    // Count queries
    long countByStudentIdsNotEmpty();
}
