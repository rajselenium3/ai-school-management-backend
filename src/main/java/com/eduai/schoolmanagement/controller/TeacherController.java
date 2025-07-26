package com.eduai.schoolmanagement.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eduai.schoolmanagement.entity.Teacher;
import com.eduai.schoolmanagement.service.TeacherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
@Tag(name = "Teacher Management", description = "Teacher management operations")
@CrossOrigin(origins = "*")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    @Operation(summary = "Get all teachers")
    public ResponseEntity<List<Teacher>> getAllTeachers() {
        List<Teacher> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get teacher by ID")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable String id) {
        Optional<Teacher> teacher = teacherService.getTeacherById(id);
        return teacher.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee-id/{employeeId}")
    @Operation(summary = "Get teacher by employee ID")
    public ResponseEntity<Teacher> getTeacherByEmployeeId(@PathVariable String employeeId) {
        Optional<Teacher> teacher = teacherService.getTeacherByEmployeeId(employeeId);
        return teacher.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get teacher by email")
    public ResponseEntity<Teacher> getTeacherByEmail(@PathVariable String email) {
        Optional<Teacher> teacher = teacherService.getTeacherByEmail(email);
        return teacher.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get teachers by department")
    public ResponseEntity<List<Teacher>> getTeachersByDepartment(@PathVariable String department) {
        List<Teacher> teachers = teacherService.getTeachersByDepartment(department);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/employment-type/{employmentType}")
    @Operation(summary = "Get teachers by employment type")
    public ResponseEntity<List<Teacher>> getTeachersByEmploymentType(@PathVariable String employmentType) {
        List<Teacher> teachers = teacherService.getTeachersByEmploymentType(employmentType);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/subject/{subject}")
    @Operation(summary = "Get teachers by subject")
    public ResponseEntity<List<Teacher>> getTeachersBySubject(@PathVariable String subject) {
        List<Teacher> teachers = teacherService.getTeachersBySubject(subject);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/search")
    @Operation(summary = "Search teachers by name")
    public ResponseEntity<Page<Teacher>> searchTeachersByName(
            @RequestParam String name, Pageable pageable) {
        Page<Teacher> teachers = teacherService.searchTeachersByName(name, pageable);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/high-performing")
    @Operation(summary = "Get high performing teachers")
    public ResponseEntity<List<Teacher>> getHighPerformingTeachers(
            @RequestParam(defaultValue = "85.0") double performanceThreshold) {
        List<Teacher> teachers = teacherService.getHighPerformingTeachers(performanceThreshold);
        return ResponseEntity.ok(teachers);
    }

    @PostMapping
    @Operation(summary = "Create new teacher")
    public ResponseEntity<Teacher> createTeacher(@Valid @RequestBody Teacher teacher) {
        try {
            Teacher savedTeacher = teacherService.createTeacher(teacher);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTeacher);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update teacher")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable String id, @Valid @RequestBody Teacher teacher) {
        Teacher updatedTeacher = teacherService.updateTeacher(id, teacher);
        return ResponseEntity.ok(updatedTeacher);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete teacher")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/department/{department}")
    @Operation(summary = "Get teacher count by department")
    public ResponseEntity<Long> getTeacherCountByDepartment(@PathVariable String department) {
        long count = teacherService.getTeacherCountByDepartment(department);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/employment-type/{employmentType}")
    @Operation(summary = "Get teacher count by employment type")
    public ResponseEntity<Long> getTeacherCountByEmploymentType(@PathVariable String employmentType) {
        long count = teacherService.getTeacherCountByEmploymentType(employmentType);
        return ResponseEntity.ok(count);
    }
}
