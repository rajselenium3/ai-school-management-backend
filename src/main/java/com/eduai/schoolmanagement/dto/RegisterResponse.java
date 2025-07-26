package com.eduai.schoolmanagement.dto;

import com.eduai.schoolmanagement.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private User user;
    private String message;
    private boolean success;
}
