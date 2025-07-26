package com.eduai.schoolmanagement.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "teachers")
public class Teacher extends BaseEntity {

    @NotBlank(message = "Employee ID is required")
    @Indexed(unique = true)
    private String employeeId;

    @Valid
    @NotNull(message = "User information is required")
    private UserInfo user;

    @NotBlank(message = "Department is required")
    private String department;

    private String designation;
    private LocalDate joiningDate;
    private String employmentType; // FULL_TIME, PART_TIME, CONTRACT, TEMPORARY
    private String qualifications;
    private String specialization;

    // Performance metrics
    private double performanceScore;
    private double studentRating;
    private int totalClassesAssigned;
    private int totalStudentsHandled;

    // Academic Information
    private List<String> subjectsHandled;
    private List<String> classesAssigned;
    private String experience;
    private List<String> certifications;

    // Contact and Personal Information
    private String emergencyContact;
    private String emergencyContactPhone;
    private String bloodGroup;
    private String maritalStatus;

    // Salary Information
    private SalaryInfo salaryInfo;

    // Login Credentials
    private LoginCredentials loginCredentials;

    // Teaching Schedule
    private Map<String, Object> weeklySchedule;
    private int maxClassesPerDay;
    private List<String> preferredTimeSlots;

    @Data
    public static class SalaryInfo {
        private double basicSalary;
        private double allowances;
        private double deductions;
        private double grossSalary;
        private double netSalary;
        private String salaryGrade;
        private LocalDate lastSalaryReview;
        private List<String> benefits;
    }

    @Data
    public static class LoginCredentials {
        private String username;
        private boolean hasAccount;
        private boolean isLocked;
        private LocalDateTime lastLogin;
        private String temporaryPassword;
        private LocalDateTime passwordResetDate;
        private List<String> loginHistory;
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
        private String nationality;
    }

    // Helper methods
    public String getFullName() {
        return user != null ? user.getFirstName() + " " + user.getLastName() : "";
    }

    public double getEffectiveRating() {
        return (performanceScore * 0.6) + (studentRating * 0.4);
    }

    public boolean isActiveEmployee() {
        return "FULL_TIME".equals(employmentType) || "PART_TIME".equals(employmentType);
    }
}
