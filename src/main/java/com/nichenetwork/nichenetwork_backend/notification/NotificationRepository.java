package com.nichenetwork.nichenetwork_backend.notification;

import com.nichenetwork.nichenetwork_backend.post.Post;
import com.nichenetwork.nichenetwork_backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    List<Notification> findByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.recipient.id = :userId")
    void deleteByRecipientId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.sender.id = :userId")
    void deleteBySenderId(@Param("userId") Long userId);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    void deleteByRelatedPost(Post post);

}
