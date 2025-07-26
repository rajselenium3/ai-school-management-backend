package com.eduai.schoolmanagement.entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "grades")
public class Grade extends BaseEntity {

    @DBRef
    @NotNull(message = "Student reference is required")
    private Student student;

    @DBRef
    @NotNull(message = "Course reference is required")
    private Course course;

    @DBRef
    @NotNull(message = "Assignment reference is required")
    private Assignment assignment;

    private double score;
    private double maxScore;
    private double percentage;
    private String letterGrade;

    private LocalDateTime submissionDate;
    private LocalDateTime gradedDate;

    private String feedback;
    private Map<String, Double> rubricScores;

    // AI Grading
    private AIGrading aiGrading;

    private String status; // PENDING, GRADED, REVIEWED

    @Data
    public static class AIGrading {
        private boolean aiGenerated;
        private double aiSuggestedScore;
        private double confidence;
        private String aiSuggestion;
        private String aiGeneratedFeedback;
        private boolean humanReviewed;
        private String reviewComments;
    }
}
