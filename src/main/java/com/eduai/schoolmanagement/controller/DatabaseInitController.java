package com.eduai.schoolmanagement.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eduai.schoolmanagement.entity.User;
import com.eduai.schoolmanagement.repository.UserRepository;

@RestController
@RequestMapping("/init")
@CrossOrigin(origins = "*")
public class DatabaseInitController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/demo-users")
    public ResponseEntity<?> createDemoUsers() {
        try {
            // Check if users already exist
            if (userRepository.findByEmail("admin@school.edu").isPresent()) {
                return ResponseEntity.ok(Map.of("message", "Demo users already exist"));
            }

            // Create Admin User
            User admin = new User();
            admin.setFirstName("John");
            admin.setLastName("Admin");
            admin.setEmail("admin@school.edu");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhone("1234567890");
            admin.setRoles(new HashSet<>(Arrays.asList(User.Role.ADMIN)));
            admin.setActive(true);
            admin.setEmailVerified(true);
            userRepository.save(admin);

            // Create Teacher User
            User teacher = new User();
            teacher.setFirstName("Sarah");
            teacher.setLastName("Johnson");
            teacher.setEmail("sarah.johnson@school.edu");
            teacher.setPassword(passwordEncoder.encode("teacher123"));
            teacher.setPhone("1234567891");
            teacher.setRoles(new HashSet<>(Arrays.asList(User.Role.TEACHER)));
            teacher.setActive(true);
            teacher.setEmailVerified(true);
            userRepository.save(teacher);

            // Create Student User
            User student = new User();
            student.setFirstName("Emma");
            student.setLastName("Thompson");
            student.setEmail("emma.thompson@school.edu");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setPhone("1234567892");
            student.setRoles(new HashSet<>(Arrays.asList(User.Role.STUDENT)));
            student.setActive(true);
            student.setEmailVerified(true);
            userRepository.save(student);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Demo users created successfully!");
            response.put("users", Arrays.asList(
                Map.of("email", "admin@school.edu", "password", "admin123", "role", "ADMIN"),
                Map.of("email", "sarah.johnson@school.edu", "password", "teacher123", "role", "TEACHER"),
                Map.of("email", "emma.thompson@school.edu", "password", "student123", "role", "STUDENT")
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to create demo users: " + e.getMessage()));
        }
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createCustomAdmin(@RequestBody Map<String, String> adminData) {
        try {
            String email = adminData.get("email");
            String password = adminData.get("password");
            String firstName = adminData.get("firstName");
            String lastName = adminData.get("lastName");

            // Validate input
            if (email == null || password == null || firstName == null || lastName == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "All fields are required: email, password, firstName, lastName"));
            }

            // Check if user already exists
            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "User with this email already exists"));
            }

            // Create Admin User
            User admin = new User();
            admin.setFirstName(firstName);
            admin.setLastName(lastName);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setPhone("0000000000");
            admin.setRoles(new HashSet<>(Arrays.asList(User.Role.ADMIN)));
            admin.setActive(true);
            admin.setEmailVerified(true);
            userRepository.save(admin);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin user created successfully!");
            response.put("admin", Map.of(
                "email", email,
                "firstName", firstName,
                "lastName", lastName,
                "role", "ADMIN"
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to create admin: " + e.getMessage()));
        }
    }

    @GetMapping("/check-users")
    public ResponseEntity<?> checkUsers() {
        Map<String, Object> response = new HashMap<>();

        response.put("totalUsers", userRepository.count());
        response.put("adminExists", userRepository.findByEmail("admin@school.edu").isPresent());
        response.put("teacherExists", userRepository.findByEmail("sarah.johnson@school.edu").isPresent());
        response.put("studentExists", userRepository.findByEmail("emma.thompson@school.edu").isPresent());

        return ResponseEntity.ok(response);
    }
}
