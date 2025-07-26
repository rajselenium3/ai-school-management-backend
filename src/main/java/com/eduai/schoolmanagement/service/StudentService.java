package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.Parent;
import com.eduai.schoolmanagement.entity.AccessCode;
import com.eduai.schoolmanagement.entity.User;
import com.eduai.schoolmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final IDGenerationService idGenerationService;
    private final AccessCodeService accessCodeService;
    private final ParentService parentService;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByUserEmail(email);
    }

    public List<Student> getStudentsByGrade(String grade) {
        return studentRepository.findByGrade(grade);
    }

    public List<Student> getStudentsByGradeAndSection(String grade, String section) {
        return studentRepository.findByGradeAndSection(grade, section);
    }

    public Page<Student> searchStudentsByName(String name, Pageable pageable) {
        return studentRepository.findByFirstNameContainingIgnoreCase(name, pageable);
    }

    public List<Student> getAtRiskStudents(double riskThreshold) {
        return studentRepository.findByRiskScoreGreaterThanEqual(riskThreshold);
    }

    public List<Student> getStudentsByPerformanceTrend(String trend) {
        return studentRepository.findByPerformanceTrend(trend);
    }

    @Transactional
    public Map<String, Object> saveStudentWithAccessCodes(Student student) {
        // Auto-generate IDs if not provided
        if (student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            if (student.getGrade() == null) {
                throw new RuntimeException("Grade is required to generate Student ID");
            }
            String generatedStudentId = idGenerationService.generateStudentId(student.getGrade(), student.getSection());
            student.setStudentId(generatedStudentId);
            log.info("Auto-generated Student ID: {}", generatedStudentId);
        } else {
            // Validate for duplicates if manually provided
            if (studentRepository.existsByStudentId(student.getStudentId())) {
                throw new RuntimeException("Student ID already exists: " + student.getStudentId());
            }
        }

        if (student.getAdmissionNumber() == null || student.getAdmissionNumber().trim().isEmpty()) {
            String generatedAdmissionNumber = idGenerationService.generateAdmissionNumber();
            student.setAdmissionNumber(generatedAdmissionNumber);
            log.info("Auto-generated Admission Number: {}", generatedAdmissionNumber);
        } else {
            // Validate for duplicates if manually provided
            if (studentRepository.existsByAdmissionNumber(student.getAdmissionNumber())) {
                throw new RuntimeException("Admission Number already exists: " + student.getAdmissionNumber());
            }
        }

        if (student.getRollNumber() == null || student.getRollNumber().trim().isEmpty()) {
            if (student.getGrade() == null || student.getSection() == null) {
                throw new RuntimeException("Grade and Section are required to generate Roll Number");
            }
            String generatedRollNumber = idGenerationService.generateRollNumber(student.getGrade(), student.getSection());
            student.setRollNumber(generatedRollNumber);
            log.info("Auto-generated Roll Number: {} for Grade: {}, Section: {}",
                    generatedRollNumber, student.getGrade(), student.getSection());
        }

        // Set enrollment date if not provided
        if (student.getEnrollmentDate() == null) {
            student.setEnrollmentDate(java.time.LocalDate.now());
        }

        // Set default academic status if not provided
        if (student.getAcademicStatus() == null) {
            student.setAcademicStatus("ACTIVE");
        }

        log.info("Saving student: {} with auto-generated IDs", student.getStudentId());
        Student savedStudent = studentRepository.save(student);

        // Handle parent mapping and access code generation
        List<AccessCode> generatedAccessCodes = new java.util.ArrayList<>();
        List<Parent> createdParents = new java.util.ArrayList<>();

        // Create and map parents if provided
        if (savedStudent.getParents() != null && !savedStudent.getParents().isEmpty()) {
            for (Student.ParentInfo parentInfo : savedStudent.getParents()) {
                try {
                    // Create or find parent
                    Parent parent = createOrFindParent(parentInfo, savedStudent);
                    createdParents.add(parent);

                    // Generate parent access code
                    AccessCode parentAccessCode = accessCodeService.generateParentAccessCode(
                            parent.getUser().getEmail(),
                            savedStudent.getId()
                    );
                    generatedAccessCodes.add(parentAccessCode);

                } catch (Exception e) {
                    log.error("Error creating parent mapping for student {}: {}",
                             savedStudent.getStudentId(), e.getMessage());
                }
            }
        }

        // Generate student access code
        try {
            AccessCode studentAccessCode = accessCodeService.generateStudentAccessCode(savedStudent.getId());
            generatedAccessCodes.add(studentAccessCode);
        } catch (Exception e) {
            log.error("Error generating student access code for {}: {}",
                     savedStudent.getStudentId(), e.getMessage());
        }

        // Prepare response
        Map<String, Object> result = new HashMap<>();
        result.put("student", savedStudent);
        result.put("accessCodes", generatedAccessCodes);
        result.put("parents", createdParents);
        result.put("message", "Student enrolled successfully with access codes generated");

        log.info("Student {} enrolled with {} access codes generated",
                savedStudent.getStudentId(), generatedAccessCodes.size());

        return result;
    }

    // Backward compatibility method
    public Student saveStudent(Student student) {
        Map<String, Object> result = saveStudentWithAccessCodes(student);
        return (Student) result.get("student");
    }

    private Parent createOrFindParent(Student.ParentInfo parentInfo, Student student) {
        // Check if parent already exists
        Optional<Parent> existingParent = parentService.getParentByEmail(parentInfo.getEmail());

        if (existingParent.isPresent()) {
            Parent parent = existingParent.get();
            // Add student to parent's children if not already linked
            parentService.addChildToParent(parent.getId(), student.getId());
            log.info("Linked existing parent {} to student {}",
                    parentInfo.getEmail(), student.getStudentId());
            return parent;
        } else {
            // Create new parent
            User parentUser = new User();
            parentUser.setFirstName(parentInfo.getFirstName());
            parentUser.setLastName(parentInfo.getLastName());
            parentUser.setEmail(parentInfo.getEmail());
            parentUser.setPhone(parentInfo.getPhone());
            parentUser.setRoles(Set.of(User.Role.PARENT));
            parentUser.setActive(true);

            Parent newParent = new Parent();
            newParent.setUser(parentUser);
            newParent.setRelationship(parentInfo.getRelationship());
            newParent.setChildren(new java.util.ArrayList<>());

            Parent savedParent = parentService.createParent(newParent);

            // Link student to parent
            parentService.addChildToParent(savedParent.getId(), student.getId());

            log.info("Created new parent {} and linked to student {}",
                    parentInfo.getEmail(), student.getStudentId());
            return savedParent;
        }
    }

    public Student updateStudent(String id, Student student) {
        student.setId(id);
        return studentRepository.save(student);
    }

    public void deleteStudent(String id) {
        log.info("Deleting student with id: {}", id);
        studentRepository.deleteById(id);
    }

    public long getStudentCountByGrade(String grade) {
        return studentRepository.countByGrade(grade);
    }

    public long getStudentCountByStatus(String status) {
        return studentRepository.countByAcademicStatus(status);
    }

    public void updateStudentAIInsights(String studentId, Student.AIInsights insights) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            student.setAiInsights(insights);
            studentRepository.save(student);
            log.info("Updated AI insights for student: {}", studentId);
        }
    }

    // ====================
    // ACCESS CODE MANAGEMENT
    // ====================

    @Transactional
    public Map<String, Object> regenerateAccessCodesForStudent(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        List<AccessCode> regeneratedCodes = new java.util.ArrayList<>();

        try {
            // Regenerate student access code
            AccessCode studentCode = accessCodeService.regenerateStudentAccessCode(studentId);
            regeneratedCodes.add(studentCode);

            // Regenerate parent access codes
            List<AccessCode> parentCodes = accessCodeService.regenerateParentAccessCodes(studentId);
            regeneratedCodes.addAll(parentCodes);

            Map<String, Object> result = new HashMap<>();
            result.put("student", student);
            result.put("accessCodes", regeneratedCodes);
            result.put("message", "Access codes regenerated successfully");
            result.put("totalCodes", regeneratedCodes.size());

            log.info("Regenerated {} access codes for student {}",
                    regeneratedCodes.size(), student.getStudentId());

            return result;

        } catch (Exception e) {
            log.error("Error regenerating access codes for student {}: {}",
                     student.getStudentId(), e.getMessage());
            throw new RuntimeException("Failed to regenerate access codes: " + e.getMessage());
        }
    }

    public List<AccessCode> getAccessCodesForStudent(String studentId) {
        return accessCodeService.getAccessCodesByStudentId(studentId);
    }

    @Transactional
    public Map<String, Object> generateAccessCodesForExistingStudent(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        try {
            List<AccessCode> accessCodes = accessCodeService.generateStudentAndParentAccessCodes(studentId);

            Map<String, Object> result = new HashMap<>();
            result.put("student", student);
            result.put("accessCodes", accessCodes);
            result.put("message", "Access codes generated successfully");
            result.put("totalCodes", accessCodes.size());

            log.info("Generated {} access codes for existing student {}",
                    accessCodes.size(), student.getStudentId());

            return result;

        } catch (Exception e) {
            log.error("Error generating access codes for existing student {}: {}",
                     student.getStudentId(), e.getMessage());
            throw new RuntimeException("Failed to generate access codes: " + e.getMessage());
        }
    }
}
