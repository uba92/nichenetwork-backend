package com.nichenetwork.nichenetwork_backend.notification;

import com.nichenetwork.nichenetwork_backend.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;
    private NotificationType type;
    private String message;
    private Long relatedPostId;
    private String senderUsername;
    private String recipientUsername;
    private boolean isRead;
    private LocalDateTime createdAt;
}
