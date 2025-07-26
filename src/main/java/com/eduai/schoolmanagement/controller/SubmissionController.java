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
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.Submission;
import com.eduai.schoolmanagement.service.AssignmentService;
import com.eduai.schoolmanagement.service.StudentService;
import com.eduai.schoolmanagement.service.SubmissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/submissions")
@RequiredArgsConstructor
@Tag(name = "Submission Management", description = "Student submission and grading operations")
@CrossOrigin(origins = "*")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final AssignmentService assignmentService;
    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Get all submissions")
    public ResponseEntity<List<Submission>> getAllSubmissions() {
        List<Submission> submissions = submissionService.getAllSubmissions();
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get submission by ID")
    public ResponseEntity<Submission> getSubmissionById(@PathVariable String id) {
        Optional<Submission> submission = submissionService.getSubmissionById(id);
        return submission.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/assignment/{assignmentId}")
    @Operation(summary = "Get submissions by assignment ID")
    public ResponseEntity<List<Submission>> getSubmissionsByAssignmentId(@PathVariable String assignmentId) {
        try {
            Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
            if (assignmentOpt.isPresent()) {
                List<Submission> submissions = submissionService.getSubmissionsByAssignment(assignmentOpt.get());
                return ResponseEntity.ok(submissions);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get submissions by student ID")
    public ResponseEntity<List<Submission>> getSubmissionsByStudentId(@PathVariable String studentId) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            if (studentOpt.isPresent()) {
                List<Submission> submissions = submissionService.getSubmissionsByStudent(studentOpt.get());
                return ResponseEntity.ok(submissions);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/assignment/{assignmentId}/student/{studentId}")
    @Operation(summary = "Get submission by assignment and student")
    public ResponseEntity<Submission> getSubmissionByAssignmentAndStudent(
            @PathVariable String assignmentId,
            @PathVariable String studentId) {
        try {
            Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);

            if (assignmentOpt.isPresent() && studentOpt.isPresent()) {
                Optional<Submission> submission = submissionService.getSubmissionByAssignmentAndStudent(
                    assignmentOpt.get(), studentOpt.get());
                return submission.map(ResponseEntity::ok)
                               .orElse(ResponseEntity.notFound().build());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get submissions by status")
    public ResponseEntity<List<Submission>> getSubmissionsByStatus(@PathVariable String status) {
        List<Submission> submissions = submissionService.getSubmissionsByStatus(status);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/pending-grading")
    @Operation(summary = "Get submissions pending grading")
    public ResponseEntity<List<Submission>> getPendingGradingSubmissions() {
        List<Submission> submissions = submissionService.getPendingGradingSubmissions();
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/ai-graded")
    @Operation(summary = "Get AI graded submissions")
    public ResponseEntity<List<Submission>> getAIGradedSubmissions() {
        List<Submission> submissions = submissionService.getAIGradedSubmissions();
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/need-human-review")
    @Operation(summary = "Get submissions needing human review")
    public ResponseEntity<List<Submission>> getSubmissionsNeedingHumanReview() {
        List<Submission> submissions = submissionService.getSubmissionsNeedingHumanReview();
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/late")
    @Operation(summary = "Get late submissions")
    public ResponseEntity<List<Submission>> getLateSubmissions() {
        List<Submission> submissions = submissionService.getLateSubmissions();
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/plagiarism-flagged")
    @Operation(summary = "Get plagiarism flagged submissions")
    public ResponseEntity<List<Submission>> getPlagiarismFlaggedSubmissions() {
        List<Submission> submissions = submissionService.getPlagiarismFlaggedSubmissions();
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/requiring-attention")
    @Operation(summary = "Get submissions requiring attention")
    public ResponseEntity<List<Submission>> getSubmissionsRequiringAttention() {
        List<Submission> submissions = submissionService.getSubmissionsRequiringAttention();
        return ResponseEntity.ok(submissions);
    }

    @PostMapping("/create")
    @Operation(summary = "Create new submission")
    public ResponseEntity<Submission> createSubmission(
            @RequestParam String assignmentId,
            @RequestParam String studentId,
            @RequestParam String textContent,
            @RequestParam(required = false) List<String> attachments) {
        try {
            Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);

            if (assignmentOpt.isPresent() && studentOpt.isPresent()) {
                Submission submission = submissionService.createSubmission(
                    assignmentOpt.get(),
                    studentOpt.get(),
                    textContent,
                    attachments
                );
                return ResponseEntity.status(HttpStatus.CREATED).body(submission);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/submit")
    @Operation(summary = "Submit assignment")
    public ResponseEntity<Submission> submitAssignment(@PathVariable String id) {
        try {
            Submission submission = submissionService.submitAssignment(id);
            return ResponseEntity.ok(submission);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/ai-grade")
    @Operation(summary = "Perform AI grading")
    public ResponseEntity<Submission> performAIGrading(@PathVariable String id) {
        try {
            Optional<Submission> submissionOpt = submissionService.getSubmissionById(id);
            if (submissionOpt.isPresent()) {
                Submission submission = submissionService.performAIGrading(submissionOpt.get());
                return ResponseEntity.ok(submission);
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/review-ai-grading")
    @Operation(summary = "Review AI grading")
    public ResponseEntity<Submission> reviewAIGrading(
            @PathVariable String id,
            @RequestParam boolean approved,
            @RequestParam String humanComments,
            @RequestParam(required = false) Double humanScore) {
        try {
            Submission submission = submissionService.reviewAIGrading(id, approved, humanComments, humanScore);
            return ResponseEntity.ok(submission);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/plagiarism-check")
    @Operation(summary = "Perform plagiarism check")
    public ResponseEntity<Submission> performPlagiarismCheck(@PathVariable String id) {
        try {
            Submission submission = submissionService.performPlagiarismCheck(id);
            return ResponseEntity.ok(submission);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/content")
    @Operation(summary = "Update submission content")
    public ResponseEntity<Submission> updateSubmissionContent(
            @PathVariable String id,
            @RequestParam String textContent,
            @RequestParam(required = false) List<String> attachments) {
        try {
            Submission submission = submissionService.updateSubmissionContent(id, textContent, attachments);
            return ResponseEntity.ok(submission);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/grade")
    @Operation(summary = "Grade submission manually")
    public ResponseEntity<Submission> gradeSubmission(
            @PathVariable String id,
            @RequestParam Double score,
            @RequestParam(required = false) String feedback) {
        try {
            Submission submission = submissionService.gradeSubmission(id, score, feedback);
            return ResponseEntity.ok(submission);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete submission")
    public ResponseEntity<Void> deleteSubmission(@PathVariable String id) {
        try {
            submissionService.deleteSubmission(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get submission statistics")
    public ResponseEntity<Object> getSubmissionStatistics() {
        Object statistics = submissionService.getSubmissionStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/assignment/{assignmentId}/analytics")
    @Operation(summary = "Get assignment submission analytics")
    public ResponseEntity<Object> getAssignmentSubmissionAnalytics(@PathVariable String assignmentId) {
        try {
            Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
            if (assignmentOpt.isPresent()) {
                Object analytics = submissionService.getAssignmentSubmissionAnalytics(assignmentOpt.get());
                return ResponseEntity.ok(analytics);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get submissions by date range")
    public ResponseEntity<List<Submission>> getSubmissionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        // This would need to be added to the service
        List<Submission> submissions = submissionService.getAllSubmissions().stream()
            .filter(s -> s.getSubmittedAt() != null)
            .filter(s -> s.getSubmittedAt().isAfter(startDate) && s.getSubmittedAt().isBefore(endDate))
            .toList();
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/student/{studentId}/history")
    @Operation(summary = "Get student submission history")
    public ResponseEntity<List<Submission>> getStudentSubmissionHistory(@PathVariable String studentId) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            if (studentOpt.isPresent()) {
                List<Submission> submissions = submissionService.getSubmissionsByStudent(studentOpt.get());
                return ResponseEntity.ok(submissions);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/summary")
    @Operation(summary = "Get submissions summary")
    public ResponseEntity<Object> getSubmissionsSummary(
            @RequestParam(required = false) String assignmentId,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String status) {

        List<Submission> submissions;

        if (assignmentId != null) {
            Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
            if (assignmentOpt.isPresent()) {
                submissions = submissionService.getSubmissionsByAssignment(assignmentOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } else if (studentId != null) {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            if (studentOpt.isPresent()) {
                submissions = submissionService.getSubmissionsByStudent(studentOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } else if (status != null) {
            submissions = submissionService.getSubmissionsByStatus(status);
        } else {
            submissions = submissionService.getAllSubmissions();
        }

        long total = submissions.size();
        long pending = submissions.stream().filter(s -> "DRAFT".equals(s.getStatus())).count();
        long submitted = submissions.stream().filter(s -> s.isSubmitted()).count();
        long graded = submissions.stream().filter(s -> s.isGraded()).count();
        long late = submissions.stream().filter(s -> s.isLate()).count();

        return ResponseEntity.ok(Map.of(
            "total", total,
            "pending", pending,
            "submitted", submitted,
            "graded", graded,
            "late", late,
            "submissions", submissions
        ));
    }

    @GetMapping("/statuses")
    @Operation(summary = "Get available submission statuses")
    public ResponseEntity<List<String>> getSubmissionStatuses() {
        List<String> statuses = List.of("DRAFT", "SUBMITTED", "LATE", "GRADED", "RETURNED");
        return ResponseEntity.ok(statuses);
    }

    @PostMapping("/bulk-ai-grade")
    @Operation(summary = "Perform bulk AI grading")
    public ResponseEntity<Object> performBulkAIGrading(@RequestBody List<String> submissionIds) {
        try {
            int successful = 0;
            int failed = 0;

            for (String submissionId : submissionIds) {
                try {
                    Optional<Submission> submissionOpt = submissionService.getSubmissionById(submissionId);
                    if (submissionOpt.isPresent()) {
                        submissionService.performAIGrading(submissionOpt.get());
                        successful++;
                    } else {
                        failed++;
                    }
                } catch (Exception e) {
                    failed++;
                }
            }

            return ResponseEntity.ok(Map.of(
                "total", submissionIds.size(),
                "successful", successful,
                "failed", failed,
                "message", String.format("Bulk AI grading completed: %d successful, %d failed", successful, failed)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Bulk AI grading failed: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/bulk-plagiarism-check")
    @Operation(summary = "Perform bulk plagiarism check")
    public ResponseEntity<Object> performBulkPlagiarismCheck(@RequestBody List<String> submissionIds) {
        try {
            int successful = 0;
            int failed = 0;
            int flagged = 0;

            for (String submissionId : submissionIds) {
                try {
                    Submission submission = submissionService.performPlagiarismCheck(submissionId);
                    successful++;
                    if (submission.getPlagiarismCheck() != null &&
                        "FLAGGED".equals(submission.getPlagiarismCheck().getStatus())) {
                        flagged++;
                    }
                } catch (Exception e) {
                    failed++;
                }
            }

            return ResponseEntity.ok(Map.of(
                "total", submissionIds.size(),
                "successful", successful,
                "failed", failed,
                "flagged", flagged,
                "message", String.format("Bulk plagiarism check completed: %d checked, %d flagged, %d failed",
                    successful, flagged, failed)
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Bulk plagiarism check failed: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/dashboard-stats")
    @Operation(summary = "Get dashboard statistics for submissions")
    public ResponseEntity<Object> getDashboardStatistics() {
        Object statistics = submissionService.getSubmissionStatistics();
        List<Submission> requiresAttention = submissionService.getSubmissionsRequiringAttention();

        return ResponseEntity.ok(Map.of(
            "submissionStatistics", statistics,
            "requiresAttention", requiresAttention.size(),
            "recentSubmissions", submissionService.getAllSubmissions().stream()
                .filter(s -> s.getSubmittedAt() != null &&
                    s.getSubmittedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .limit(10)
                .toList()
        ));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate submission data")
    public ResponseEntity<Object> validateSubmission(
            @RequestParam String assignmentId,
            @RequestParam String studentId,
            @RequestParam String textContent) {
        try {
            Optional<Assignment> assignmentOpt = assignmentService.getAssignmentById(assignmentId);
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);

            if (assignmentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Assignment not found"));
            }
            if (studentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Student not found"));
            }
            if (textContent == null || textContent.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("valid", false, "message", "Content cannot be empty"));
            }

            Assignment assignment = assignmentOpt.get();
            if (assignment.getDueDate().isBefore(LocalDateTime.now())) {
                return ResponseEntity.ok(Map.of("valid", true, "warning", "Assignment is past due date"));
            }

            return ResponseEntity.ok(Map.of("valid", true, "message", "Submission data is valid"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("valid", false, "message", e.getMessage()));
        }
    }
}
