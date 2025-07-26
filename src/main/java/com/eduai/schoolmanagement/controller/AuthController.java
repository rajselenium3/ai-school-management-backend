package com.eduai.schoolmanagement.controller;

import com.eduai.schoolmanagement.dto.LoginRequest;
import com.eduai.schoolmanagement.dto.LoginResponse;
import com.eduai.schoolmanagement.dto.RegisterRequest;
import com.eduai.schoolmanagement.dto.RegisterResponse;
import com.eduai.schoolmanagement.dto.PasswordChangeRequest;
import com.eduai.schoolmanagement.dto.PasswordResetRequest;
import com.eduai.schoolmanagement.dto.EmailVerificationRequest;
import com.eduai.schoolmanagement.dto.ForgotPasswordRequest;
import com.eduai.schoolmanagement.dto.ResetPasswordRequest;
import com.eduai.schoolmanagement.dto.VerifyCodeRequest;
import com.eduai.schoolmanagement.service.EmailService;
import com.eduai.schoolmanagement.entity.User;
import com.eduai.schoolmanagement.entity.Student;
import com.eduai.schoolmanagement.entity.Teacher;
import com.eduai.schoolmanagement.repository.UserRepository;
import com.eduai.schoolmanagement.repository.StudentRepository;
import com.eduai.schoolmanagement.repository.TeacherRepository;
import com.eduai.schoolmanagement.security.JwtUtils;
import com.eduai.schoolmanagement.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization operations")
@CrossOrigin(origins = "*")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Check if user exists
            Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User not found with email: " + loginRequest.getEmail()));
            }

            User user = userOpt.get();

            // Check if user is active
            if (!user.isActive()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User account is inactive"));
            }

            // Check if user is locked
            if (user.isLocked()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User account is locked"));
            }

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Create response
            LoginResponse response = new LoginResponse(jwt, user, jwtUtils.getExpirationTime());

            log.info("User {} logged in successfully", loginRequest.getEmail());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login failed for user {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Invalid email or password"));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "User logged out successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user info")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "User not authenticated"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Optional<User> userOpt = userRepository.findByEmail(userDetails.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "User not found"));
        }

        User user = userOpt.get();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("email", user.getEmail());
        userInfo.put("firstName", user.getFirstName());
        userInfo.put("lastName", user.getLastName());
        userInfo.put("roles", user.getRoles());
        userInfo.put("emailVerified", user.isEmailVerified());

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token")
    public ResponseEntity<?> validateToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest()
                .body(Map.of("valid", false, "message", "Invalid token"));
        }

        return ResponseEntity.ok(Map.of("valid", true, "message", "Token is valid"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token")
    public ResponseEntity<?> refreshToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "User not authenticated"));
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String newToken = jwtUtils.generateTokenFromEmail(userDetails.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", newToken);
        response.put("type", "Bearer");
        response.put("expiresIn", jwtUtils.getExpirationTime());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/demo-login")
    @Operation(summary = "Demo login for testing")
    public ResponseEntity<?> demoLogin(@RequestParam String role) {
        try {
            String email;
            switch (role.toLowerCase()) {
                case "admin":
                    email = "admin@school.edu";
                    break;
                case "teacher":
                    email = "sarah.johnson@school.edu";
                    break;
                case "student":
                    email = "emma.thompson@school.edu";
                    break;
                default:
                    return ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid demo role"));
            }

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Demo user not found"));
            }

            User user = userOpt.get();
            String jwt = jwtUtils.generateTokenFromEmail(email);

            LoginResponse response = new LoginResponse(jwt, user, jwtUtils.getExpirationTime());

            log.info("Demo login for role {} successful", role);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Demo login failed for role {}: {}", role, e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Demo login failed"));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email is already taken!"));
            }

            // Validate password confirmation
            if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Passwords do not match!"));
            }

            // Create new user
            User user = new User();
            user.setFirstName(signUpRequest.getFirstName());
            user.setLastName(signUpRequest.getLastName());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(encoder.encode(signUpRequest.getPassword()));
            user.setRoles(signUpRequest.getRoles());
            user.setPhone(signUpRequest.getPhone());
            // Convert dateOfBirth string to LocalDate
            if (signUpRequest.getDateOfBirth() != null && !signUpRequest.getDateOfBirth().isEmpty()) {
                user.setDateOfBirth(java.time.LocalDate.parse(signUpRequest.getDateOfBirth()));
            }
            user.setGender(signUpRequest.getGender());
            user.setAddress(signUpRequest.getAddress());
            user.setActive(true);
            user.setEmailVerified(false);
            user.setLocked(false);

            User savedUser = userRepository.save(user);

            // Create role-specific profiles
            createRoleSpecificProfiles(savedUser, signUpRequest);

            RegisterResponse response = new RegisterResponse(
                savedUser,
                "User registered successfully!",
                true
            );

            log.info("User {} registered successfully with roles {}", savedUser.getEmail(), savedUser.getRoles());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Registration failed for email {}: {}", signUpRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change user password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeRequest request,
                                          Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User not authenticated"));
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Optional<User> userOpt = userRepository.findByEmail(userDetails.getEmail());

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User not found"));
            }

            User user = userOpt.get();

            // Verify current password
            if (!encoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Current password is incorrect"));
            }

            // Validate new password confirmation
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "New passwords do not match"));
            }

            // Update password
            user.setPassword(encoder.encode(request.getNewPassword()));
            userRepository.save(user);

            log.info("Password changed successfully for user {}", user.getEmail());
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));

        } catch (Exception e) {
            log.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Password change failed"));
        }
    }

//    @PostMapping("/forgot-password")
//    @Operation(summary = "Request password reset")
//    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
//        try {
//            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
//
//            if (userOpt.isEmpty()) {
//                // Don't reveal if email exists or not for security
//                return ResponseEntity.ok(Map.of(
//                    "message",
//                    "If the email exists, a password reset link has been sent"
//                ));
//            }
//
//            User user = userOpt.get();
//
//            // Generate password reset token (simplified for demo)
//            String resetToken = jwtUtils.generateTokenFromEmail(user.getEmail());
//
//            // In production, save the token with expiration and send email
//            // For now, we'll just log it
//            log.info("Password reset token for {}: {}", user.getEmail(), resetToken);
//
//            return ResponseEntity.ok(Map.of(
//                "message",
//                "If the email exists, a password reset link has been sent",
//                "resetToken", resetToken // Remove this in production
//            ));
//
//        } catch (Exception e) {
//            log.error("Password reset request failed: {}", e.getMessage());
//            return ResponseEntity.ok(Map.of(
//                "message",
//                "If the email exists, a password reset link has been sent"
//            ));
//        }
//    }

//    @PostMapping("/reset-password")
//    @Operation(summary = "Reset password with token")
//    public ResponseEntity<?> resetPassword(@RequestParam String token,
//                                         @RequestParam String newPassword,
//                                         @RequestParam String confirmPassword) {
//        try {
//            // Validate token
//            if (!jwtUtils.validateJwtToken(token)) {
//                return ResponseEntity.badRequest()
//                    .body(Map.of("message", "Invalid or expired reset token"));
//            }
//
//            // Validate password confirmation
//            if (!newPassword.equals(confirmPassword)) {
//                return ResponseEntity.badRequest()
//                    .body(Map.of("message", "Passwords do not match"));
//            }
//
//            String email = jwtUtils.getEmailFromJwtToken(token);
//            Optional<User> userOpt = userRepository.findByEmail(email);
//
//            if (userOpt.isEmpty()) {
//                return ResponseEntity.badRequest()
//                    .body(Map.of("message", "User not found"));
//            }
//
//            User user = userOpt.get();
//            user.setPassword(encoder.encode(newPassword));
//            userRepository.save(user);
//
//            log.info("Password reset successfully for user {}", user.getEmail());
//            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
//
//        } catch (Exception e) {
//            log.error("Password reset failed: {}", e.getMessage());
//            return ResponseEntity.badRequest()
//                .body(Map.of("message", "Password reset failed"));
//        }
//    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email address")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            if (!jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid or expired verification token"));
            }

            String email = jwtUtils.getEmailFromJwtToken(token);
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User not found"));
            }

            User user = userOpt.get();
            user.setEmailVerified(true);
            userRepository.save(user);

            log.info("Email verified successfully for user {}", user.getEmail());
            return ResponseEntity.ok(Map.of("message", "Email verified successfully"));

        } catch (Exception e) {
            log.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Email verification failed"));
        }
    }

    @GetMapping("/check-email")
    @Operation(summary = "Check if email exists")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userRepository.existsByEmail(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    private void createRoleSpecificProfiles(User user, RegisterRequest request) {
        try {
            if (user.getRoles().contains(User.Role.STUDENT)) {
                // Create student profile
                Student student = new Student();

                // Set user info
                Student.UserInfo userInfo = new Student.UserInfo();
                userInfo.setFirstName(user.getFirstName());
                userInfo.setLastName(user.getLastName());
                userInfo.setEmail(user.getEmail());
                userInfo.setPhone(user.getPhone());
                userInfo.setDateOfBirth(user.getDateOfBirth());
                userInfo.setGender(user.getGender());
                userInfo.setAddress(user.getAddress());
                student.setUser(userInfo);

                student.setStudentId(request.getStudentId() != null ? request.getStudentId() :
                    "STU" + System.currentTimeMillis());
                student.setGrade(request.getGrade());
                student.setSection(request.getSection());
                student.setEnrollmentDate(java.time.LocalDate.now());
                student.setAcademicStatus("ACTIVE");
                student.setCurrentGPA(0.0);
                student.setAttendanceRate(100.0);

                studentRepository.save(student);
                log.info("Student profile created for user {}", user.getEmail());
            }

            if (user.getRoles().contains(User.Role.TEACHER)) {
                // Create teacher profile
                Teacher teacher = new Teacher();

                // Set user info
                Teacher.UserInfo userInfo = new Teacher.UserInfo();
                userInfo.setFirstName(user.getFirstName());
                userInfo.setLastName(user.getLastName());
                userInfo.setEmail(user.getEmail());
                userInfo.setPhone(user.getPhone());
                userInfo.setDateOfBirth(user.getDateOfBirth());
                userInfo.setGender(user.getGender());
                userInfo.setAddress(user.getAddress());
                teacher.setUser(userInfo);

                teacher.setEmployeeId(request.getEmployeeId() != null ? request.getEmployeeId() :
                    "EMP" + System.currentTimeMillis());
                teacher.setDepartment(request.getDepartment());
                teacher.setJoiningDate(java.time.LocalDate.now());
                teacher.setEmploymentType("FULL_TIME");
                teacher.setPerformanceScore(85.0);
                teacher.setStudentRating(4.0);

                teacherRepository.save(teacher);
                log.info("Teacher profile created for user {}", user.getEmail());
            }

        } catch (Exception e) {
            log.error("Failed to create role-specific profile for user {}: {}", user.getEmail(), e.getMessage());
        }
    }

    // ====================
    // EMAIL VERIFICATION & PASSWORD RESET ENDPOINTS
    // ====================

    @PostMapping("/send-login-code")
    @Operation(summary = "Send 6-digit verification code for login")
    public ResponseEntity<?> sendLoginVerificationCode(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User not found with this email"));
            }

            User user = userOpt.get();

            // Generate 6-digit code
            String verificationCode = generateVerificationCode();

            // Store code temporarily (in real app, use Redis or database with expiration)
            user.setVerificationCode(verificationCode);
            user.setCodeExpiryTime(java.time.LocalDateTime.now().plusMinutes(5));
            userRepository.save(user);

            // Send email
            emailService.sendLoginVerificationCode(user.getEmail(), verificationCode, user.getFirstName());

            log.info("Login verification code sent to {}", request.getEmail());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Verification code sent to your email"
            ));

        } catch (Exception e) {
            log.error("Failed to send login verification code: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to send verification code"));
        }
    }

    @PostMapping("/verify-login-code")
    @Operation(summary = "Verify 6-digit login code")
    public ResponseEntity<?> verifyLoginCode(@Valid @RequestBody VerifyCodeRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User not found"));
            }

            User user = userOpt.get();

            // Check if code matches and hasn't expired
            if (user.getVerificationCode() == null ||
                !user.getVerificationCode().equals(request.getCode()) ||
                user.getCodeExpiryTime() == null ||
                user.getCodeExpiryTime().isBefore(java.time.LocalDateTime.now())) {

                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid or expired verification code"));
            }

            // Clear verification code
            user.setVerificationCode(null);
            user.setCodeExpiryTime(null);
            user.setEmailVerified(true);
            userRepository.save(user);

            log.info("Login verification successful for {}", request.getEmail());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Email verified successfully"
            ));

        } catch (Exception e) {
            log.error("Failed to verify login code: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Code verification failed"));
        }
    }

    @PostMapping("/resend-login-code")
    @Operation(summary = "Resend login verification code")
    public ResponseEntity<?> resendLoginVerificationCode(@Valid @RequestBody EmailVerificationRequest request) {
        return sendLoginVerificationCode(request);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                // Don't reveal if email exists for security
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "If your email exists in our system, you will receive a password reset code"
                ));
            }

            User user = userOpt.get();

            // Generate 6-digit reset code
            String resetCode = generateVerificationCode();

            // Store reset code temporarily
            user.setPasswordResetCode(resetCode);
            user.setResetCodeExpiryTime(java.time.LocalDateTime.now().plusMinutes(15)); // 15 minutes
            userRepository.save(user);

            // Send email
            emailService.sendPasswordResetCode(user.getEmail(), resetCode, user.getFirstName());

            log.info("Password reset code sent to {}", request.getEmail());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Password reset code sent to your email"
            ));

        } catch (Exception e) {
            log.error("Failed to send password reset code: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Failed to send reset code"));
        }
    }

    @PostMapping("/verify-reset-code")
    @Operation(summary = "Verify password reset code")
    public ResponseEntity<?> verifyResetCode(@Valid @RequestBody VerifyCodeRequest request) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User not found"));
            }

            User user = userOpt.get();

            // Check if reset code matches and hasn't expired
            if (user.getPasswordResetCode() == null ||
                !user.getPasswordResetCode().equals(request.getCode()) ||
                user.getResetCodeExpiryTime() == null ||
                user.getResetCodeExpiryTime().isBefore(java.time.LocalDateTime.now())) {

                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid or expired reset code"));
            }

            // Generate reset token for password change
            String resetToken = jwtUtils.generatePasswordResetToken(user.getEmail());

            log.info("Password reset code verified for {}", request.getEmail());
            return ResponseEntity.ok(Map.of(
                "success", true,
                "resetToken", resetToken,
                "message", "Reset code verified successfully"
            ));

        } catch (Exception e) {
            log.error("Failed to verify reset code: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Reset code verification failed"));
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with token")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            // Validate reset token
            if (!jwtUtils.validatePasswordResetToken(request.getResetToken())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid or expired reset token"));
            }

            String email = jwtUtils.getEmailFromPasswordResetToken(request.getResetToken());
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "User not found"));
            }

            User user = userOpt.get();

            // Update password
            user.setPassword(encoder.encode(request.getNewPassword()));
            user.setPasswordResetCode(null);
            user.setResetCodeExpiryTime(null);
            userRepository.save(user);

            // Send confirmation email
            emailService.sendPasswordResetConfirmation(user.getEmail(), user.getFirstName());

            log.info("Password reset successful for {}", email);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Password reset successfully"
            ));

        } catch (Exception e) {
            log.error("Failed to reset password: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Password reset failed"));
        }
    }

    @PostMapping("/google")
    @Operation(summary = "Google OAuth login")
    public ResponseEntity<?> googleLogin(@RequestHeader(value = "Authorization", required = false) String googleToken) {
        try {
            // In a real implementation, verify Google token here
            // For demo purposes, we'll simulate the process

            log.info("Google OAuth login attempt");

            // Simulate Google user data
            Map<String, Object> response = new HashMap<>();
            response.put("email", "googleuser@gmail.com");
            response.put("firstName", "Google");
            response.put("lastName", "User");
            response.put("displayName", "Google User");
            response.put("roles", new String[]{"STUDENT"});
            response.put("token", jwtUtils.generateTokenFromEmail("googleuser@gmail.com"));
            response.put("requiresEmailVerification", true);
            response.put("message", "Google login successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Google login failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Google login failed"));
        }
    }

    // ====================
    // UTILITY METHODS
    // ====================

    private String generateVerificationCode() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }
}
