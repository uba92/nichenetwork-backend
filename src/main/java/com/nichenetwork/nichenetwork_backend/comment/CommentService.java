package com.nichenetwork.nichenetwork_backend.comment;

import com.nichenetwork.nichenetwork_backend.community.Community;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMember;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMemberRepository;
import com.nichenetwork.nichenetwork_backend.enums.CommunityRole;
import com.nichenetwork.nichenetwork_backend.enums.NotificationType;
import com.nichenetwork.nichenetwork_backend.notification.NotificationService;
import com.nichenetwork.nichenetwork_backend.post.Post;
import com.nichenetwork.nichenetwork_backend.post.PostRepository;
import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import com.nichenetwork.nichenetwork_backend.user.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final NotificationService notificationService;

    @Transactional
    public Comment createComment(Long userId, Long postId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);

        commentRepository.save(comment);

        if(!user.getId().equals(post.getUser().getId())) {
            String communityName = post.getCommunity().getName();
            String message = user.getUsername() + " ha commentato un tuo post nella community: " + communityName;

            notificationService.createNotification(
                    post.getUser(),
                    user,
                    message,
                    NotificationType.COMMENT,
                    post
            );
        }

        return comment;
    }

    @Transactional
    public void updateComment(Long userId, Long commentId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only edit your own comments");
        }

        comment.setContent(newContent);
        commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own comments");
        }

        System.out.println("✅ Eliminazione in corso...");

        commentRepository.deleteByIdCustom(commentId); // 🔥 Forza la DELETE nel DB

        System.out.println("🗑️ Commento eliminato con successo!");
    }

    @Transactional
    public void deleteCommentAsModerator(Long moderatorId, Long commentId) {
        User moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new EntityNotFoundException("Moderator not found"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        Community community = comment.getPost().getCommunity();

        CommunityMember moderatorMember = communityMemberRepository.findByUserAndCommunity(moderator, community)
                .orElseThrow(() -> new IllegalStateException("You are not a member of this community"));

        // ✅ Solo OWNER e MODERATOR possono eliminare commenti
        if (moderatorMember.getRole() != CommunityRole.OWNER && moderatorMember.getRole() != CommunityRole.MODERATOR) {
            throw new IllegalStateException("Only the owner or a moderator can delete comments");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponse> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        List<Comment> comments = commentRepository.findByPost(post);
        return comments.stream()
                .map(this::responseFromEntity)
                .toList();
    }

    public CommentResponse responseFromEntity(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                new UserResponse(
                        comment.getUser().getId(),
                        comment.getUser().getUsername(),
                        comment.getUser().getAvatar(),
                        comment.getUser().getFirstName(),
                        comment.getUser().getLastName(),
                        comment.getUser().getBio(),
                        comment.getUser().getCreatedAt(),
                        comment.getUser().getEmail()
                )
        );
    }
}
