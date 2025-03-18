package com.nichenetwork.nichenetwork_backend.comment;

import com.nichenetwork.nichenetwork_backend.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private UserResponse author;
}
