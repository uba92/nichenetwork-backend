package com.nichenetwork.nichenetwork_backend.community;

import com.nichenetwork.nichenetwork_backend.post.PostResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String imageUrl;
    private String color;
}