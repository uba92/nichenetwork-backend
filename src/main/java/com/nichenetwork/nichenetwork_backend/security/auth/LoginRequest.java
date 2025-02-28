package com.nichenetwork.nichenetwork_backend.security.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
