package com.eduai.schoolmanagement.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
public class User extends BaseEntity {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Indexed(unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotEmpty(message = "At least one role is required")
    private Set<Role> roles;

    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String nationality;

    // Account status fields (active inherited from BaseEntity)
    private boolean emailVerified = false;
    private boolean locked = false;

    // Email verification and password reset
    private String verificationCode;
    private LocalDateTime codeExpiryTime;
    private String passwordResetCode;
    private LocalDateTime resetCodeExpiryTime;

    // Profile information
    private String profilePicture;
    private String bio;
    private LocalDateTime lastLogin;
    private String lastLoginIp;

    // Preferences
    private String preferredLanguage = "en";
    private String timezone = "UTC";
    private boolean notificationsEnabled = true;

    public enum Role {
        ADMIN,
        TEACHER,
        STUDENT,
        PARENT,
        ACCOUNTANT
    }

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    public boolean isAccountActive() {
        return isActive() && !locked;
    }
}
