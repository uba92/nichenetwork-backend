package com.nichenetwork.nichenetwork_backend.post;

import com.nichenetwork.nichenetwork_backend.comment.CommentResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long id;
    private String content;
    private String image;
    private String username;
    private LocalDateTime createdAt;


    public PostResponse(Long id, String content, String image, String string, @NotBlank(message = "Username is required") String username, List<CommentResponse> collect) {
        this.id = id;
        this.content = content;
        this.image = image;
        this.username = username;
        this.createdAt = LocalDateTime.now();
    }
}
