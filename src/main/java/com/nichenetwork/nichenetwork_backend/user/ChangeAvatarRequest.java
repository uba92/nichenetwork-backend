package com.nichenetwork.nichenetwork_backend.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeAvatarRequest {
    private String avatar;
}
