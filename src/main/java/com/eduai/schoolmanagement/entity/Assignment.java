package com.eduai.schoolmanagement.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "assignments")
public class Assignment extends BaseEntity {

    @NotBlank(message = "Assignment title is required")
    private String title;

    private String description;

    @DBRef
    @NotNull(message = "Course reference is required")
    private Course course;

    private String type; // HOMEWORK, QUIZ, TEST, PROJECT, PARTICIPATION
    private double maxScore;
    private double weight;

    private LocalDateTime dueDate;
    private LocalDateTime submissionStartDate;

    private int submissions;
    private int graded;
    private double averageScore;

    private boolean aiGradingEnabled;
    private String instructions;
    private List<String> attachments;

    // Rubric for AI grading
    private List<RubricCriterion> rubric;

    @Data
    public static class RubricCriterion {
        private String name;
        private String description;
        private double maxPoints;
        private double weight;
        private Map<String, String> scoringGuide;
    }
}
