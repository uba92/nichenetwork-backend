package com.nichenetwork.nichenetwork_backend.security.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username è obbligatorio")
    private String username;
    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Formato della email non valido")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "La password deve essere lunga almeno 8 caratteri")
    private String password;
    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;
    @NotBlank(message = "Il cognome é obbligatorio")
    private String lastName;
}
