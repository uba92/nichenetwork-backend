package com.nichenetwork.nichenetwork_backend.user;

import com.nichenetwork.nichenetwork_backend.security.auth.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String avatar;
    private String firstName;
    private String lastName;
    private String bio;
    private LocalDateTime createdAt;
    private String email;
}
