package com.eduai.schoolmanagement.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "submissions")
public class Submission extends BaseEntity {

    @DBRef
    @NotNull(message = "Assignment reference is required")
    private Assignment assignment;

    @DBRef
    @NotNull(message = "Student reference is required")
    private Student student;

    // Submission content
    private String textContent;
    private List<String> attachments; // File paths/URLs
    private Map<String, Object> submissionData; // Flexible data structure

    // Submission metadata
    private LocalDateTime submittedAt;
    private LocalDateTime lastModifiedAt;
    private String status; // DRAFT, SUBMITTED, LATE, GRADED, RETURNED

    // Grading information
    private Double score;
    private Double maxScore;
    private Double percentage;
    private String letterGrade;
    private String feedback;
    private LocalDateTime gradedAt;

    // AI Grading
    private AIGrading aiGrading;

    // Submission attempts and versioning
    private int attemptNumber;
    private boolean isLatestAttempt;
    private List<String> previousSubmissionIds;

    // Plagiarism and integrity
    private PlagiarismCheck plagiarismCheck;

    // Analytics and insights
    private SubmissionAnalytics analytics;

    @Data
    public static class AIGrading {
        private boolean aiGraded;
        private Double aiScore;
        private Double confidence;
        private String aiRecommendation;
        private String aiFeedback;
        private Map<String, Double> rubricScores; // Criterion name -> score
        private boolean humanReviewed;
        private String humanReviewComments;
        private LocalDateTime aiGradedAt;
        private String aiModel; // Which AI model was used
        private Map<String, Object> aiMetadata; // Additional AI processing data
    }

    @Data
    public static class PlagiarismCheck {
        private boolean checked;
        private Double similarityScore;
        private String status; // PASSED, FLAGGED, REVIEW_REQUIRED
        private List<PlagiarismMatch> matches;
        private LocalDateTime checkedAt;
        private String checkingService; // TURNITIN, COPYLEAKS, etc.
    }

    @Data
    public static class PlagiarismMatch {
        private String sourceUrl;
        private String sourceTitle;
        private Double similarityPercentage;
        private String matchedText;
        private String sourceType; // WEB, STUDENT_PAPER, JOURNAL, etc.
    }

    @Data
    public static class SubmissionAnalytics {
        private LocalDateTime timeStarted; // When student started working
        private Long timeSpent; // Total time spent in minutes
        private int revisionCount; // Number of edits/revisions
        private LocalDateTime lastActivity;
        private Map<String, Object> behaviorMetrics; // Typing patterns, etc.
        private String submissionDevice; // DESKTOP, MOBILE, TABLET
        private String browserInfo;
        private String ipAddress;
    }

    // Helper methods
    public boolean isSubmitted() {
        return "SUBMITTED".equals(status) || "LATE".equals(status) || "GRADED".equals(status);
    }

    public boolean isLate() {
        return submittedAt != null && assignment != null && assignment.getDueDate() != null
               && submittedAt.isAfter(assignment.getDueDate());
    }

    public boolean isGraded() {
        return score != null || (aiGrading != null && aiGrading.isAiGraded());
    }

    public Double getFinalScore() {
        if (score != null) {
            return score; // Human-graded score takes precedence
        }
        if (aiGrading != null && aiGrading.getAiScore() != null) {
            return aiGrading.getAiScore();
        }
        return null;
    }

    public String getFinalFeedback() {
        if (feedback != null && !feedback.trim().isEmpty()) {
            return feedback; // Human feedback takes precedence
        }
        if (aiGrading != null && aiGrading.getAiFeedback() != null) {
            return aiGrading.getAiFeedback();
        }
        return null;
    }
}
