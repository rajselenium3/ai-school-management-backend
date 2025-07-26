package com.eduai.schoolmanagement.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.eduai.schoolmanagement.entity.Assignment;
import com.eduai.schoolmanagement.entity.Course;
import com.eduai.schoolmanagement.repository.AssignmentRepository;
import com.eduai.schoolmanagement.repository.CourseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public Optional<Assignment> getAssignmentById(String id) {
        return assignmentRepository.findById(id);
    }

    public List<Assignment> getAssignmentsByCourse(Course course) {
        return assignmentRepository.findByCourse(course);
    }

    public List<Assignment> getAssignmentsByCourseCode(String courseCode) {
        return assignmentRepository.findByCourseCode(courseCode);
    }

    public List<Assignment> getAssignmentsByTeacher(String teacherId) {
        return assignmentRepository.findByTeacherId(teacherId);
    }

    public List<Assignment> getAssignmentsByType(String type) {
        return assignmentRepository.findByType(type);
    }

    public List<Assignment> searchAssignments(String searchTerm) {
        return assignmentRepository.findByTitleContainingIgnoreCase(searchTerm);
    }

    public List<Assignment> getAssignmentsDueSoon(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        return assignmentRepository.findDueSoon(now, futureDate);
    }

    public List<Assignment> getOverdueAssignments() {
        return assignmentRepository.findOverdueAssignments(LocalDateTime.now());
    }

    public List<Assignment> getAssignmentsPendingGrading() {
        return assignmentRepository.findAssignmentsPendingGrading();
    }

    public List<Assignment> getAIGradingEnabledAssignments() {
        return assignmentRepository.findAIGradingEnabledAssignments();
    }

    public Assignment createAssignment(Assignment assignment) {
        log.info("Creating assignment: {}", assignment.getTitle());

        // Set default values
        if (assignment.getCreatedAt() == null) {
            assignment.setCreatedAt(LocalDateTime.now());
        }

        // Set default submission start date if not provided
        if (assignment.getSubmissionStartDate() == null) {
            assignment.setSubmissionStartDate(LocalDateTime.now());
        }

        // Initialize counters
        assignment.setSubmissions(0);
        assignment.setGraded(0);
        assignment.setAverageScore(0.0);

        // Validate business rules
        validateAssignment(assignment);

        Assignment savedAssignment = assignmentRepository.save(assignment);
        log.info("Assignment created successfully with ID: {}", savedAssignment.getId());

        return savedAssignment;
    }

    public Assignment updateAssignment(String id, Assignment assignmentData) {
        log.info("Updating assignment with ID: {}", id);

        Optional<Assignment> existingAssignment = assignmentRepository.findById(id);
        if (existingAssignment.isPresent()) {
            Assignment assignment = existingAssignment.get();

            // Update fields
            assignment.setTitle(assignmentData.getTitle());
            assignment.setDescription(assignmentData.getDescription());
            assignment.setCourse(assignmentData.getCourse());
            assignment.setType(assignmentData.getType());
            assignment.setMaxScore(assignmentData.getMaxScore());
            assignment.setWeight(assignmentData.getWeight());
            assignment.setDueDate(assignmentData.getDueDate());
            assignment.setSubmissionStartDate(assignmentData.getSubmissionStartDate());
            assignment.setAiGradingEnabled(assignmentData.isAiGradingEnabled());
            assignment.setInstructions(assignmentData.getInstructions());
            assignment.setAttachments(assignmentData.getAttachments());
            assignment.setRubric(assignmentData.getRubric());

            // Validate updated assignment
            validateAssignment(assignment);

            return assignmentRepository.save(assignment);
        }
        throw new RuntimeException("Assignment not found with ID: " + id);
    }

    public void deleteAssignment(String id) {
        log.info("Deleting assignment with ID: {}", id);

        Optional<Assignment> assignment = assignmentRepository.findById(id);
        if (assignment.isPresent()) {
            // Check if assignment has submissions
            if (assignment.get().getSubmissions() > 0) {
                throw new RuntimeException("Cannot delete assignment with existing submissions");
            }
            assignmentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Assignment not found with ID: " + id);
        }
    }

    public Assignment duplicateAssignment(String assignmentId, String newTitle) {
        log.info("Duplicating assignment with ID: {} with new title: {}", assignmentId, newTitle);

        Optional<Assignment> originalOpt = assignmentRepository.findById(assignmentId);
        if (originalOpt.isPresent()) {
            Assignment original = originalOpt.get();
            Assignment duplicate = new Assignment();

            // Copy all properties except ID and submission-related data
            duplicate.setTitle(newTitle);
            duplicate.setDescription(original.getDescription());
            duplicate.setCourse(original.getCourse());
            duplicate.setType(original.getType());
            duplicate.setMaxScore(original.getMaxScore());
            duplicate.setWeight(original.getWeight());
            duplicate.setDueDate(original.getDueDate().plusWeeks(1)); // Set due date 1 week later
            duplicate.setSubmissionStartDate(LocalDateTime.now());
            duplicate.setAiGradingEnabled(original.isAiGradingEnabled());
            duplicate.setInstructions(original.getInstructions());
            duplicate.setAttachments(original.getAttachments());
            duplicate.setRubric(original.getRubric());

            // Initialize new assignment counters
            duplicate.setSubmissions(0);
            duplicate.setGraded(0);
            duplicate.setAverageScore(0.0);

            return assignmentRepository.save(duplicate);
        }
        throw new RuntimeException("Assignment not found with ID: " + assignmentId);
    }

    public Assignment updateSubmissionCount(String assignmentId) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            assignment.setSubmissions(assignment.getSubmissions() + 1);
            return assignmentRepository.save(assignment);
        }
        throw new RuntimeException("Assignment not found with ID: " + assignmentId);
    }

    public Assignment updateGradedCount(String assignmentId) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            assignment.setGraded(assignment.getGraded() + 1);
            return assignmentRepository.save(assignment);
        }
        throw new RuntimeException("Assignment not found with ID: " + assignmentId);
    }

    public Assignment updateAverageScore(String assignmentId, double newAverageScore) {
        Optional<Assignment> assignmentOpt = assignmentRepository.findById(assignmentId);
        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();
            assignment.setAverageScore(newAverageScore);
            return assignmentRepository.save(assignment);
        }
        throw new RuntimeException("Assignment not found with ID: " + assignmentId);
    }

    public List<Assignment> getAssignmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return assignmentRepository.findByDueDateBetween(startDate, endDate);
    }

    public List<Assignment> getRecentAssignments(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return assignmentRepository.findRecentAssignments(since);
    }

    public List<Assignment> getActiveSubmissionPeriodAssignments() {
        return assignmentRepository.findActiveSubmissionPeriod(LocalDateTime.now());
    }

    public List<Assignment> getAssignmentsByDepartment(String department) {
        return assignmentRepository.findByCourseDepartment(department);
    }

    public List<Assignment> getAssignmentsByGrade(String grade) {
        return assignmentRepository.findByCourseGrade(grade);
    }

    public List<Assignment> getAssignmentsRequiringAttention() {
        return assignmentRepository.findAssignmentsRequiringAttention(LocalDateTime.now());
    }

    public Object getAssignmentStatistics() {
        long totalAssignments = assignmentRepository.count();
        long overdueAssignments = assignmentRepository.countOverdueAssignments(LocalDateTime.now());
        long aiGradingEnabled = assignmentRepository.countAIGradingEnabledAssignments();
        long pendingGrading = assignmentRepository.findAssignmentsPendingGrading().size();

        // Get assignments due in next 7 days
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextWeek = now.plusDays(7);
        long upcomingAssignments = assignmentRepository.findDueSoon(now, nextWeek).size();

        return Map.of(
            "totalAssignments", totalAssignments,
            "overdueAssignments", overdueAssignments,
            "upcomingAssignments", upcomingAssignments,
            "pendingGrading", pendingGrading,
            "aiGradingEnabled", aiGradingEnabled,
            "assignmentsByType", getAssignmentsByTypeBreakdown(),
            "averageSubmissionRate", calculateAverageSubmissionRate()
        );
    }

    public Object getTeacherAssignmentAnalytics(String teacherId) {
        List<Assignment> teacherAssignments = assignmentRepository.findByTeacherId(teacherId);

        long totalAssignments = teacherAssignments.size();
        long pendingGrading = teacherAssignments.stream()
            .filter(a -> a.getSubmissions() > a.getGraded())
            .count();

        double averageScore = teacherAssignments.stream()
            .mapToDouble(Assignment::getAverageScore)
            .average()
            .orElse(0.0);

        return Map.of(
            "totalAssignments", totalAssignments,
            "pendingGrading", pendingGrading,
            "averageScore", averageScore,
            "submissionRate", calculateTeacherSubmissionRate(teacherAssignments),
            "assignmentDistribution", getTeacherAssignmentDistribution(teacherAssignments)
        );
    }

    public Object getCourseAssignmentAnalytics(Course course) {
        List<Assignment> courseAssignments = assignmentRepository.findByCourse(course);

        int totalAssignments = courseAssignments.size();
        double totalWeight = courseAssignments.stream()
            .mapToDouble(Assignment::getWeight)
            .sum();

        double averageScore = courseAssignments.stream()
            .mapToDouble(Assignment::getAverageScore)
            .average()
            .orElse(0.0);

        return Map.of(
            "totalAssignments", totalAssignments,
            "totalWeight", totalWeight,
            "averageScore", averageScore,
            "submissionRate", calculateCourseSubmissionRate(courseAssignments),
            "difficultyAnalysis", analyzeDifficulty(courseAssignments)
        );
    }

    public List<Assignment> bulkCreateAssignments(List<Assignment> assignments) {
        log.info("Creating {} assignments in bulk", assignments.size());

        assignments.forEach(assignment -> {
            if (assignment.getCreatedAt() == null) {
                assignment.setCreatedAt(LocalDateTime.now());
            }
            if (assignment.getSubmissionStartDate() == null) {
                assignment.setSubmissionStartDate(LocalDateTime.now());
            }
            assignment.setSubmissions(0);
            assignment.setGraded(0);
            assignment.setAverageScore(0.0);
            validateAssignment(assignment);
        });

        return assignmentRepository.saveAll(assignments);
    }

    public List<Assignment> getAssignmentsByMultipleCriteria(String titleSearch, List<String> types, List<String> courseCodes) {
        if (titleSearch == null) titleSearch = "";
        if (types == null || types.isEmpty()) types = List.of("HOMEWORK", "QUIZ", "TEST", "PROJECT", "PARTICIPATION");
        if (courseCodes == null || courseCodes.isEmpty()) {
            // Get all course codes if none specified
            courseCodes = courseRepository.findAll().stream()
                .map(Course::getCourseCode)
                .collect(Collectors.toList());
        }

        return assignmentRepository.searchAssignments(titleSearch, types, courseCodes);
    }

    public Object getDashboardStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6);

        long totalAssignments = assignmentRepository.count();
        long thisWeekDue = assignmentRepository.findByDueDateBetween(startOfWeek, endOfWeek).size();
        long overdue = assignmentRepository.countOverdueAssignments(now);
        long pendingGrading = assignmentRepository.findAssignmentsPendingGrading().size();

        return Map.of(
            "totalAssignments", totalAssignments,
            "thisWeekDue", thisWeekDue,
            "overdueAssignments", overdue,
            "pendingGrading", pendingGrading,
            "aiGradingRate", calculateAIGradingRate()
        );
    }

    // Private helper methods
    private void validateAssignment(Assignment assignment) {
        if (assignment.getTitle() == null || assignment.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Assignment title is required");
        }

        if (assignment.getCourse() == null) {
            throw new RuntimeException("Course is required for assignment");
        }

        if (assignment.getDueDate() == null) {
            throw new RuntimeException("Due date is required");
        }

        if (assignment.getMaxScore() <= 0) {
            throw new RuntimeException("Max score must be positive");
        }

        if (assignment.getWeight() < 0) {
            throw new RuntimeException("Weight cannot be negative");
        }

        if (assignment.getDueDate().isBefore(LocalDateTime.now().minusHours(1))) {
            throw new RuntimeException("Due date cannot be in the past");
        }

        if (assignment.getSubmissionStartDate() != null &&
            assignment.getSubmissionStartDate().isAfter(assignment.getDueDate())) {
            throw new RuntimeException("Submission start date cannot be after due date");
        }
    }

    private Map<String, Long> getAssignmentsByTypeBreakdown() {
        Map<String, Long> breakdown = new HashMap<>();
        breakdown.put("HOMEWORK", assignmentRepository.countByType("HOMEWORK"));
        breakdown.put("QUIZ", assignmentRepository.countByType("QUIZ"));
        breakdown.put("TEST", assignmentRepository.countByType("TEST"));
        breakdown.put("PROJECT", assignmentRepository.countByType("PROJECT"));
        breakdown.put("PARTICIPATION", assignmentRepository.countByType("PARTICIPATION"));
        return breakdown;
    }

    private double calculateAverageSubmissionRate() {
        List<Assignment> allAssignments = assignmentRepository.findAll();
        if (allAssignments.isEmpty()) return 0.0;

        return allAssignments.stream()
            .filter(a -> a.getCourse() != null && a.getCourse().getEnrolledStudents() != null)
            .mapToDouble(this::calculateSubmissionRate)
            .average()
            .orElse(0.0);
    }

    private double calculateSubmissionRate(Assignment assignment) {
        if (assignment.getCourse() == null || assignment.getCourse().getEnrolledStudents() == null) {
            return 0.0;
        }
        int enrolledStudents = assignment.getCourse().getEnrolledStudents().size();
        if (enrolledStudents == 0) return 0.0;
        return (assignment.getSubmissions() * 100.0) / enrolledStudents;
    }

    private double calculateTeacherSubmissionRate(List<Assignment> assignments) {
        if (assignments.isEmpty()) return 0.0;
        return assignments.stream()
            .mapToDouble(this::calculateSubmissionRate)
            .average()
            .orElse(0.0);
    }

    private double calculateCourseSubmissionRate(List<Assignment> assignments) {
        if (assignments.isEmpty()) return 0.0;
        return assignments.stream()
            .mapToDouble(this::calculateSubmissionRate)
            .average()
            .orElse(0.0);
    }

    private Map<String, Long> getTeacherAssignmentDistribution(List<Assignment> assignments) {
        return assignments.stream()
            .collect(Collectors.groupingBy(
                Assignment::getType,
                Collectors.counting()
            ));
    }

    private Map<String, Object> analyzeDifficulty(List<Assignment> assignments) {
        if (assignments.isEmpty()) {
            return Map.of("difficulty", "UNKNOWN", "averageScore", 0.0);
        }

        double avgScore = assignments.stream()
            .mapToDouble(Assignment::getAverageScore)
            .average()
            .orElse(0.0);

        String difficulty;
        if (avgScore >= 85) difficulty = "EASY";
        else if (avgScore >= 70) difficulty = "MODERATE";
        else if (avgScore >= 60) difficulty = "HARD";
        else difficulty = "VERY_HARD";

        return Map.of(
            "difficulty", difficulty,
            "averageScore", avgScore,
            "scoreDistribution", getScoreDistribution(assignments)
        );
    }

    private Map<String, Long> getScoreDistribution(List<Assignment> assignments) {
        Map<String, Long> distribution = new HashMap<>();

        for (Assignment assignment : assignments) {
            String range = getScoreRange(assignment.getAverageScore());
            distribution.merge(range, 1L, Long::sum);
        }

        return distribution;
    }

    private String getScoreRange(double score) {
        if (score >= 90) return "90-100";
        if (score >= 80) return "80-89";
        if (score >= 70) return "70-79";
        if (score >= 60) return "60-69";
        return "Below 60";
    }

    private double calculateAIGradingRate() {
        long total = assignmentRepository.count();
        long aiEnabled = assignmentRepository.countAIGradingEnabledAssignments();
        return total > 0 ? (aiEnabled * 100.0 / total) : 0.0;
    }
}
