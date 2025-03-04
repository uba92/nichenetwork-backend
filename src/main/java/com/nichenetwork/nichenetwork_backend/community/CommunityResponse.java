package com.nichenetwork.nichenetwork_backend.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityResponse {

    private Long id;
    private String name;
    private String description;
    private String createdAt;  // Puoi formattarlo come preferisci

}
