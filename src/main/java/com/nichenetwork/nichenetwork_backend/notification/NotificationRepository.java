package com.nichenetwork.nichenetwork_backend.notification;

import com.nichenetwork.nichenetwork_backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);

    void deleteByRecipient_Id(Long userId);
    void deleteBySender_Id(Long userId);

}
