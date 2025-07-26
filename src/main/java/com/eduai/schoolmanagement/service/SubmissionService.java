package com.eduai.schoolmanagement.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.eduai.schoolmanagement.entity.Assignment;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.Submission;
import com.eduai.schoolmanagement.repository.SubmissionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public Optional<Submission> getSubmissionById(String id) {
        return submissionRepository.findById(id);
    }

    public List<Submission> getSubmissionsByAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    public List<Submission> getSubmissionsByStudent(Student student) {
        return submissionRepository.findByStudent(student);
    }

    public Optional<Submission> getSubmissionByAssignmentAndStudent(Assignment assignment, Student student) {
        return submissionRepository.findLatestSubmissionByAssignmentAndStudent(assignment, student);
    }

    public List<Submission> getSubmissionsByStatus(String status) {
        return submissionRepository.findByStatus(status);
    }

    public List<Submission> getPendingGradingSubmissions() {
        return submissionRepository.findPendingGrading();
    }

    public List<Submission> getAIGradedSubmissions() {
        return submissionRepository.findAIGradedSubmissions();
    }

    public List<Submission> getSubmissionsNeedingHumanReview() {
        return submissionRepository.findSubmissionsNeedingHumanReview();
    }

    public List<Submission> getLateSubmissions() {
        return submissionRepository.findLateSubmissions();
    }

    public List<Submission> getPlagiarismFlaggedSubmissions() {
        return submissionRepository.findPlagiarismFlaggedSubmissions();
    }

    public Submission createSubmission(Assignment assignment, Student student, String textContent, List<String> attachments) {
        log.info("Creating submission for student {} and assignment {}", student.getStudentId(), assignment.getTitle());

        // Check if there's already a submission
        Optional<Submission> existingSubmission = submissionRepository.findLatestSubmissionByAssignmentAndStudent(assignment, student);

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setTextContent(textContent);
        submission.setAttachments(attachments);
        submission.setStatus("DRAFT");
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setMaxScore(assignment.getMaxScore());

        // Set attempt number
        if (existingSubmission.isPresent()) {
            submission.setAttemptNumber(existingSubmission.get().getAttemptNumber() + 1);
            // Mark previous submission as not latest
            existingSubmission.get().setLatestAttempt(false);
            submissionRepository.save(existingSubmission.get());
        } else {
            submission.setAttemptNumber(1);
        }

        submission.setLatestAttempt(true);
        submission.setCreatedAt(LocalDateTime.now());

        // Initialize analytics
        Submission.SubmissionAnalytics analytics = new Submission.SubmissionAnalytics();
        analytics.setTimeStarted(LocalDateTime.now());
        analytics.setLastActivity(LocalDateTime.now());
        analytics.setRevisionCount(0);
        submission.setAnalytics(analytics);

        return submissionRepository.save(submission);
    }

    public Submission submitAssignment(String submissionId) {
        Optional<Submission> submissionOpt = submissionRepository.findById(submissionId);
        if (submissionOpt.isPresent()) {
            Submission submission = submissionOpt.get();
            submission.setStatus(submission.isLate() ? "LATE" : "SUBMITTED");
            submission.setSubmittedAt(LocalDateTime.now());

            log.info("Assignment submitted: {} by student {}",
                submission.getAssignment().getTitle(), submission.getStudent().getStudentId());

            // Trigger AI grading if enabled
            if (submission.getAssignment().isAiGradingEnabled()) {
                return performAIGrading(submission);
            }

            return submissionRepository.save(submission);
        }
        throw new RuntimeException("Submission not found with id: " + submissionId);
    }

    public Submission performAIGrading(Submission submission) {
        log.info("Performing AI grading for submission {}", submission.getId());

        Submission.AIGrading aiGrading = new Submission.AIGrading();

        // Simulate AI grading logic
        double aiScore = generateAIScore(submission);
        double confidence = calculateAIConfidence(submission);
        String aiFeedback = generateAIFeedback(submission, aiScore);
        Map<String, Double> rubricScores = generateRubricScores(submission);

        aiGrading.setAiGraded(true);
        aiGrading.setAiScore(aiScore);
        aiGrading.setConfidence(confidence);
        aiGrading.setAiFeedback(aiFeedback);
        aiGrading.setRubricScores(rubricScores);
        aiGrading.setAiGradedAt(LocalDateTime.now());
        aiGrading.setAiModel("GPT-4-Education-v1.0");
        aiGrading.setHumanReviewed(false);

        submission.setAiGrading(aiGrading);
        submission.setScore(aiScore);
        submission.setPercentage((aiScore / submission.getMaxScore()) * 100);
        submission.setLetterGrade(calculateLetterGrade(submission.getPercentage()));
        submission.setGradedAt(LocalDateTime.now());
        submission.setStatus("GRADED");

        log.info("AI grading completed: Score={}, Confidence={}", aiScore, confidence);
        return submissionRepository.save(submission);
    }

    public Submission reviewAIGrading(String submissionId, boolean approved, String humanComments, Double humanScore) {
        Optional<Submission> submissionOpt = submissionRepository.findById(submissionId);
        if (submissionOpt.isPresent()) {
            Submission submission = submissionOpt.get();

            if (submission.getAiGrading() != null) {
                submission.getAiGrading().setHumanReviewed(true);
                submission.getAiGrading().setHumanReviewComments(humanComments);

                if (!approved && humanScore != null) {
                    // Override AI score with human score
                    submission.setScore(humanScore);
                    submission.setPercentage((humanScore / submission.getMaxScore()) * 100);
                    submission.setLetterGrade(calculateLetterGrade(submission.getPercentage()));
                    submission.setFeedback(humanComments);
                }

                log.info("AI grading reviewed: Approved={}, Human Score={}", approved, humanScore);
                return submissionRepository.save(submission);
            }
        }
        throw new RuntimeException("Submission not found or not AI graded");
    }

    public Submission performPlagiarismCheck(String submissionId) {
        Optional<Submission> submissionOpt = submissionRepository.findById(submissionId);
        if (submissionOpt.isPresent()) {
            Submission submission = submissionOpt.get();

            Submission.PlagiarismCheck plagiarismCheck = new Submission.PlagiarismCheck();

            // Simulate plagiarism checking
            double similarityScore = Math.random() * 30; // 0-30% similarity
            String status = similarityScore > 20 ? "FLAGGED" : "PASSED";

            plagiarismCheck.setChecked(true);
            plagiarismCheck.setSimilarityScore(similarityScore);
            plagiarismCheck.setStatus(status);
            plagiarismCheck.setCheckedAt(LocalDateTime.now());
            plagiarismCheck.setCheckingService("AI-PLAGIARISM-DETECTOR");

            submission.setPlagiarismCheck(plagiarismCheck);

            log.info("Plagiarism check completed: Similarity={}%, Status={}", similarityScore, status);
            return submissionRepository.save(submission);
        }
        throw new RuntimeException("Submission not found with id: " + submissionId);
    }

    public Submission updateSubmissionContent(String submissionId, String textContent, List<String> attachments) {
        Optional<Submission> submissionOpt = submissionRepository.findById(submissionId);
        if (submissionOpt.isPresent()) {
            Submission submission = submissionOpt.get();

            if (!"SUBMITTED".equals(submission.getStatus()) && !"GRADED".equals(submission.getStatus())) {
                submission.setTextContent(textContent);
                submission.setAttachments(attachments);
                submission.setLastModifiedAt(LocalDateTime.now());

                // Update analytics
                if (submission.getAnalytics() != null) {
                    submission.getAnalytics().setLastActivity(LocalDateTime.now());
                    submission.getAnalytics().setRevisionCount(
                        submission.getAnalytics().getRevisionCount() + 1);
                }

                return submissionRepository.save(submission);
            } else {
                throw new RuntimeException("Cannot modify submitted assignment");
            }
        }
        throw new RuntimeException("Submission not found with id: " + submissionId);
    }

    public Submission gradeSubmission(String submissionId, Double score, String feedback) {
        Optional<Submission> submissionOpt = submissionRepository.findById(submissionId);
        if (submissionOpt.isPresent()) {
            Submission submission = submissionOpt.get();

            submission.setScore(score);
            submission.setPercentage((score / submission.getMaxScore()) * 100);
            submission.setLetterGrade(calculateLetterGrade(submission.getPercentage()));
            submission.setFeedback(feedback);
            submission.setGradedAt(LocalDateTime.now());
            submission.setStatus("GRADED");

            log.info("Manual grading completed for submission {}: Score={}", submissionId, score);
            return submissionRepository.save(submission);
        }
        throw new RuntimeException("Submission not found with id: " + submissionId);
    }

    public void deleteSubmission(String id) {
        log.info("Deleting submission with id: {}", id);
        submissionRepository.deleteById(id);
    }

    public Object getSubmissionStatistics() {
        long totalSubmissions = submissionRepository.count();
        long pendingGrading = submissionRepository.countByStatus("SUBMITTED");
        long gradedSubmissions = submissionRepository.countByStatus("GRADED");
        long lateSubmissions = submissionRepository.countLateSubmissions();
        long aiGradedSubmissions = submissionRepository.countAIGradedSubmissions();

        return Map.of(
            "totalSubmissions", totalSubmissions,
            "pendingGrading", pendingGrading,
            "gradedSubmissions", gradedSubmissions,
            "lateSubmissions", lateSubmissions,
            "aiGradedSubmissions", aiGradedSubmissions,
            "gradingRate", totalSubmissions > 0 ? (gradedSubmissions * 100.0 / totalSubmissions) : 0,
            "aiGradingRate", gradedSubmissions > 0 ? (aiGradedSubmissions * 100.0 / gradedSubmissions) : 0
        );
    }

    public Object getAssignmentSubmissionAnalytics(Assignment assignment) {
        List<Submission> submissions = submissionRepository.findByAssignment(assignment);
        List<Submission> gradedSubmissions = submissionRepository.findGradedSubmissionsByAssignment(assignment);

        double averageScore = gradedSubmissions.stream()
            .mapToDouble(s -> s.getFinalScore() != null ? s.getFinalScore() : 0.0)
            .average()
            .orElse(0.0);

        long onTimeSubmissions = submissions.stream()
            .filter(s -> s.isSubmitted() && !s.isLate())
            .count();

        return Map.of(
            "totalSubmissions", submissions.size(),
            "gradedSubmissions", gradedSubmissions.size(),
            "averageScore", averageScore,
            "onTimeSubmissions", onTimeSubmissions,
            "lateSubmissions", submissions.size() - onTimeSubmissions,
            "submissionRate", assignment.getCourse().getEnrolledStudents() != null ?
                (submissions.size() * 100.0 / assignment.getCourse().getEnrolledStudents().size()) : 0
        );
    }

    public List<Submission> getSubmissionsRequiringAttention() {
        return submissionRepository.findSubmissionsRequiringAttention();
    }

    // Private helper methods
    private double generateAIScore(Submission submission) {
        // Simulate AI scoring based on content length, keywords, structure
        String content = submission.getTextContent();
        if (content == null || content.trim().isEmpty()) {
            return 0.0;
        }

        // Basic scoring simulation
        double baseScore = Math.min(submission.getMaxScore() * 0.6, submission.getMaxScore());
        double contentBonus = Math.min(content.length() / 100.0, submission.getMaxScore() * 0.3);
        double randomFactor = (Math.random() - 0.5) * (submission.getMaxScore() * 0.2);

        return Math.max(0, Math.min(submission.getMaxScore(), baseScore + contentBonus + randomFactor));
    }

    private double calculateAIConfidence(Submission submission) {
        // Simulate confidence based on content quality indicators
        String content = submission.getTextContent();
        if (content == null || content.trim().isEmpty()) {
            return 0.3; // Low confidence for empty content
        }

        // Basic confidence calculation
        double lengthFactor = Math.min(1.0, content.length() / 500.0); // Confidence increases with length
        double structureFactor = content.contains(".") && content.contains(" ") ? 0.8 : 0.5;
        double randomVariation = 0.1 + (Math.random() * 0.2); // 0.1 - 0.3

        return Math.min(0.95, lengthFactor * structureFactor + randomVariation);
    }

    private String generateAIFeedback(Submission submission, double score) {
        double percentage = (score / submission.getMaxScore()) * 100;

        if (percentage >= 90) {
            return "Excellent work! Your submission demonstrates a thorough understanding of the topic with clear explanations and well-structured content.";
        } else if (percentage >= 80) {
            return "Good work! Your submission shows solid understanding. Consider expanding on key points and providing more detailed explanations.";
        } else if (percentage >= 70) {
            return "Satisfactory work. Your submission covers the basics but could benefit from more depth and better organization of ideas.";
        } else if (percentage >= 60) {
            return "Your submission shows some understanding but needs significant improvement. Focus on addressing all requirements and providing clearer explanations.";
        } else {
            return "This submission requires major revision. Please review the assignment requirements and seek additional help to improve your understanding.";
        }
    }

    private Map<String, Double> generateRubricScores(Submission submission) {
        Map<String, Double> rubricScores = new HashMap<>();

        // Simulate rubric scoring for common criteria
        if (submission.getAssignment().getRubric() != null && !submission.getAssignment().getRubric().isEmpty()) {
            for (Assignment.RubricCriterion criterion : submission.getAssignment().getRubric()) {
                double score = Math.random() * criterion.getMaxPoints();
                rubricScores.put(criterion.getName(), score);
            }
        } else {
            // Default rubric criteria
            rubricScores.put("Content Quality", Math.random() * 25);
            rubricScores.put("Organization", Math.random() * 25);
            rubricScores.put("Grammar & Style", Math.random() * 25);
            rubricScores.put("Requirements Met", Math.random() * 25);
        }

        return rubricScores;
    }

    private String calculateLetterGrade(double percentage) {
        if (percentage >= 97) return "A+";
        if (percentage >= 93) return "A";
        if (percentage >= 90) return "A-";
        if (percentage >= 87) return "B+";
        if (percentage >= 83) return "B";
        if (percentage >= 80) return "B-";
        if (percentage >= 77) return "C+";
        if (percentage >= 73) return "C";
        if (percentage >= 70) return "C-";
        if (percentage >= 67) return "D+";
        if (percentage >= 63) return "D";
        if (percentage >= 60) return "D-";
        return "F";
    }
}
