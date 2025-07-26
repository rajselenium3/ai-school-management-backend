package com.eduai.schoolmanagement.dto;

import com.eduai.schoolmanagement.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @NotEmpty(message = "At least one role is required")
    private Set<User.Role> roles;

    private String phone;
    private String dateOfBirth;
    private String gender;
    private String address;

    // Student-specific fields
    private String studentId;
    private String grade;
    private String section;

    // Teacher-specific fields
    private String employeeId;
    private String department;
}
