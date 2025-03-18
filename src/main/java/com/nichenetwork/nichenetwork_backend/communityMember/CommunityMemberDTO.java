package com.nichenetwork.nichenetwork_backend.communityMember;

import com.nichenetwork.nichenetwork_backend.enums.CommunityRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityMemberDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private CommunityRole role;
    private String avatar;
}