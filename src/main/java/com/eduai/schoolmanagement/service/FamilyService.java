package com.eduai.schoolmanagement.service;

import com.eduai.schoolmanagement.entity.Family;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.repository.FamilyRepository;
import com.eduai.schoolmanagement.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final StudentRepository studentRepository;

    // ====================
    // FAMILY MANAGEMENT
    // ====================

    public List<Map<String, Object>> getAllFamilies() {
        List<Family> families = familyRepository.findAll();
        return families.stream()
            .map(this::convertFamilyToMap)
            .collect(Collectors.toList());
    }

    public Map<String, Object> getFamilyById(String id) {
        Optional<Family> familyOpt = familyRepository.findById(id);
        if (familyOpt.isPresent()) {
            return convertFamilyToMap(familyOpt.get());
        }
        throw new RuntimeException("Family not found with ID: " + id);
    }

    public Optional<Family> getFamilyByName(String familyName) {
        return familyRepository.findByFamilyName(familyName);
    }

    public Optional<Family> getFamilyByStudentId(String studentId) {
        return familyRepository.findByStudentIdsContaining(studentId);
    }

    @Transactional
    public Map<String, Object> createFamily(Map<String, Object> familyData) {
        Family family = new Family();
        family.setFamilyName((String) familyData.get("familyName"));
        family.setAddress((String) familyData.get("address"));

        // Set primary contact
        Map<String, Object> primaryContactData = (Map<String, Object>) familyData.get("primaryContact");
        if (primaryContactData != null) {
            Family.ContactInfo primaryContact = new Family.ContactInfo();
            primaryContact.setName((String) primaryContactData.get("name"));
            primaryContact.setRelationship((String) primaryContactData.get("relationship"));
            primaryContact.setEmail((String) primaryContactData.get("email"));
            primaryContact.setPhone((String) primaryContactData.get("phone"));
            family.setPrimaryContact(primaryContact);
        }

        // Set secondary contact
        Map<String, Object> secondaryContactData = (Map<String, Object>) familyData.get("secondaryContact");
        if (secondaryContactData != null) {
            Family.ContactInfo secondaryContact = new Family.ContactInfo();
            secondaryContact.setName((String) secondaryContactData.get("name"));
            secondaryContact.setRelationship((String) secondaryContactData.get("relationship"));
            secondaryContact.setEmail((String) secondaryContactData.get("email"));
            secondaryContact.setPhone((String) secondaryContactData.get("phone"));
            family.setSecondaryContact(secondaryContact);
        }

        // Set emergency contacts
        List<Map<String, Object>> emergencyContactsData = (List<Map<String, Object>>) familyData.get("emergencyContacts");
        if (emergencyContactsData != null) {
            List<Family.EmergencyContact> emergencyContacts = emergencyContactsData.stream()
                .map(contactData -> {
                    Family.EmergencyContact contact = new Family.EmergencyContact();
                    contact.setName((String) contactData.get("name"));
                    contact.setRelationship((String) contactData.get("relationship"));
                    contact.setPhone((String) contactData.get("phone"));
                    return contact;
                })
                .collect(Collectors.toList());
            family.setEmergencyContacts(emergencyContacts);
        }

        // Set student IDs
        List<String> studentIds = (List<String>) familyData.getOrDefault("studentIds", new ArrayList<>());
        family.setStudentIds(studentIds);

        Family savedFamily = familyRepository.save(family);
        log.info("Created family: {}", savedFamily.getFamilyName());

        return convertFamilyToMap(savedFamily);
    }

    @Transactional
    public Map<String, Object> updateFamily(String id, Map<String, Object> familyData) {
        Family family = familyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Family not found with ID: " + id));

        // Update basic info
        if (familyData.containsKey("familyName")) {
            family.setFamilyName((String) familyData.get("familyName"));
        }
        if (familyData.containsKey("address")) {
            family.setAddress((String) familyData.get("address"));
        }

        // Update contacts if provided
        if (familyData.containsKey("primaryContact")) {
            Map<String, Object> primaryContactData = (Map<String, Object>) familyData.get("primaryContact");
            Family.ContactInfo primaryContact = new Family.ContactInfo();
            primaryContact.setName((String) primaryContactData.get("name"));
            primaryContact.setRelationship((String) primaryContactData.get("relationship"));
            primaryContact.setEmail((String) primaryContactData.get("email"));
            primaryContact.setPhone((String) primaryContactData.get("phone"));
            family.setPrimaryContact(primaryContact);
        }

        Family savedFamily = familyRepository.save(family);
        log.info("Updated family: {}", savedFamily.getFamilyName());

        return convertFamilyToMap(savedFamily);
    }

    @Transactional
    public void addStudentToFamily(String familyId, String studentId) {
        Family family = familyRepository.findById(familyId)
            .orElseThrow(() -> new RuntimeException("Family not found with ID: " + familyId));

        if (family.getStudentIds() == null) {
            family.setStudentIds(new ArrayList<>());
        }

        if (!family.getStudentIds().contains(studentId)) {
            family.getStudentIds().add(studentId);
            familyRepository.save(family);

            // Update student's family ID
            Optional<Student> studentOpt = studentRepository.findById(studentId);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                student.setFamilyId(familyId);
                studentRepository.save(student);
            }

            log.info("Added student {} to family {}", studentId, family.getFamilyName());
        }
    }

    @Transactional
    public void removeStudentFromFamily(String familyId, String studentId) {
        Family family = familyRepository.findById(familyId)
            .orElseThrow(() -> new RuntimeException("Family not found with ID: " + familyId));

        if (family.getStudentIds() != null) {
            family.getStudentIds().remove(studentId);
            familyRepository.save(family);

            // Clear student's family ID
            Optional<Student> studentOpt = studentRepository.findById(studentId);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                student.setFamilyId(null);
                studentRepository.save(student);
            }

            log.info("Removed student {} from family {}", studentId, family.getFamilyName());
        }
    }

    public void deleteFamily(String id) {
        Family family = familyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Family not found with ID: " + id));

        // Clear family ID from all students
        if (family.getStudentIds() != null) {
            for (String studentId : family.getStudentIds()) {
                Optional<Student> studentOpt = studentRepository.findById(studentId);
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    student.setFamilyId(null);
                    studentRepository.save(student);
                }
            }
        }

        familyRepository.delete(family);
        log.info("Deleted family: {}", family.getFamilyName());
    }

    // ====================
    // SEARCH AND FILTERING
    // ====================

    public List<Map<String, Object>> searchFamiliesByName(String name) {
        List<Family> families = familyRepository.findByFamilyNameContainingIgnoreCase(name);
        return families.stream()
            .map(this::convertFamilyToMap)
            .collect(Collectors.toList());
    }

    public List<Map<String, Object>> searchFamiliesByContactName(String contactName) {
        List<Family> families = familyRepository.findByPrimaryContactNameContainingIgnoreCaseOrSecondaryContactNameContainingIgnoreCase(contactName);
        return families.stream()
            .map(this::convertFamilyToMap)
            .collect(Collectors.toList());
    }

    public Map<String, Object> getFamilyByContactEmail(String email) {
        List<Family> families = familyRepository.findByPrimaryContactEmailOrSecondaryContactEmail(email);
        if (!families.isEmpty()) {
            return convertFamilyToMap(families.get(0));
        }
        return null;
    }

    // ====================
    // ANALYTICS
    // ====================

    public Map<String, Object> getFamilyAnalytics() {
        List<Family> allFamilies = familyRepository.findAll();

        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalFamilies", allFamilies.size());
        analytics.put("familiesWithMultipleChildren", allFamilies.stream()
            .filter(f -> f.getStudentIds() != null && f.getStudentIds().size() > 1)
            .count());
        analytics.put("averageChildrenPerFamily", allFamilies.stream()
            .filter(f -> f.getStudentIds() != null)
            .mapToInt(f -> f.getStudentIds().size())
            .average()
            .orElse(0.0));
        analytics.put("totalStudentsWithFamilies", familyRepository.countByStudentIdsNotEmpty());

        return analytics;
    }

    // ====================
    // HELPER METHODS
    // ====================

    private Map<String, Object> convertFamilyToMap(Family family) {
        Map<String, Object> familyMap = new HashMap<>();
        familyMap.put("id", family.getId());
        familyMap.put("familyName", family.getFamilyName());
        familyMap.put("address", family.getAddress());
        familyMap.put("createdAt", family.getCreatedAt());
        familyMap.put("updatedAt", family.getUpdatedAt());

        // Convert primary contact
        if (family.getPrimaryContact() != null) {
            Map<String, Object> primaryContact = new HashMap<>();
            primaryContact.put("name", family.getPrimaryContact().getName());
            primaryContact.put("relationship", family.getPrimaryContact().getRelationship());
            primaryContact.put("email", family.getPrimaryContact().getEmail());
            primaryContact.put("phone", family.getPrimaryContact().getPhone());
            familyMap.put("primaryContact", primaryContact);
        }

        // Convert secondary contact
        if (family.getSecondaryContact() != null) {
            Map<String, Object> secondaryContact = new HashMap<>();
            secondaryContact.put("name", family.getSecondaryContact().getName());
            secondaryContact.put("relationship", family.getSecondaryContact().getRelationship());
            secondaryContact.put("email", family.getSecondaryContact().getEmail());
            secondaryContact.put("phone", family.getSecondaryContact().getPhone());
            familyMap.put("secondaryContact", secondaryContact);
        }

        // Convert emergency contacts
        if (family.getEmergencyContacts() != null) {
            List<Map<String, Object>> emergencyContacts = family.getEmergencyContacts().stream()
                .map(contact -> {
                    Map<String, Object> contactMap = new HashMap<>();
                    contactMap.put("name", contact.getName());
                    contactMap.put("relationship", contact.getRelationship());
                    contactMap.put("phone", contact.getPhone());
                    return contactMap;
                })
                .collect(Collectors.toList());
            familyMap.put("emergencyContacts", emergencyContacts);
        }

        // Get student names for student IDs
        if (family.getStudentIds() != null) {
            List<String> studentNames = family.getStudentIds().stream()
                .map(studentId -> {
                    Optional<Student> studentOpt = studentRepository.findById(studentId);
                    if (studentOpt.isPresent()) {
                        Student student = studentOpt.get();
                        return student.getUser().getFirstName() + " " + student.getUser().getLastName();
                    }
                    return "Unknown Student";
                })
                .collect(Collectors.toList());
            familyMap.put("students", studentNames);
            familyMap.put("studentIds", family.getStudentIds());
        }

        return familyMap;
    }
}
