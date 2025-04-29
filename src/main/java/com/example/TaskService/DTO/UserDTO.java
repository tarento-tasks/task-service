package com.example.TaskService.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String role;

    public UserDTO(String email, String role) {
        this.email = email;
        this.role = role;
    }
}