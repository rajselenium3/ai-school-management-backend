package com.eduai.schoolmanagement.entity;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "courses")
public class Course extends BaseEntity {

    @NotBlank(message = "Course code is required")
    @Indexed(unique = true)
    private String courseCode;

    @NotBlank(message = "Course name is required")
    private String courseName;

    private String description;
    private String department;
    private int credits;
    private String grade;
    private String semester;

    @Valid
    @NotNull(message = "Teacher information is required")
    private TeacherInfo teacher;

    @Valid
    private List<StudentInfo> enrolledStudents;

    private int capacity;
    private String schedule;
    private String classroom;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status; // ACTIVE, COMPLETED, UPCOMING, CANCELLED
    private boolean aiGradingEnabled;

    private CourseAnalytics analytics;

    @Data
    public static class CourseAnalytics {
        private double averageScore;
        private double completionRate;
        private int enrollmentCount;
        private double aiScore;
        private String performanceTrend;
        private LocalDate lastUpdated;
    }

    @Data
    public static class TeacherInfo {
        @NotBlank(message = "Teacher ID is required")
        private String teacherId;

        @NotBlank(message = "Teacher first name is required")
        private String firstName;

        @NotBlank(message = "Teacher last name is required")
        private String lastName;

        @NotBlank(message = "Teacher email is required")
        private String email;

        private String department;
        private String employeeId;
    }

    @Data
    public static class StudentInfo {
        @NotBlank(message = "Student ID is required")
        private String studentId;

        @NotBlank(message = "Student first name is required")
        private String firstName;

        @NotBlank(message = "Student last name is required")
        private String lastName;

        @NotBlank(message = "Student email is required")
        private String email;

        private String grade;
        private String section;
        private LocalDate enrollmentDate;
    }
}
