package com.nichenetwork.nichenetwork_backend.notification;

import com.nichenetwork.nichenetwork_backend.enums.NotificationType;
import com.nichenetwork.nichenetwork_backend.post.Post;
import com.nichenetwork.nichenetwork_backend.post.PostRepository;
import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void createNotification(User recipient, User sender, String message, NotificationType type, Post relatedPost) {

        recipient = userRepository.findById(recipient.getId())
                .orElseThrow(() -> new EntityNotFoundException("Recipient not found"));

        sender = userRepository.findById(sender.getId())
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        Post managedPost = null;
        if (relatedPost != null) {
            managedPost = postRepository.findById(relatedPost.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        }

        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setSender(sender);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedPost(managedPost);
        notification.setRead(false);

        notificationRepository.save(notification);
    }


    public List<NotificationDTO> getUnreadNotifications(User user) {

        List<Notification> notifications = notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);
        return notifications.stream().map(this::mapNotificationToDTO).collect(Collectors.toList());
    }

    public void markNotificationAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new EntityNotFoundException("Notifica non trovata"));


        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new AccessDeniedException("Non puoi modificare questa notifica");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void markAllNotificationsAsRead(User user) {
        List<Notification> notifications = notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    private NotificationDTO mapNotificationToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRecipientUsername(notification.getRecipient().getUsername());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());

        if (notification.getSender() != null) {
            dto.setSenderUsername(notification.getSender().getUsername());
        }
        if (notification.getRelatedPost() != null) {
            dto.setRelatedPostId(notification.getRelatedPost().getId());
        }
        return dto;
    }

}
