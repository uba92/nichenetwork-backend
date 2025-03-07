package com.nichenetwork.nichenetwork_backend.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    @NotBlank(message = "Content is required")
    private String content;

    private String image;

    @NotNull(message = "Community ID is required")
    private Long communityId;
}
