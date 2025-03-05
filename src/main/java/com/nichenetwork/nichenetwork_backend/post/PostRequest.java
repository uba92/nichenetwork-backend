package com.nichenetwork.nichenetwork_backend.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 255, message = "Content must be at most 255 characters long")
    private String content;

    private String image;

//    @NotNull(message = "Community ID is required")
    private Long communityId;
}
