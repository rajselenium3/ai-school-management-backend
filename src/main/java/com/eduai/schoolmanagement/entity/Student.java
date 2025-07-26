package com.eduai.schoolmanagement.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "students")
public class Student extends BaseEntity {

    @NotBlank(message = "Student ID is required")
    @Indexed(unique = true)
    private String studentId;

    @Valid
    @NotNull(message = "User information is required")
    private UserInfo user;

    @NotBlank(message = "Grade is required")
    private String grade;

    private String section;
    private String rollNumber;
    private LocalDate enrollmentDate;
    private String admissionNumber;

    // Parent Information
    @Valid
    private List<ParentInfo> parents;

    private String emergencyContact;
    private String emergencyContactPhone;

    // Academic Information
    private double currentGPA;
    private double attendanceRate;
    private String academicStatus; // ACTIVE, SUSPENDED, GRADUATED, DROPPED_OUT

    // AI Insights
    private AIInsights aiInsights;

    // Medical Information
    private MedicalInfo medicalInfo;

    @Data
    public static class AIInsights {
        private double riskScore;
        private String performanceTrend; // IMPROVING, STABLE, DECLINING
        private List<String> recommendations;
        private Map<String, Double> subjectPerformance;
        private LocalDate lastAnalysisDate;
    }

    @Data
    public static class MedicalInfo {
        private String bloodGroup;
        private List<String> allergies;
        private List<String> medications;
        private String specialNeeds;
        private String emergencyMedicalContact;
    }

    @Data
    public static class UserInfo {
        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotBlank(message = "Email is required")
        @Indexed(unique = true)
        private String email;

        private String phone;
        private LocalDate dateOfBirth;
        private String gender;
        private String address;
    }

    @Data
    public static class ParentInfo {
        @NotBlank(message = "Parent first name is required")
        private String firstName;

        @NotBlank(message = "Parent last name is required")
        private String lastName;

        @NotBlank(message = "Parent email is required")
        private String email;

        @NotBlank(message = "Parent phone is required")
        private String phone;

        @NotBlank(message = "Relationship is required")
        private String relationship; // FATHER, MOTHER, GUARDIAN, OTHER
    }

	public void setFamilyId(Object object) {
		// TODO Auto-generated method stub
		
	}
}
