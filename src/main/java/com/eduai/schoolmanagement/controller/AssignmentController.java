package com.eduai.schoolmanagement.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
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

import com.eduai.schoolmanagement.entity.Assignment;
import com.eduai.schoolmanagement.entity.Course;
import com.eduai.schoolmanagement.service.AssignmentService;
import com.eduai.schoolmanagement.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
@Tag(name = "Assignment Management", description = "Assignment creation, distribution, and management operations")
@CrossOrigin(origins = "*")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "Get all assignments")
    public ResponseEntity<List<Assignment>> getAllAssignments() {
        List<Assignment> assignments = assignmentService.getAllAssignments();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get assignment by ID")
    public ResponseEntity<Assignment> getAssignmentById(@PathVariable String id) {
        Optional<Assignment> assignment = assignmentService.getAssignmentById(id);
        return assignment.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/course/{courseCode}")
    @Operation(summary = "Get assignments by course code")
    public ResponseEntity<List<Assignment>> getAssignmentsByCourseCode(@PathVariable String courseCode) {
        List<Assignment> assignments = assignmentService.getAssignmentsByCourseCode(courseCode);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Get assignments by teacher ID")
    public ResponseEntity<List<Assignment>> getAssignmentsByTeacher(@PathVariable String teacherId) {
        List<Assignment> assignments = assignmentService.getAssignmentsByTeacher(teacherId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get assignments by type")
    public ResponseEntity<List<Assignment>> getAssignmentsByType(@PathVariable String type) {
        List<Assignment> assignments = assignmentService.getAssignmentsByType(type);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/search")
    @Operation(summary = "Search assignments by title")
    public ResponseEntity<List<Assignment>> searchAssignments(@RequestParam String searchTerm) {
        List<Assignment> assignments = assignmentService.searchAssignments(searchTerm);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/due-soon")
    @Operation(summary = "Get assignments due soon")
    public ResponseEntity<List<Assignment>> getAssignmentsDueSoon(
            @RequestParam(defaultValue = "7") int days) {
        List<Assignment> assignments = assignmentService.getAssignmentsDueSoon(days);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue assignments")
    public ResponseEntity<List<Assignment>> getOverdueAssignments() {
        List<Assignment> assignments = assignmentService.getOverdueAssignments();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/pending-grading")
    @Operation(summary = "Get assignments pending grading")
    public ResponseEntity<List<Assignment>> getAssignmentsPendingGrading() {
        List<Assignment> assignments = assignmentService.getAssignmentsPendingGrading();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/ai-grading-enabled")
    @Operation(summary = "Get assignments with AI grading enabled")
    public ResponseEntity<List<Assignment>> getAIGradingEnabledAssignments() {
        List<Assignment> assignments = assignmentService.getAIGradingEnabledAssignments();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get assignments by date range")
    public ResponseEntity<List<Assignment>> getAssignmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Assignment> assignments = assignmentService.getAssignmentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent assignments")
    public ResponseEntity<List<Assignment>> getRecentAssignments(
            @RequestParam(defaultValue = "30") int days) {
        List<Assignment> assignments = assignmentService.getRecentAssignments(days);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/active-submission")
    @Operation(summary = "Get assignments with active submission period")
    public ResponseEntity<List<Assignment>> getActiveSubmissionPeriodAssignments() {
        List<Assignment> assignments = assignmentService.getActiveSubmissionPeriodAssignments();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get assignments by department")
    public ResponseEntity<List<Assignment>> getAssignmentsByDepartment(@PathVariable String department) {
        List<Assignment> assignments = assignmentService.getAssignmentsByDepartment(department);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/grade/{grade}")
    @Operation(summary = "Get assignments by grade level")
    public ResponseEntity<List<Assignment>> getAssignmentsByGrade(@PathVariable String grade) {
        List<Assignment> assignments = assignmentService.getAssignmentsByGrade(grade);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/requiring-attention")
    @Operation(summary = "Get assignments requiring attention")
    public ResponseEntity<List<Assignment>> getAssignmentsRequiringAttention() {
        List<Assignment> assignments = assignmentService.getAssignmentsRequiringAttention();
        return ResponseEntity.ok(assignments);
    }

    @PostMapping
    @Operation(summary = "Create new assignment")
    public ResponseEntity<Assignment> createAssignment(@Valid @RequestBody Assignment assignment) {
        Assignment savedAssignment = assignmentService.createAssignment(assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAssignment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update assignment")
    public ResponseEntity<Assignment> updateAssignment(
            @PathVariable String id,
            @Valid @RequestBody Assignment assignment) {
        try {
            Assignment updatedAssignment = assignmentService.updateAssignment(id, assignment);
            return ResponseEntity.ok(updatedAssignment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete assignment")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String id) {
        try {
            assignmentService.deleteAssignment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Duplicate assignment")
    public ResponseEntity<Assignment> duplicateAssignment(
            @PathVariable String id,
            @RequestParam String newTitle) {
        try {
            Assignment duplicatedAssignment = assignmentService.duplicateAssignment(id, newTitle);
            return ResponseEntity.status(HttpStatus.CREATED).body(duplicatedAssignment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create multiple assignments")
    public ResponseEntity<List<Assignment>> createBulkAssignments(@Valid @RequestBody List<Assignment> assignments) {
        try {
            List<Assignment> savedAssignments = assignmentService.bulkCreateAssignments(assignments);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAssignments);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/update-submission-count")
    @Operation(summary = "Update submission count")
    public ResponseEntity<Assignment> updateSubmissionCount(@PathVariable String id) {
        try {
            Assignment updatedAssignment = assignmentService.updateSubmissionCount(id);
            return ResponseEntity.ok(updatedAssignment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/update-graded-count")
    @Operation(summary = "Update graded count")
    public ResponseEntity<Assignment> updateGradedCount(@PathVariable String id) {
        try {
            Assignment updatedAssignment = assignmentService.updateGradedCount(id);
            return ResponseEntity.ok(updatedAssignment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/update-average-score")
    @Operation(summary = "Update average score")
    public ResponseEntity<Assignment> updateAverageScore(
            @PathVariable String id,
            @RequestParam Double averageScore) {
        try {
            Assignment updatedAssignment = assignmentService.updateAverageScore(id, averageScore);
            return ResponseEntity.ok(updatedAssignment);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search-advanced")
    @Operation(summary = "Advanced search assignments")
    public ResponseEntity<List<Assignment>> searchAssignmentsByCriteria(
            @RequestParam(required = false) String titleSearch,
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<String> courseCodes) {
        List<Assignment> assignments = assignmentService.getAssignmentsByMultipleCriteria(titleSearch, types, courseCodes);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get assignment statistics")
    public ResponseEntity<Object> getAssignmentStatistics() {
        Object statistics = assignmentService.getAssignmentStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/teacher/{teacherId}/analytics")
    @Operation(summary = "Get teacher assignment analytics")
    public ResponseEntity<Object> getTeacherAssignmentAnalytics(@PathVariable String teacherId) {
        Object analytics = assignmentService.getTeacherAssignmentAnalytics(teacherId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/course/{courseId}/analytics")
    @Operation(summary = "Get course assignment analytics")
    public ResponseEntity<Object> getCourseAssignmentAnalytics(@PathVariable String courseId) {
        try {
            Optional<Course> courseOpt = courseService.getCourseById(courseId);
            if (courseOpt.isPresent()) {
                Object analytics = assignmentService.getCourseAssignmentAnalytics(courseOpt.get());
                return ResponseEntity.ok(analytics);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/dashboard-stats")
    @Operation(summary = "Get dashboard statistics")
    public ResponseEntity<Object> getDashboardStatistics() {
        Object statistics = assignmentService.getDashboardStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/types")
    @Operation(summary = "Get available assignment types")
    public ResponseEntity<List<String>> getAssignmentTypes() {
        List<String> types = List.of("HOMEWORK", "QUIZ", "TEST", "PROJECT", "PARTICIPATION");
        return ResponseEntity.ok(types);
    }

    @GetMapping("/teacher/{teacherId}/workload")
    @Operation(summary = "Get teacher workload by date range")
    public ResponseEntity<List<Assignment>> getTeacherWorkload(
            @PathVariable String teacherId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        // This would need to be added to the repository and service
        List<Assignment> assignments = assignmentService.getAssignmentsByTeacher(teacherId).stream()
            .filter(a -> a.getDueDate().isAfter(startDate) && a.getDueDate().isBefore(endDate))
            .toList();
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{id}/similar")
    @Operation(summary = "Get similar assignments")
    public ResponseEntity<List<Assignment>> getSimilarAssignments(@PathVariable String id) {
        try {
            Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(id);
            if (assignmentOpt.isPresent()) {
                Assignment assignment = assignmentOpt.get();
                // This would need to be implemented in the repository and service
                List<Assignment> similarAssignments = assignmentService.getAssignmentsByType(assignment.getType())
                    .stream()
                    .filter(a -> !a.getId().equals(id))
                    .filter(a -> a.getCourse().equals(assignment.getCourse()))
                    .limit(5)
                    .toList();
                return ResponseEntity.ok(similarAssignments);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/summary")
    @Operation(summary = "Get assignments summary")
    public ResponseEntity<Object> getAssignmentsSummary(
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String department) {

        List<Assignment> assignments;

        if (teacherId != null) {
            assignments = assignmentService.getAssignmentsByTeacher(teacherId);
        } else if (courseCode != null) {
            assignments = assignmentService.getAssignmentsByCourseCode(courseCode);
        } else if (department != null) {
            assignments = assignmentService.getAssignmentsByDepartment(department);
        } else {
            assignments = assignmentService.getAllAssignments();
        }

        long total = assignments.size();
        long overdue = assignments.stream().filter(a -> a.getDueDate().isBefore(LocalDateTime.now())).count();
        long dueSoon = assignments.stream().filter(a -> {
            LocalDateTime now = LocalDateTime.now();
            return a.getDueDate().isAfter(now) && a.getDueDate().isBefore(now.plusDays(7));
        }).count();
        long pendingGrading = assignments.stream().filter(a -> a.getSubmissions() > a.getGraded()).count();

        return ResponseEntity.ok(Map.of(
            "total", total,
            "overdue", overdue,
            "dueSoon", dueSoon,
            "pendingGrading", pendingGrading,
            "assignments", assignments
        ));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate assignment data")
    public ResponseEntity<Object> validateAssignment(@RequestBody Assignment assignment) {
        try {
            // The validation would happen in the service layer
            assignmentService.createAssignment(assignment);
            return ResponseEntity.ok(Map.of("valid", true, "message", "Assignment data is valid"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", e.getMessage()));
        }
    }
}
