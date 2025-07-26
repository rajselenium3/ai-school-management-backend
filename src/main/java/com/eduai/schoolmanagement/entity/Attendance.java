package com.eduai.schoolmanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "attendance")
public class Attendance extends BaseEntity {

    @Valid
    @NotNull(message = "Student information is required")
    private StudentInfo student;

    @Valid
    @NotNull(message = "Course information is required")
    private CourseInfo course;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Status is required")
    private String status; // PRESENT, ABSENT, LATE, EXCUSED

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private LocalDateTime markedAt;

    private String reason;
    private String notes;
    private String markedBy; // Teacher/Admin who marked attendance

    // AI Analytics
    private AttendanceAnalytics analytics;

    @Data
    public static class AttendanceAnalytics {
        private double weeklyRate;
        private double monthlyRate;
        private double semesterRate;
        private String absencePattern;
        private String riskLevel; // LOW, MEDIUM, HIGH
        private List<String> aiRecommendations;
        private LocalDateTime lastAnalyzed;
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
        private String rollNumber;
    }

    @Data
    public static class CourseInfo {
        @NotBlank(message = "Course code is required")
        private String courseCode;

        @NotBlank(message = "Course name is required")
        private String courseName;

        private String department;
        private String teacherName;
        private String classroom;
        private String schedule;
    }
}
