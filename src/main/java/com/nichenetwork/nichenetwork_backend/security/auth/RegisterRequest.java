package com.nichenetwork.nichenetwork_backend.security.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}
