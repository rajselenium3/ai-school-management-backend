package com.eduai.schoolmanagement.dto;

import com.eduai.schoolmanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String displayName;
    private Set<User.Role> roles;
    private boolean emailVerified;
    private LocalDateTime expiresAt;
    private String message;

    public LoginResponse(String token, User user, long expirationTime) {
        this.token = token;
        this.type = "Bearer";
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.displayName = user.getFirstName() + " " + user.getLastName();
        this.roles = user.getRoles();
        this.emailVerified = user.isEmailVerified();
        this.expiresAt = LocalDateTime.now().plusSeconds(expirationTime / 1000);
        this.message = "Login successful";
    }
}
