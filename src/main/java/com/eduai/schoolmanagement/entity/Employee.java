package com.eduai.schoolmanagement.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Document(collection = "employees")
public class Employee extends BaseEntity {

    @NotBlank(message = "Employee ID is required")
    @Indexed(unique = true)
    private String employeeId;

    @Valid
    @NotNull(message = "Personal information is required")
    private PersonalInfo personalInfo;

    @Valid
    @NotNull(message = "Employment information is required")
    private EmploymentInfo employmentInfo;

    @Valid
    private Qualifications qualifications;

    @Valid
    private Documents documents;

    // Login Credentials
    private LoginCredentials loginCredentials;

    // Status
    private String status; // ACTIVE, INACTIVE, ON_LEAVE, TERMINATED

    @Data
    public static class PersonalInfo {
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
        private String emergencyContact;
        private String emergencyPhone;
    }

    @Data
    public static class EmploymentInfo {
        @NotBlank(message = "Department is required")
        private String department;

        @NotBlank(message = "Position is required")
        private String position;

        @NotBlank(message = "Employment type is required")
        private String employmentType; // FULL_TIME, PART_TIME, CONTRACT, SUBSTITUTE, GUEST_FACULTY, CONSULTANT

        private LocalDate joinDate;
        private Double salary;
        private String workLocation;
        private String reportingManager;
    }

    @Data
    public static class Qualifications {
        private String education;
        private String certifications;
        private String experience;
        private List<String> skills;
        private List<String> languages;
    }

    @Data
    public static class Documents {
        private String resume;
        private String idProof;
        private String addressProof;
        private List<String> certificates;
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
}
