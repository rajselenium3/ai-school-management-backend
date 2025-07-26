package com.eduai.schoolmanagement.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "grade_levels")
public class GradeLevel extends BaseEntity {

    @NotBlank(message = "Grade code is required")
    @Indexed(unique = true)
    private String gradeCode; // e.g., "1", "2", "K", "Pre-K"

    @NotBlank(message = "Grade name is required")
    private String gradeName; // e.g., "Grade 1", "Kindergarten", "Pre-Kindergarten"

    @NotNull(message = "Grade level is required")
    @Min(value = 0, message = "Grade level must be 0 or positive")
    @Max(value = 20, message = "Grade level must be 20 or less")
    private Integer gradeLevel; // Numeric level for sorting, 0 for Pre-K, 1 for K, 2 for Grade 1, etc.

    private String description; // Optional description

    @NotNull(message = "Minimum age is required")
    @Min(value = 3, message = "Minimum age must be at least 3")
    private Integer minimumAge; // Minimum age for this grade

    @NotNull(message = "Maximum age is required")
    @Min(value = 4, message = "Maximum age must be at least 4")
    private Integer maximumAge; // Maximum age for this grade

    private Integer displayOrder; // Order for display in dropdowns

    // Static factory methods for common grade levels
    public static GradeLevel createPreK() {
        GradeLevel grade = new GradeLevel();
        grade.setGradeCode("Pre-K");
        grade.setGradeName("Pre-Kindergarten");
        grade.setGradeLevel(0);
        grade.setMinimumAge(3);
        grade.setMaximumAge(4);
        grade.setDisplayOrder(1);
        grade.setDescription("Pre-Kindergarten program for ages 3-4");
        grade.setActive(true);
        return grade;
    }

    public static GradeLevel createKindergarten() {
        GradeLevel grade = new GradeLevel();
        grade.setGradeCode("K");
        grade.setGradeName("Kindergarten");
        grade.setGradeLevel(1);
        grade.setMinimumAge(4);
        grade.setMaximumAge(6);
        grade.setDisplayOrder(2);
        grade.setDescription("Kindergarten program for ages 4-6");
        grade.setActive(true);
        return grade;
    }

    public static GradeLevel createElementaryGrade(int gradeNumber) {
        GradeLevel grade = new GradeLevel();
        grade.setGradeCode(String.valueOf(gradeNumber));
        grade.setGradeName("Grade " + gradeNumber);
        grade.setGradeLevel(gradeNumber + 1); // K is 1, so Grade 1 is 2
        grade.setMinimumAge(gradeNumber + 4);
        grade.setMaximumAge(gradeNumber + 6);
        grade.setDisplayOrder(gradeNumber + 2);
        grade.setDescription("Elementary Grade " + gradeNumber);
        grade.setActive(true);
        return grade;
    }

    public static GradeLevel createHighSchoolGrade(int gradeNumber) {
        GradeLevel grade = new GradeLevel();
        grade.setGradeCode(String.valueOf(gradeNumber));
        grade.setGradeName("Grade " + gradeNumber);
        grade.setGradeLevel(gradeNumber + 1);
        grade.setMinimumAge(gradeNumber + 4);
        grade.setMaximumAge(gradeNumber + 6);
        grade.setDisplayOrder(gradeNumber + 2);
        grade.setDescription("High School Grade " + gradeNumber);
        grade.setActive(true);
        return grade;
    }
}
