package com.nichenetwork.nichenetwork_backend.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @Size(max = 255, message = "Bio must be at most 255 characters long")
    private String bio;
    @Size(max = 255, message = "Avatar must be at most 255 characters long")
    private String avatar;
}
