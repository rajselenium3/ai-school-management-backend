package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.entity.Parent;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.service.ParentService;
import com.eduai.schoolmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/parents")
@RequiredArgsConstructor
@Tag(name = "Parent Management", description = "Parent management and parent-child relationship operations")
@CrossOrigin(origins = "*")
public class ParentController {

    private final ParentService parentService;
    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Get all parents")
    public ResponseEntity<List<Parent>> getAllParents() {
        List<Parent> parents = parentService.getAllParents();
        return ResponseEntity.ok(parents);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active parents")
    public ResponseEntity<List<Parent>> getAllActiveParents() {
        List<Parent> parents = parentService.getAllActiveParents();
        return ResponseEntity.ok(parents);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parent by ID")
    public ResponseEntity<Parent> getParentById(@PathVariable String id) {
        Optional<Parent> parent = parentService.getParentById(id);
        return parent.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/parent-id/{parentId}")
    @Operation(summary = "Get parent by parent ID")
    public ResponseEntity<Parent> getParentByParentId(@PathVariable String parentId) {
        Optional<Parent> parent = parentService.getParentByParentId(parentId);
        return parent.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get parent by email")
    public ResponseEntity<Parent> getParentByEmail(@PathVariable String email) {
        Optional<Parent> parent = parentService.getActiveParentByEmail(email);
        return parent.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get parents by student ID")
    public ResponseEntity<List<Parent>> getParentsByStudentId(@PathVariable String studentId) {
        List<Parent> parents = parentService.getParentsByStudentId(studentId);
        return ResponseEntity.ok(parents);
    }

    @GetMapping("/student/{studentId}/primary")
    @Operation(summary = "Get primary parent by student ID")
    public ResponseEntity<Parent> getPrimaryParentByStudentId(@PathVariable String studentId) {
        // First get the student to get the child ID
        Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
        if (studentOpt.isPresent()) {
            Optional<Parent> parent = parentService.getPrimaryParentByChildId(studentOpt.get().getId());
            return parent.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{parentEmail}/children")
    @Operation(summary = "Get children by parent email")
    public ResponseEntity<List<Student>> getChildrenByParentEmail(@PathVariable String parentEmail) {
        List<Student> children = parentService.getChildrenByParentEmail(parentEmail);
        return ResponseEntity.ok(children);
    }

    @PostMapping
    @Operation(summary = "Create new parent")
    public ResponseEntity<Parent> createParent(@Valid @RequestBody Parent parent) {
        try {
            Parent savedParent = parentService.createParent(parent);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedParent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update parent")
    public ResponseEntity<Parent> updateParent(@PathVariable String id, @Valid @RequestBody Parent parentDetails) {
        try {
            Parent updatedParent = parentService.updateParent(id, parentDetails);
            return ResponseEntity.ok(updatedParent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete parent")
    public ResponseEntity<Void> deleteParent(@PathVariable String id) {
        parentService.deleteParent(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate parent")
    public ResponseEntity<Void> deactivateParent(@PathVariable String id) {
        parentService.deactivateParent(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{parentId}/children/{studentId}")
    @Operation(summary = "Add child to parent")
    public ResponseEntity<Void> addChildToParent(@PathVariable String parentId, @PathVariable String studentId) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            if (studentOpt.isPresent()) {
                parentService.addChildToParent(parentId, studentOpt.get());
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{parentId}/children/{studentId}")
    @Operation(summary = "Remove child from parent")
    public ResponseEntity<Void> removeChildFromParent(@PathVariable String parentId, @PathVariable String studentId) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            if (studentOpt.isPresent()) {
                parentService.removeChildFromParent(parentId, studentOpt.get());
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get parent statistics")
    public ResponseEntity<Object> getParentStatistics() {
        Object statistics = parentService.getParentStatistics();
        return ResponseEntity.ok(statistics);
    }

    @PostMapping("/bulk-create-mappings")
    @Operation(summary = "Bulk create parent-child mappings")
    public ResponseEntity<Object> bulkCreateParentChildMappings(@RequestBody List<Object> mappings) {
        // This endpoint allows bulk creation of parent-child relationships
        // Expected format: [{"parentEmail": "email", "childId": "studentId", "relationship": "Father", "isPrimary": true}]

        int successCount = 0;
        int errorCount = 0;

        for (Object mappingObj : mappings) {
            try {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> mapping = (java.util.Map<String, Object>) mappingObj;

                String parentEmail = (String) mapping.get("parentEmail");
                String childId = (String) mapping.get("childId");
                String relationship = (String) mapping.get("relationship");
                Boolean isPrimary = (Boolean) mapping.getOrDefault("isPrimary", false);

                // Find or create parent
                Optional<Parent> parentOpt = parentService.getActiveParentByEmail(parentEmail);
                Parent parent;

                if (parentOpt.isPresent()) {
                    parent = parentOpt.get();
                } else {
                    // Create new parent (requires user to be created first)
                    continue; // Skip if user doesn't exist
                }

                // Find student
                Optional<Student> studentOpt = studentService.getStudentByStudentId(childId);
                if (studentOpt.isPresent()) {
                    parent.addChild(studentOpt.get());
                    parent.setIsPrimary(isPrimary);
                    if (relationship != null) {
                        parent.setRelationship(relationship);
                    }
                    parentService.saveParent(parent);
                    successCount++;
                } else {
                    errorCount++;
                }

            } catch (Exception e) {
                errorCount++;
            }
        }

        return ResponseEntity.ok(java.util.Map.of(
            "successCount", successCount,
            "errorCount", errorCount,
            "totalProcessed", mappings.size()
        ));
    }
}
