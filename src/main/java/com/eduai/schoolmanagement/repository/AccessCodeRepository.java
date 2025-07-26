package com.eduai.schoolmanagement.repository;

import com.eduai.schoolmanagement.entity.AccessCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccessCodeRepository extends MongoRepository<AccessCode, String> {

    Optional<AccessCode> findByAccessCode(String accessCode);

    Optional<AccessCode> findByAccessCodeAndActive(String accessCode, Boolean active);

    List<AccessCode> findByCodeType(String codeType);

    List<AccessCode> findByCodeTypeAndActiveTrue(String codeType);

    List<AccessCode> findByUserType(String userType);

    List<AccessCode> findByUserTypeAndActiveTrue(String userType);

    List<AccessCode> findByStudentId(String studentId);

    List<AccessCode> findByStudentIdAndActiveTrue(String studentId);

    List<AccessCode> findByParentEmail(String parentEmail);

    List<AccessCode> findByParentEmailAndActiveTrue(String parentEmail);

    List<AccessCode> findByEntityId(String entityId);

    List<AccessCode> findByEntityIdAndActiveTrue(String entityId);

    List<AccessCode> findByStatus(String status);

    List<AccessCode> findByStatusAndActiveTrue(String status);

    List<AccessCode> findByIsUsed(Boolean isUsed);

    List<AccessCode> findByIsUsedAndActiveTrue(Boolean isUsed);

    List<AccessCode> findByIsExpired(Boolean isExpired);

    List<AccessCode> findByIsExpiredAndActiveTrue(Boolean isExpired);

    @Query("{ 'expiryDate': { $lt: ?0 }, 'active': true }")
    List<AccessCode> findExpiredCodes(LocalDateTime now);

    @Query("{ 'status': 'ACTIVE', 'isUsed': false, 'isExpired': false, 'active': true }")
    List<AccessCode> findValidUnusedCodes();

    @Query("{ 'status': 'ACTIVE', 'isUsed': false, 'isExpired': false, 'codeType': ?0, 'active': true }")
    List<AccessCode> findValidUnusedCodesByType(String codeType);

    @Query("{ 'status': 'ACTIVE', 'isUsed': false, 'isExpired': false, 'studentId': ?0, 'active': true }")
    Optional<AccessCode> findValidUnusedCodeByStudentId(String studentId);

    @Query("{ 'status': 'ACTIVE', 'isUsed': false, 'isExpired': false, 'parentEmail': ?0, 'active': true }")
    List<AccessCode> findValidUnusedCodesByParentEmail(String parentEmail);

    boolean existsByAccessCode(String accessCode);

    boolean existsByAccessCodeAndActive(String accessCode, Boolean active);

    long countByCodeTypeAndActiveTrue(String codeType);

    long countByStatusAndActiveTrue(String status);

    @Query("{ 'generatedDate': { $gte: ?0, $lte: ?1 }, 'active': true }")
    List<AccessCode> findByGeneratedDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'usedDate': { $gte: ?0, $lte: ?1 }, 'active': true }")
    List<AccessCode> findByUsedDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Method to find codes that need cleanup (expired or old unused codes)
    @Query("{ $or: [ " +
           "{ 'expiryDate': { $lt: ?0 } }, " +
           "{ 'generatedDate': { $lt: ?1 }, 'isUsed': false } " +
           "], 'active': true }")
    List<AccessCode> findCodesForCleanup(LocalDateTime now, LocalDateTime oldDate);
}
