package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.entity.*;
import com.eduai.schoolmanagement.service.MasterDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/master-data")
@RequiredArgsConstructor
@Tag(name = "Master Data Management", description = "Manage grade levels, sections, departments, employment types, and semesters")
@CrossOrigin(origins = "*")
public class MasterDataController {

    private final MasterDataService masterDataService;

    // ====================
    // GRADE LEVEL ENDPOINTS
    // ====================

    @GetMapping("/grade-levels")
    @Operation(summary = "Get all grade levels")
    public ResponseEntity<List<GradeLevel>> getAllGradeLevels() {
        List<GradeLevel> gradeLevels = masterDataService.getAllGradeLevels();
        return ResponseEntity.ok(gradeLevels);
    }

    @GetMapping("/grade-levels/{gradeCode}")
    @Operation(summary = "Get grade level by code")
    public ResponseEntity<GradeLevel> getGradeLevelByCode(@PathVariable String gradeCode) {
        Optional<GradeLevel> gradeLevel = masterDataService.getGradeLevelByCode(gradeCode);
        return gradeLevel.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/grade-levels")
    @Operation(summary = "Create new grade level")
    public ResponseEntity<GradeLevel> createGradeLevel(@Valid @RequestBody GradeLevel gradeLevel) {
        try {
            GradeLevel createdGradeLevel = masterDataService.createGradeLevel(gradeLevel);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGradeLevel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/grade-levels/{id}")
    @Operation(summary = "Update grade level")
    public ResponseEntity<GradeLevel> updateGradeLevel(
            @PathVariable String id,
            @Valid @RequestBody GradeLevel gradeLevelDetails) {
        try {
            GradeLevel updatedGradeLevel = masterDataService.updateGradeLevel(id, gradeLevelDetails);
            return ResponseEntity.ok(updatedGradeLevel);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/grade-levels/{id}")
    @Operation(summary = "Delete grade level")
    public ResponseEntity<Void> deleteGradeLevel(@PathVariable String id) {
        try {
            masterDataService.deleteGradeLevel(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ====================
    // SECTION ENDPOINTS
    // ====================

    @GetMapping("/sections")
    @Operation(summary = "Get all sections")
    public ResponseEntity<List<Section>> getAllSections() {
        List<Section> sections = masterDataService.getAllSections();
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/sections/{sectionCode}")
    @Operation(summary = "Get section by code")
    public ResponseEntity<Section> getSectionByCode(@PathVariable String sectionCode) {
        Optional<Section> section = masterDataService.getSectionByCode(sectionCode);
        return section.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sections")
    @Operation(summary = "Create new section")
    public ResponseEntity<Section> createSection(@Valid @RequestBody Section section) {
        try {
            Section createdSection = masterDataService.createSection(section);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSection);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/sections/{id}")
    @Operation(summary = "Update section")
    public ResponseEntity<Section> updateSection(
            @PathVariable String id,
            @Valid @RequestBody Section sectionDetails) {
        try {
            Section updatedSection = masterDataService.updateSection(id, sectionDetails);
            return ResponseEntity.ok(updatedSection);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/sections/{id}")
    @Operation(summary = "Delete section")
    public ResponseEntity<Void> deleteSection(@PathVariable String id) {
        try {
            masterDataService.deleteSection(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ====================
    // DEPARTMENT ENDPOINTS
    // ====================

    @GetMapping("/departments")
    @Operation(summary = "Get all departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = masterDataService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/departments/{departmentCode}")
    @Operation(summary = "Get department by code")
    public ResponseEntity<Department> getDepartmentByCode(@PathVariable String departmentCode) {
        Optional<Department> department = masterDataService.getDepartmentByCode(departmentCode);
        return department.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/departments")
    @Operation(summary = "Create new department")
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody Department department) {
        try {
            Department createdDepartment = masterDataService.createDepartment(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/departments/{id}")
    @Operation(summary = "Update department")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable String id,
            @Valid @RequestBody Department departmentDetails) {
        try {
            Department updatedDepartment = masterDataService.updateDepartment(id, departmentDetails);
            return ResponseEntity.ok(updatedDepartment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/departments/{id}")
    @Operation(summary = "Delete department")
    public ResponseEntity<Void> deleteDepartment(@PathVariable String id) {
        try {
            masterDataService.deleteDepartment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ====================
    // EMPLOYMENT TYPE ENDPOINTS
    // ====================

    @GetMapping("/employment-types")
    @Operation(summary = "Get all employment types")
    public ResponseEntity<List<EmploymentType>> getAllEmploymentTypes() {
        List<EmploymentType> employmentTypes = masterDataService.getAllEmploymentTypes();
        return ResponseEntity.ok(employmentTypes);
    }

    @GetMapping("/employment-types/{typeCode}")
    @Operation(summary = "Get employment type by code")
    public ResponseEntity<EmploymentType> getEmploymentTypeByCode(@PathVariable String typeCode) {
        Optional<EmploymentType> employmentType = masterDataService.getEmploymentTypeByCode(typeCode);
        return employmentType.map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/employment-types")
    @Operation(summary = "Create new employment type")
    public ResponseEntity<EmploymentType> createEmploymentType(@Valid @RequestBody EmploymentType employmentType) {
        try {
            EmploymentType createdEmploymentType = masterDataService.createEmploymentType(employmentType);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmploymentType);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/employment-types/{id}")
    @Operation(summary = "Update employment type")
    public ResponseEntity<EmploymentType> updateEmploymentType(
            @PathVariable String id,
            @Valid @RequestBody EmploymentType employmentTypeDetails) {
        try {
            EmploymentType updatedEmploymentType = masterDataService.updateEmploymentType(id, employmentTypeDetails);
            return ResponseEntity.ok(updatedEmploymentType);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/employment-types/{id}")
    @Operation(summary = "Delete employment type")
    public ResponseEntity<Void> deleteEmploymentType(@PathVariable String id) {
        try {
            masterDataService.deleteEmploymentType(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ====================
    // SEMESTER ENDPOINTS
    // ====================

    @GetMapping("/semesters")
    @Operation(summary = "Get all semesters")
    public ResponseEntity<List<Semester>> getAllSemesters() {
        List<Semester> semesters = masterDataService.getAllSemesters();
        return ResponseEntity.ok(semesters);
    }

    @GetMapping("/semesters/{semesterCode}")
    @Operation(summary = "Get semester by code")
    public ResponseEntity<Semester> getSemesterByCode(@PathVariable String semesterCode) {
        Optional<Semester> semester = masterDataService.getSemesterByCode(semesterCode);
        return semester.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/semesters")
    @Operation(summary = "Create new semester")
    public ResponseEntity<Semester> createSemester(@Valid @RequestBody Semester semester) {
        try {
            Semester createdSemester = masterDataService.createSemester(semester);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSemester);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/semesters/{id}")
    @Operation(summary = "Update semester")
    public ResponseEntity<Semester> updateSemester(
            @PathVariable String id,
            @Valid @RequestBody Semester semesterDetails) {
        try {
            Semester updatedSemester = masterDataService.updateSemester(id, semesterDetails);
            return ResponseEntity.ok(updatedSemester);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/semesters/{id}")
    @Operation(summary = "Delete semester")
    public ResponseEntity<Void> deleteSemester(@PathVariable String id) {
        try {
            masterDataService.deleteSemester(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ====================
    // INITIALIZATION ENDPOINTS
    // ====================

    @PostMapping("/initialize-defaults")
    @Operation(summary = "Initialize all default master data")
    public ResponseEntity<Object> initializeDefaults() {
        try {
            masterDataService.initializeDefaultMasterData();
            return ResponseEntity.ok(Map.of("message", "Default master data initialized successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/grade-levels/initialize-defaults")
    @Operation(summary = "Initialize default grade levels")
    public ResponseEntity<Object> initializeDefaultGradeLevels() {
        try {
            masterDataService.initializeDefaultMasterData(); // This will only initialize if empty
            return ResponseEntity.ok(Map.of("message", "Default grade levels initialized successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/sections/initialize-defaults")
    @Operation(summary = "Initialize default sections")
    public ResponseEntity<Object> initializeDefaultSections() {
        try {
            masterDataService.initializeDefaultMasterData(); // This will only initialize if empty
            return ResponseEntity.ok(Map.of("message", "Default sections initialized successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/departments/initialize-defaults")
    @Operation(summary = "Initialize default departments")
    public ResponseEntity<Object> initializeDefaultDepartments() {
        try {
            masterDataService.initializeDefaultMasterData(); // This will only initialize if empty
            return ResponseEntity.ok(Map.of("message", "Default departments initialized successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/employment-types/initialize-defaults")
    @Operation(summary = "Initialize default employment types")
    public ResponseEntity<Object> initializeDefaultEmploymentTypes() {
        try {
            masterDataService.initializeDefaultMasterData(); // This will only initialize if empty
            return ResponseEntity.ok(Map.of("message", "Default employment types initialized successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/semesters/initialize-defaults")
    @Operation(summary = "Initialize default semesters")
    public ResponseEntity<Object> initializeDefaultSemesters() {
        try {
            masterDataService.initializeDefaultMasterData(); // This will only initialize if empty
            return ResponseEntity.ok(Map.of("message", "Default semesters initialized successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
