package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Parent;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.User;
import com.eduai.schoolmanagement.repository.ParentRepository;
import com.eduai.schoolmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParentService {

    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;

    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    public List<Parent> getAllActiveParents() {
        return parentRepository.findByIsActiveTrue();
    }

    public Optional<Parent> getParentById(String id) {
        return parentRepository.findById(id);
    }

    public Optional<Parent> getParentByParentId(String parentId) {
        return parentRepository.findByParentId(parentId);
    }

    public Optional<Parent> getParentByUser(User user) {
        return parentRepository.findByUser(user);
    }

    public Optional<Parent> getParentByEmail(String email) {
        return parentRepository.findByUserEmail(email);
    }

    public Optional<Parent> getActiveParentByEmail(String email) {
        return parentRepository.findActiveParentByEmail(email);
    }

    public List<Parent> getParentsByStudent(Student student) {
        return parentRepository.findByChildrenContaining(student);
    }

    public List<Parent> getParentsByStudentId(String studentId) {
        return parentRepository.findParentsByChildStudentId(studentId);
    }

    public Optional<Parent> getPrimaryParentByChildId(String childId) {
        return parentRepository.findPrimaryParentByChildId(childId);
    }

    public Parent saveParent(Parent parent) {
        log.info("Saving parent: {}", parent.getParentId());
        return parentRepository.save(parent);
    }

    public Parent createParent(Parent parent) {
        log.info("Creating new parent: {}", parent.getUser().getEmail());

        // Generate parent ID if not provided
        if (parent.getParentId() == null || parent.getParentId().isEmpty()) {
            parent.setParentId(generateParentId());
        }

        // Set default values
        if (parent.getIsActive() == null) {
            parent.setIsActive(true);
        }

        return parentRepository.save(parent);
    }

    public Parent updateParent(String id, Parent parentDetails) {
        Optional<Parent> parentOpt = parentRepository.findById(id);
        if (parentOpt.isPresent()) {
            Parent parent = parentOpt.get();

            // Update fields
            parent.setRelationship(parentDetails.getRelationship());
            parent.setOccupation(parentDetails.getOccupation());
            parent.setWorkPhone(parentDetails.getWorkPhone());
            parent.setEmergencyContact(parentDetails.getEmergencyContact());
            parent.setEmergencyContactName(parentDetails.getEmergencyContactName());
            parent.setEmergencyContactRelationship(parentDetails.getEmergencyContactRelationship());
            parent.setIsPrimary(parentDetails.getIsPrimary());
            parent.setIsActive(parentDetails.getIsActive());

            // Update preferences
            parent.setCanViewFinancials(parentDetails.getCanViewFinancials());
            parent.setCanMakePayments(parentDetails.getCanMakePayments());
            parent.setReceiveBillingNotifications(parentDetails.getReceiveBillingNotifications());
            parent.setReceiveEmailNotifications(parentDetails.getReceiveEmailNotifications());
            parent.setReceiveSmsNotifications(parentDetails.getReceiveSmsNotifications());
            parent.setReceiveProgressReports(parentDetails.getReceiveProgressReports());
            parent.setReceiveAttendanceAlerts(parentDetails.getReceiveAttendanceAlerts());

            log.info("Updated parent: {}", parent.getParentId());
            return parentRepository.save(parent);
        }
        throw new RuntimeException("Parent not found with id: " + id);
    }

    public void deleteParent(String id) {
        log.info("Deleting parent with id: {}", id);
        parentRepository.deleteById(id);
    }

    public void deactivateParent(String id) {
        Optional<Parent> parentOpt = parentRepository.findById(id);
        if (parentOpt.isPresent()) {
            Parent parent = parentOpt.get();
            parent.setIsActive(false);
            parentRepository.save(parent);
            log.info("Deactivated parent: {}", parent.getParentId());
        }
    }

    public void addChildToParent(String parentId, Student student) {
        Optional<Parent> parentOpt = parentRepository.findByParentId(parentId);
        if (parentOpt.isPresent()) {
            Parent parent = parentOpt.get();
            parent.addChild(student);
            parentRepository.save(parent);
            log.info("Added child {} to parent {}", student.getStudentId(), parent.getParentId());
        } else {
            throw new RuntimeException("Parent not found with id: " + parentId);
        }
    }

    public void addChildToParent(String parentId, String studentId) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isPresent()) {
            addChildToParent(parentId, studentOpt.get());
        } else {
            throw new RuntimeException("Student not found with id: " + studentId);
        }
    }

    public void removeChildFromParent(String parentId, Student student) {
        Optional<Parent> parentOpt = parentRepository.findByParentId(parentId);
        if (parentOpt.isPresent()) {
            Parent parent = parentOpt.get();
            parent.removeChild(student);
            parentRepository.save(parent);
            log.info("Removed child {} from parent {}", student.getStudentId(), parent.getParentId());
        } else {
            throw new RuntimeException("Parent not found with id: " + parentId);
        }
    }

    public List<Student> getChildrenByParentEmail(String parentEmail) {
        Optional<Parent> parentOpt = getActiveParentByEmail(parentEmail);
        if (parentOpt.isPresent()) {
            return parentOpt.get().getChildren();
        }
        return List.of();
    }

    public Object getParentStatistics() {
        long totalParents = parentRepository.count();
        long activeParents = parentRepository.countByIsActiveTrue();

        return java.util.Map.of(
            "totalParents", totalParents,
            "activeParents", activeParents,
            "inactiveParents", totalParents - activeParents
        );
    }

    private String generateParentId() {
        long count = parentRepository.count();
        return String.format("PAR%05d", count + 1);
    }
}
