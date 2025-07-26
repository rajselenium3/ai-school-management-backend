package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.AccessCode;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.Parent;
import com.eduai.schoolmanagement.repository.AccessCodeRepository;
import com.eduai.schoolmanagement.repository.StudentRepository;
import com.eduai.schoolmanagement.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessCodeService {

    private final AccessCodeRepository accessCodeRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;

    // ====================
    // ACCESS CODE MANAGEMENT
    // ====================

    public List<AccessCode> getAllAccessCodes() {
        return accessCodeRepository.findAll();
    }

    public Optional<AccessCode> getAccessCodeByCode(String accessCode) {
        return accessCodeRepository.findByAccessCodeAndActive(accessCode, true);
    }

    public List<AccessCode> getAccessCodesByStudentId(String studentId) {
        return accessCodeRepository.findByStudentIdAndActiveTrue(studentId);
    }

    public List<AccessCode> getAccessCodesByParentEmail(String parentEmail) {
        return accessCodeRepository.findByParentEmailAndActiveTrue(parentEmail);
    }

    public List<AccessCode> getValidUnusedCodes() {
        return accessCodeRepository.findValidUnusedCodes();
    }

    public List<AccessCode> getValidUnusedCodesByType(String codeType) {
        return accessCodeRepository.findValidUnusedCodesByType(codeType);
    }

    // ====================
    // STUDENT ACCESS CODE GENERATION
    // ====================

    @Transactional
    public AccessCode generateStudentAccessCode(String studentId) {
        // Check if student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        // Check if student already has an active access code
        Optional<AccessCode> existingCode = accessCodeRepository.findValidUnusedCodeByStudentId(studentId);
        if (existingCode.isPresent()) {
            log.info("Student {} already has an active access code: {}", studentId, existingCode.get().getAccessCode());
            return existingCode.get();
        }

        // Generate new access code
        String studentName = (student.getUser() != null) ?
                             student.getUser().getFirstName() + " " + student.getUser().getLastName() :
                             "Student";

        AccessCode accessCode = AccessCode.createStudentAccessCode(studentId, studentName);
        AccessCode savedCode = accessCodeRepository.save(accessCode);

        log.info("Generated student access code {} for student {} ({})",
                 savedCode.getAccessCode(), studentId, studentName);

        return savedCode;
    }

    @Transactional
    public List<AccessCode> generateStudentAndParentAccessCodes(String studentId) {
        List<AccessCode> generatedCodes = new java.util.ArrayList<>();

        // Generate student access code
        AccessCode studentCode = generateStudentAccessCode(studentId);
        generatedCodes.add(studentCode);

        // Generate parent access codes for all parents linked to this student
        List<AccessCode> parentCodes = generateParentAccessCodes(studentId);
        generatedCodes.addAll(parentCodes);

        return generatedCodes;
    }

    // ====================
    // PARENT ACCESS CODE GENERATION
    // ====================

    @Transactional
    public List<AccessCode> generateParentAccessCodes(String studentId) {
        // Get student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        List<AccessCode> parentCodes = new java.util.ArrayList<>();

        // Get all parents linked to this student
        List<Parent> parents = parentRepository.findParentsByChildStudentId(studentId);

        String studentName = (student.getUser() != null) ?
                             student.getUser().getFirstName() + " " + student.getUser().getLastName() :
                             "Student";

        for (Parent parent : parents) {
            // Check if parent already has an active access code for this student
            List<AccessCode> existingCodes = accessCodeRepository.findValidUnusedCodesByParentEmail(parent.getUser().getEmail());

            boolean hasValidCode = existingCodes.stream()
                    .anyMatch(code -> studentId.equals(code.getStudentId()));

            if (!hasValidCode) {
                String parentName = parent.getUser().getFirstName() + " " + parent.getUser().getLastName();

                AccessCode parentCode = AccessCode.createParentAccessCode(
                        parent.getUser().getEmail(),
                        parentName,
                        studentId,
                        studentName
                );

                AccessCode savedCode = accessCodeRepository.save(parentCode);
                parentCodes.add(savedCode);

                log.info("Generated parent access code {} for parent {} (child: {})",
                         savedCode.getAccessCode(), parent.getUser().getEmail(), studentName);
            } else {
                log.info("Parent {} already has an active access code for student {}",
                         parent.getUser().getEmail(), studentId);
            }
        }

        return parentCodes;
    }

    @Transactional
    public AccessCode generateParentAccessCode(String parentEmail, String studentId) {
        // Check if student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        // Check if parent exists
        Parent parent = parentRepository.findByUserEmail(parentEmail)
                .orElseThrow(() -> new RuntimeException("Parent not found with email: " + parentEmail));

        // Check if parent already has an active access code for this student
        List<AccessCode> existingCodes = accessCodeRepository.findValidUnusedCodesByParentEmail(parentEmail);
        Optional<AccessCode> existingCode = existingCodes.stream()
                .filter(code -> studentId.equals(code.getStudentId()))
                .findFirst();

        if (existingCode.isPresent()) {
            log.info("Parent {} already has an active access code for student {}: {}",
                     parentEmail, studentId, existingCode.get().getAccessCode());
            return existingCode.get();
        }

        // Generate new access code
        String studentName = (student.getUser() != null) ?
                             student.getUser().getFirstName() + " " + student.getUser().getLastName() :
                             "Student";
        String parentName = parent.getUser().getFirstName() + " " + parent.getUser().getLastName();

        AccessCode accessCode = AccessCode.createParentAccessCode(parentEmail, parentName, studentId, studentName);
        AccessCode savedCode = accessCodeRepository.save(accessCode);

        log.info("Generated parent access code {} for parent {} (child: {})",
                 savedCode.getAccessCode(), parentEmail, studentName);

        return savedCode;
    }

    // ====================
    // ACCESS CODE VALIDATION
    // ====================

    public boolean validateAccessCode(String accessCode, String userType) {
        Optional<AccessCode> codeOpt = accessCodeRepository.findByAccessCodeAndActive(accessCode, true);

        if (codeOpt.isEmpty()) {
            log.warn("Access code not found: {}", accessCode);
            return false;
        }

        AccessCode code = codeOpt.get();

        // Check if code matches user type
        if (!userType.equals(code.getUserType())) {
            log.warn("Access code {} is for {} but user type is {}", accessCode, code.getUserType(), userType);
            return false;
        }

        // Check if code is valid
        if (!code.isValid()) {
            log.warn("Access code {} is not valid: used={}, expired={}, status={}",
                     accessCode, code.getIsUsed(), code.getIsExpired(), code.getStatus());
            return false;
        }

        // Check if code can be used
        if (!code.canBeUsed()) {
            log.warn("Access code {} cannot be used: max usage exceeded", accessCode);
            return false;
        }

        return true;
    }

    @Transactional
    public Map<String, Object> useAccessCode(String accessCode, String usedBy, String userType) {
        Map<String, Object> result = new HashMap<>();

        if (!validateAccessCode(accessCode, userType)) {
            result.put("success", false);
            result.put("message", "Invalid or expired access code");
            return result;
        }

        Optional<AccessCode> codeOpt = accessCodeRepository.findByAccessCodeAndActive(accessCode, true);
        AccessCode code = codeOpt.get();

        // Mark code as used
        code.markAsUsed(usedBy);
        accessCodeRepository.save(code);

        result.put("success", true);
        result.put("message", "Access code used successfully");
        result.put("codeType", code.getCodeType());
        result.put("userType", code.getUserType());
        result.put("entityId", code.getEntityId());
        result.put("studentId", code.getStudentId());
        result.put("parentEmail", code.getParentEmail());

        log.info("Access code {} used by {} for user type {}", accessCode, usedBy, userType);

        return result;
    }

    // ====================
    // ACCESS CODE REGENERATION
    // ====================

    @Transactional
    public AccessCode regenerateAccessCode(String accessCodeId) {
        AccessCode existingCode = accessCodeRepository.findById(accessCodeId)
                .orElseThrow(() -> new RuntimeException("Access code not found with ID: " + accessCodeId));

        existingCode.regenerate();
        AccessCode savedCode = accessCodeRepository.save(existingCode);

        log.info("Regenerated access code for {} ({}): new code = {}",
                 existingCode.getCodeType(), existingCode.getEntityId(), savedCode.getAccessCode());

        return savedCode;
    }

    @Transactional
    public AccessCode regenerateStudentAccessCode(String studentId) {
        Optional<AccessCode> existingCodeOpt = accessCodeRepository.findValidUnusedCodeByStudentId(studentId);

        if (existingCodeOpt.isPresent()) {
            return regenerateAccessCode(existingCodeOpt.get().getId());
        } else {
            // If no existing code, generate a new one
            return generateStudentAccessCode(studentId);
        }
    }

    @Transactional
    public List<AccessCode> regenerateParentAccessCodes(String studentId) {
        List<AccessCode> regeneratedCodes = new java.util.ArrayList<>();

        // Get all parents linked to this student
        List<Parent> parents = parentRepository.findParentsByChildStudentId(studentId);

        for (Parent parent : parents) {
            List<AccessCode> existingCodes = accessCodeRepository.findValidUnusedCodesByParentEmail(parent.getUser().getEmail());

            Optional<AccessCode> existingCode = existingCodes.stream()
                    .filter(code -> studentId.equals(code.getStudentId()))
                    .findFirst();

            if (existingCode.isPresent()) {
                AccessCode regeneratedCode = regenerateAccessCode(existingCode.get().getId());
                regeneratedCodes.add(regeneratedCode);
            } else {
                // If no existing code, generate a new one
                AccessCode newCode = generateParentAccessCode(parent.getUser().getEmail(), studentId);
                regeneratedCodes.add(newCode);
            }
        }

        return regeneratedCodes;
    }

    // ====================
    // ACCESS CODE MANAGEMENT
    // ====================

    @Transactional
    public void revokeAccessCode(String accessCodeId, String reason) {
        AccessCode code = accessCodeRepository.findById(accessCodeId)
                .orElseThrow(() -> new RuntimeException("Access code not found with ID: " + accessCodeId));

        code.revoke(reason);
        accessCodeRepository.save(code);

        log.info("Revoked access code {} for reason: {}", code.getAccessCode(), reason);
    }

    @Transactional
    public void cleanupExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oldDate = now.minusDays(30); // Clean up unused codes older than 30 days

        List<AccessCode> codesToCleanup = accessCodeRepository.findCodesForCleanup(now, oldDate);

        for (AccessCode code : codesToCleanup) {
            if (code.isExpiredByDate()) {
                code.markAsExpired();
            } else {
                code.revoke("Automatic cleanup - old unused code");
            }
            accessCodeRepository.save(code);
        }

        log.info("Cleaned up {} expired/old access codes", codesToCleanup.size());
    }

    // ====================
    // STATISTICS AND REPORTING
    // ====================

    public Map<String, Object> getAccessCodeStatistics() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalCodes", accessCodeRepository.count());
        stats.put("activeCodes", accessCodeRepository.countByStatusAndActiveTrue("ACTIVE"));
        stats.put("usedCodes", accessCodeRepository.countByStatusAndActiveTrue("USED"));
        stats.put("expiredCodes", accessCodeRepository.countByStatusAndActiveTrue("EXPIRED"));
        stats.put("revokedCodes", accessCodeRepository.countByStatusAndActiveTrue("REVOKED"));
        stats.put("studentCodes", accessCodeRepository.countByCodeTypeAndActiveTrue("STUDENT"));
        stats.put("parentCodes", accessCodeRepository.countByCodeTypeAndActiveTrue("PARENT"));

        return stats;
    }

    public List<AccessCode> getAccessCodesGeneratedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return accessCodeRepository.findByGeneratedDateRange(startDate, endDate);
    }

    public List<AccessCode> getAccessCodesUsedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return accessCodeRepository.findByUsedDateRange(startDate, endDate);
    }
}
