package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.service.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/families")
@RequiredArgsConstructor
@Tag(name = "Family Management", description = "Family management operations for students")
@CrossOrigin(origins = "*")
public class FamilyController {

    private final FamilyService familyService;

    // ====================
    // BASIC CRUD OPERATIONS
    // ====================

    @GetMapping
    @Operation(summary = "Get all families")
    public ResponseEntity<List<Map<String, Object>>> getAllFamilies() {
        List<Map<String, Object>> families = familyService.getAllFamilies();
        return ResponseEntity.ok(families);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get family by ID")
    public ResponseEntity<Map<String, Object>> getFamilyById(@PathVariable String id) {
        Map<String, Object> family = familyService.getFamilyById(id);
        return ResponseEntity.ok(family);
    }

    @PostMapping
    @Operation(summary = "Create new family")
    public ResponseEntity<Map<String, Object>> createFamily(@RequestBody Map<String, Object> familyData) {
        Map<String, Object> createdFamily = familyService.createFamily(familyData);
        return ResponseEntity.ok(createdFamily);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update family")
    public ResponseEntity<Map<String, Object>> updateFamily(
            @PathVariable String id,
            @RequestBody Map<String, Object> familyData) {
        Map<String, Object> updatedFamily = familyService.updateFamily(id, familyData);
        return ResponseEntity.ok(updatedFamily);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete family")
    public ResponseEntity<Void> deleteFamily(@PathVariable String id) {
        familyService.deleteFamily(id);
        return ResponseEntity.ok().build();
    }

    // ====================
    // FAMILY-STUDENT RELATIONSHIPS
    // ====================

    @PostMapping("/{familyId}/students/{studentId}")
    @Operation(summary = "Add student to family")
    public ResponseEntity<Void> addStudentToFamily(
            @PathVariable String familyId,
            @PathVariable String studentId) {
        familyService.addStudentToFamily(familyId, studentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{familyId}/students/{studentId}")
    @Operation(summary = "Remove student from family")
    public ResponseEntity<Void> removeStudentFromFamily(
            @PathVariable String familyId,
            @PathVariable String studentId) {
        familyService.removeStudentFromFamily(familyId, studentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get family by student ID")
    public ResponseEntity<Map<String, Object>> getFamilyByStudentId(@PathVariable String studentId) {
        var family = familyService.getFamilyByStudentId(studentId);
        if (family.isPresent()) {
            return ResponseEntity.ok(familyService.getFamilyById(family.get().getId()));
        }
        return ResponseEntity.notFound().build();
    }

    // ====================
    // SEARCH AND FILTERING
    // ====================

    @GetMapping("/search/name")
    @Operation(summary = "Search families by name")
    public ResponseEntity<List<Map<String, Object>>> searchFamiliesByName(@RequestParam String name) {
        List<Map<String, Object>> families = familyService.searchFamiliesByName(name);
        return ResponseEntity.ok(families);
    }

    @GetMapping("/search/contact")
    @Operation(summary = "Search families by contact name")
    public ResponseEntity<List<Map<String, Object>>> searchFamiliesByContactName(@RequestParam String contactName) {
        List<Map<String, Object>> families = familyService.searchFamiliesByContactName(contactName);
        return ResponseEntity.ok(families);
    }

    @GetMapping("/contact/email/{email}")
    @Operation(summary = "Get family by contact email")
    public ResponseEntity<Map<String, Object>> getFamilyByContactEmail(@PathVariable String email) {
        Map<String, Object> family = familyService.getFamilyByContactEmail(email);
        if (family != null) {
            return ResponseEntity.ok(family);
        }
        return ResponseEntity.notFound().build();
    }

    // ====================
    // ANALYTICS
    // ====================

    @GetMapping("/analytics")
    @Operation(summary = "Get family analytics")
    public ResponseEntity<Map<String, Object>> getFamilyAnalytics() {
        Map<String, Object> analytics = familyService.getFamilyAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
