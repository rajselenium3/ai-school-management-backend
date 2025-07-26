package com.eduai.schoolmanagement.controller;

import java.util.List;
import java.util.Optional;

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
import com.eduai.schoolmanagement.entity.Grade;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.repository.AssignmentRepository;
import com.eduai.schoolmanagement.service.CourseService;
import com.eduai.schoolmanagement.service.GradeService;
import com.eduai.schoolmanagement.service.StudentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/grades")
@RequiredArgsConstructor
@Tag(name = "Grade Management", description = "Grade management and AI grading operations")
@CrossOrigin(origins = "*")
public class GradeController {

    private final GradeService gradeService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final AssignmentRepository assignmentRepository;

    @GetMapping
    @Operation(summary = "Get all grades")
    public ResponseEntity<List<Grade>> getAllGrades() {
        List<Grade> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get grade by ID")
    public ResponseEntity<Grade> getGradeById(@PathVariable String id) {
        Optional<Grade> grade = gradeService.getGradeById(id);
        return grade.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get grades by student")
    public ResponseEntity<List<Grade>> getGradesByStudent(@PathVariable String studentId) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<Grade> grades = gradeService.getGradesByStudent(studentOpt.get());
            return ResponseEntity.ok(grades);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/course/{courseCode}")
    @Operation(summary = "Get grades by course")
    public ResponseEntity<List<Grade>> getGradesByCourse(@PathVariable String courseCode) {
        try {
            Optional<Course> courseOpt = courseService.getCourseByCourseCode(courseCode);
            if (courseOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<Grade> grades = gradeService.getGradesByCourse(courseOpt.get());
            return ResponseEntity.ok(grades);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/assignment/{assignmentId}")
    @Operation(summary = "Get grades by assignment")
    public ResponseEntity<List<Grade>> getGradesByAssignment(@PathVariable String assignmentId) {
        try {
            Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
            if (assignmentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<Grade> grades = gradeService.getGradesByAssignment(assignmentOpt.get());
            return ResponseEntity.ok(grades);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending grades")
    public ResponseEntity<List<Grade>> getPendingGrades() {
        List<Grade> grades = gradeService.getPendingGrades();
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/ai-generated")
    @Operation(summary = "Get AI generated grades")
    public ResponseEntity<List<Grade>> getAIGeneratedGrades() {
        List<Grade> grades = gradeService.getAIGeneratedGrades();
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/ai-confidence/{threshold}")
    @Operation(summary = "Get high confidence AI grades")
    public ResponseEntity<List<Grade>> getHighConfidenceAIGrades(@PathVariable double threshold) {
        List<Grade> grades = gradeService.getHighConfidenceAIGrades(threshold);
        return ResponseEntity.ok(grades);
    }

    @PostMapping
    @Operation(summary = "Create new grade")
    public ResponseEntity<Grade> createGrade(@Valid @RequestBody Grade grade) {
        Grade savedGrade = gradeService.saveGrade(grade);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGrade);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update grade")
    public ResponseEntity<Grade> updateGrade(@PathVariable String id, @Valid @RequestBody Grade grade) {
        Grade updatedGrade = gradeService.updateGrade(id, grade);
        return ResponseEntity.ok(updatedGrade);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete grade")
    public ResponseEntity<Void> deleteGrade(@PathVariable String id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ai-grade")
    @Operation(summary = "Generate AI grade")
    public ResponseEntity<Grade> generateAIGrade(
            @PathVariable String id,
            @RequestParam double aiScore,
            @RequestParam double confidence,
            @RequestParam String feedback) {

        Optional<Grade> gradeOpt = gradeService.getGradeById(id);
        if (gradeOpt.isPresent()) {
            Grade grade = gradeService.generateAIGrade(gradeOpt.get(), aiScore, confidence, feedback);
            return ResponseEntity.ok(grade);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/review-ai-grade")
    @Operation(summary = "Review AI grade")
    public ResponseEntity<Grade> reviewAIGrade(
            @PathVariable String id,
            @RequestParam boolean approved,
            @RequestParam(required = false) String reviewComments) {

        try {
            Grade grade = gradeService.reviewAIGrade(id, approved, reviewComments);
            return ResponseEntity.ok(grade);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/batch-ai-grade")
    @Operation(summary = "Batch AI grading")
    public ResponseEntity<List<Grade>> batchAIGrade(@RequestBody List<String> gradeIds) {
        try {
            List<Grade> gradedGrades = gradeIds.stream()
                .map(gradeService::getGradeById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(grade -> {
                    // Simulate AI grading with random scores and feedback
                    double aiScore = 75 + (Math.random() * 25); // Random score between 75-100
                    double confidence = 0.8 + (Math.random() * 0.2); // Random confidence 0.8-1.0
                    String feedback = generateAIFeedback(aiScore);
                    return gradeService.generateAIGrade(grade, aiScore, confidence, feedback);
                })
                .toList();
            return ResponseEntity.ok(gradedGrades);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/student/{studentId}/course/{courseCode}")
    @Operation(summary = "Get grades by student and course")
    public ResponseEntity<List<Grade>> getGradesByStudentAndCourse(
            @PathVariable String studentId, @PathVariable String courseCode) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            Optional<Course> courseOpt = courseService.getCourseByCourseCode(courseCode);

            if (studentOpt.isEmpty() || courseOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<Grade> grades = gradeService.getGradesByStudentAndCourse(studentOpt.get(), courseOpt.get());
            return ResponseEntity.ok(grades);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/student/{studentId}/assignment/{assignmentId}")
    @Operation(summary = "Get specific grade by student and assignment")
    public ResponseEntity<Grade> getGradeByStudentAndAssignment(
            @PathVariable String studentId, @PathVariable String assignmentId) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);

            if (studentOpt.isEmpty() || assignmentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Optional<Grade> gradeOpt = gradeService.getGradeByStudentAndAssignment(
                studentOpt.get(), assignmentOpt.get());

            return gradeOpt.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/bulk-create")
    @Operation(summary = "Bulk create grades for assignment")
    public ResponseEntity<List<Grade>> bulkCreateGrades(
            @RequestParam String assignmentId,
            @RequestParam String courseCode,
            @RequestBody List<String> studentIds) {
        try {
            Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
            Optional<Course> courseOpt = courseService.getCourseByCourseCode(courseCode);

            if (assignmentOpt.isEmpty() || courseOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Assignment assignment = assignmentOpt.get();
            Course course = courseOpt.get();

            List<Grade> createdGrades = studentIds.stream()
                .map(studentService::getStudentByStudentId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(student -> {
                    Grade grade = new Grade();
                    grade.setStudent(student);
                    grade.setCourse(course);
                    grade.setAssignment(assignment);
                    grade.setMaxScore(assignment.getMaxScore());
                    grade.setScore(0.0);
                    grade.setPercentage(0.0);
                    grade.setStatus("PENDING");
                    return gradeService.saveGrade(grade);
                })
                .toList();

            return ResponseEntity.status(HttpStatus.CREATED).body(createdGrades);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/analytics/course/{courseCode}")
    @Operation(summary = "Get grade analytics for course")
    public ResponseEntity<Object> getCourseGradeAnalytics(@PathVariable String courseCode) {
        try {
            Optional<Course> courseOpt = courseService.getCourseByCourseCode(courseCode);
            if (courseOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<Grade> grades = gradeService.getGradesByCourse(courseOpt.get());

            var analytics = calculateGradeAnalytics(grades);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/analytics/student/{studentId}")
    @Operation(summary = "Get grade analytics for student")
    public ResponseEntity<Object> getStudentGradeAnalytics(@PathVariable String studentId) {
        try {
            Optional<Student> studentOpt = studentService.getStudentByStudentId(studentId);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            List<Grade> grades = gradeService.getGradesByStudent(studentOpt.get());

            var analytics = calculateGradeAnalytics(grades);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/publish")
    @Operation(summary = "Publish grade to student")
    public ResponseEntity<Grade> publishGrade(@PathVariable String id) {
        try {
            Optional<Grade> gradeOpt = gradeService.getGradeById(id);
            if (gradeOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Grade grade = gradeOpt.get();
            grade.setStatus("PUBLISHED");
            Grade updatedGrade = gradeService.updateGrade(id, grade);
            return ResponseEntity.ok(updatedGrade);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String generateAIFeedback(double score) {
        if (score >= 90) {
            return "Excellent work! Your understanding of the concepts is clearly demonstrated.";
        } else if (score >= 80) {
            return "Good work! You show solid understanding with minor areas for improvement.";
        } else if (score >= 70) {
            return "Satisfactory work. Consider reviewing the key concepts and practice more.";
        } else if (score >= 60) {
            return "Your work shows some understanding, but significant improvement is needed.";
        } else {
            return "This work requires major revision. Please seek additional help and practice.";
        }
    }

    private Object calculateGradeAnalytics(List<Grade> grades) {
        if (grades.isEmpty()) {
            return java.util.Map.of("message", "No grades available for analysis");
        }

        // Filter out grades with no score (score = 0.0 means not graded yet)
        List<Grade> gradedGrades = grades.stream()
            .filter(g -> g.getScore() > 0)
            .toList();

        double totalScore = gradedGrades.stream()
            .mapToDouble(Grade::getScore)
            .sum();
        double averageScore = gradedGrades.isEmpty() ? 0.0 : totalScore / gradedGrades.size();

        double totalPercentage = gradedGrades.stream()
            .mapToDouble(Grade::getPercentage)
            .sum();
        double averagePercentage = gradedGrades.isEmpty() ? 0.0 : totalPercentage / gradedGrades.size();

        long pendingCount = grades.stream()
            .filter(g -> "PENDING".equals(g.getStatus()))
            .count();

        long gradedCount = grades.stream()
            .filter(g -> "GRADED".equals(g.getStatus()) || "PUBLISHED".equals(g.getStatus()))
            .count();

        var letterGradeDistribution = grades.stream()
            .filter(g -> g.getLetterGrade() != null && !g.getLetterGrade().isEmpty())
            .collect(java.util.stream.Collectors.groupingBy(
                Grade::getLetterGrade,
                java.util.stream.Collectors.counting()));

        // Calculate passing rate only for grades that have been graded (percentage > 0)
        long passingGrades = grades.stream()
            .filter(g -> g.getPercentage() >= 60)
            .count();

        double passingRate = gradedGrades.isEmpty() ? 0.0 :
            (double) passingGrades * 100.0 / gradedGrades.size();

        return java.util.Map.of(
            "totalGrades", grades.size(),
            "gradedGrades", gradedGrades.size(),
            "averageScore", Math.round(averageScore * 100.0) / 100.0,
            "averagePercentage", Math.round(averagePercentage * 100.0) / 100.0,
            "pendingGrades", pendingCount,
            "gradedCount", gradedCount,
            "letterGradeDistribution", letterGradeDistribution,
            "passingRate", Math.round(passingRate * 100.0) / 100.0
        );
    }
}
