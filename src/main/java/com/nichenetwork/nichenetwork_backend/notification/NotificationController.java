package com.nichenetwork.nichenetwork_backend.notification;

import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public List<NotificationDTO> getUnreadNotifications(@AuthenticationPrincipal AppUser appUser) {
        User user = userRepository.findById(appUser.getId()).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + appUser.getId()));
        return notificationService.getUnreadNotifications(user);
    }

    @PutMapping("/markAsRead/{notificationId}")
    public void markNotificationAsRead(@AuthenticationPrincipal AppUser appUser, Long notificationId) {
        User user = userRepository.findById(appUser.getId()).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + appUser.getId()));
        notificationService.markNotificationAsRead(notificationId, user);
    }

    @PutMapping("/markAllAsRead")
    public void markAllNotificationsAsRead(@AuthenticationPrincipal AppUser appUser) {
        User user = userRepository.findById(appUser.getId()).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + appUser.getId()));
        notificationService.markAllNotificationsAsRead(user);
    }
}
