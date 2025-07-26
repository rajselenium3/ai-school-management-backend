package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Basic queries
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByRoles(User.Role role);
    List<User> findByRolesIn(Set<User.Role> roles);

    // Status queries
    List<User> findByActive(boolean active);
    List<User> findByEmailVerified(boolean emailVerified);
    List<User> findByLocked(boolean locked);

    // Combined status queries
    List<User> findByActiveAndEmailVerified(boolean active, boolean emailVerified);
    List<User> findByActiveAndLocked(boolean active, boolean locked);

    // Search queries
    @Query("{'$or': [{'firstName': {'$regex': ?0, '$options': 'i'}}, " +
           "{'lastName': {'$regex': ?0, '$options': 'i'}}, " +
           "{'email': {'$regex': ?0, '$options': 'i'}}]}")
    List<User> searchByNameOrEmail(String searchTerm);

    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    List<User> findByLastNameContainingIgnoreCase(String lastName);

    // Verification and reset codes
    Optional<User> findByVerificationCode(String verificationCode);
    Optional<User> findByPasswordResetCode(String passwordResetCode);

    // Date range queries
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<User> findByLastLoginBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Role-specific queries
    @Query("{'roles': ?0, 'active': true}")
    List<User> findActiveUsersByRole(User.Role role);

    @Query("{'roles': {'$in': ?0}, 'active': true, 'emailVerified': true}")
    List<User> findActiveVerifiedUsersByRoles(Set<User.Role> roles);

    // Count queries
    long countByRoles(User.Role role);
    long countByActive(boolean active);
    long countByEmailVerified(boolean emailVerified);

    // Analytics queries
    @Query(value = "{'roles': ?0}", count = true)
    long countUsersByRole(User.Role role);

    @Query("{'createdAt': {'$gte': ?0}}")
    List<User> findUsersCreatedAfter(LocalDateTime date);

    @Query("{'lastLogin': {'$gte': ?0}}")
    List<User> findUsersLoggedInAfter(LocalDateTime date);

    // Cleanup queries for expired codes
    @Query("{'codeExpiryTime': {'$lt': ?0}}")
    List<User> findUsersWithExpiredVerificationCodes(LocalDateTime now);

    @Query("{'resetCodeExpiryTime': {'$lt': ?0}}")
    List<User> findUsersWithExpiredResetCodes(LocalDateTime now);

    // Custom update operations would be handled in service layer
    // These are read-only repository methods following Spring Data conventions
}
