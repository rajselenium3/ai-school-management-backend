package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.AccessCode;
import com.eduai.schoolmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "Student management operations")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Get all students")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<Student> getStudentById(@PathVariable String id) {
        Optional<Student> student = studentService.getStudentById(id);
        return student.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student-id/{studentId}")
    @Operation(summary = "Get student by student ID")
    public ResponseEntity<Student> getStudentByStudentId(@PathVariable String studentId) {
        Optional<Student> student = studentService.getStudentByStudentId(studentId);
        return student.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get student by email")
    public ResponseEntity<Student> getStudentByEmail(@PathVariable String email) {
        Optional<Student> student = studentService.getStudentByEmail(email);
        return student.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/grade/{grade}")
    @Operation(summary = "Get students by grade")
    public ResponseEntity<List<Student>> getStudentsByGrade(@PathVariable String grade) {
        List<Student> students = studentService.getStudentsByGrade(grade);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/grade/{grade}/section/{section}")
    @Operation(summary = "Get students by grade and section")
    public ResponseEntity<List<Student>> getStudentsByGradeAndSection(
            @PathVariable String grade, @PathVariable String section) {
        List<Student> students = studentService.getStudentsByGradeAndSection(grade, section);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    @Operation(summary = "Search students by name")
    public ResponseEntity<Page<Student>> searchStudentsByName(
            @RequestParam String name, Pageable pageable) {
        Page<Student> students = studentService.searchStudentsByName(name, pageable);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/at-risk")
    @Operation(summary = "Get at-risk students")
    public ResponseEntity<List<Student>> getAtRiskStudents(
            @RequestParam(defaultValue = "70.0") double riskThreshold) {
        List<Student> students = studentService.getAtRiskStudents(riskThreshold);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/performance-trend/{trend}")
    @Operation(summary = "Get students by performance trend")
    public ResponseEntity<List<Student>> getStudentsByPerformanceTrend(@PathVariable String trend) {
        List<Student> students = studentService.getStudentsByPerformanceTrend(trend);
        return ResponseEntity.ok(students);
    }

    @PostMapping
    @Operation(summary = "Create new student")
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student savedStudent = studentService.saveStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    @PostMapping("/with-access-codes")
    @Operation(summary = "Create new student with access codes and parent mapping")
    public ResponseEntity<Object> createStudentWithAccessCodes(@Valid @RequestBody Student student) {
        try {
            Map<String, Object> result = studentService.saveStudentWithAccessCodes(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student")
    public ResponseEntity<Student> updateStudent(@PathVariable String id, @Valid @RequestBody Student student) {
        Student updatedStudent = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{studentId}/ai-insights")
    @Operation(summary = "Update student AI insights")
    public ResponseEntity<Void> updateStudentAIInsights(
            @PathVariable String studentId, @RequestBody Student.AIInsights insights) {
        studentService.updateStudentAIInsights(studentId, insights);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count/grade/{grade}")
    @Operation(summary = "Get student count by grade")
    public ResponseEntity<Long> getStudentCountByGrade(@PathVariable String grade) {
        long count = studentService.getStudentCountByGrade(grade);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count/status/{status}")
    @Operation(summary = "Get student count by status")
    public ResponseEntity<Long> getStudentCountByStatus(@PathVariable String status) {
        long count = studentService.getStudentCountByStatus(status);
        return ResponseEntity.ok(count);
    }

    // ====================
    // ACCESS CODE MANAGEMENT ENDPOINTS
    // ====================

    @GetMapping("/{studentId}/access-codes")
    @Operation(summary = "Get access codes for a student")
    public ResponseEntity<List<AccessCode>> getAccessCodesForStudent(@PathVariable String studentId) {
        List<AccessCode> accessCodes = studentService.getAccessCodesForStudent(studentId);
        return ResponseEntity.ok(accessCodes);
    }

    @PostMapping("/{studentId}/access-codes/generate")
    @Operation(summary = "Generate access codes for existing student")
    public ResponseEntity<Object> generateAccessCodesForStudent(@PathVariable String studentId) {
        try {
            Map<String, Object> result = studentService.generateAccessCodesForExistingStudent(studentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{studentId}/access-codes/regenerate")
    @Operation(summary = "Regenerate access codes for a student")
    public ResponseEntity<Object> regenerateAccessCodesForStudent(@PathVariable String studentId) {
        try {
            Map<String, Object> result = studentService.regenerateAccessCodesForStudent(studentId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
