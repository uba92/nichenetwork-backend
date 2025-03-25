package com.nichenetwork.nichenetwork_backend.like;

import com.nichenetwork.nichenetwork_backend.comment.Comment;
import com.nichenetwork.nichenetwork_backend.comment.CommentRepository;
import com.nichenetwork.nichenetwork_backend.enums.NotificationType;
import com.nichenetwork.nichenetwork_backend.notification.NotificationService;
import com.nichenetwork.nichenetwork_backend.post.Post;
import com.nichenetwork.nichenetwork_backend.post.PostRepository;
import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    @Transactional
    public void toggleLike(Long userId, Long postId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        User sender = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (likeRepository.existsByUserIdAndPostId(userId, postId)) {

            likeRepository.deleteByUserIdAndPostId(userId, postId);
        } else {

            Like like = new Like();
            like.setPost(post);
            like.setUser(sender);
            likeRepository.save(like);

            if(!sender.getId().equals(post.getUser().getId())) {
                String communityName = post.getCommunity().getName();
                String message = sender.getUsername() + " ha messo un like al tuo post nella community: " + communityName;
                notificationService.createNotification(
                        post.getUser(),
                        sender,
                        message,
                        NotificationType.LIKE,
                        post
                );
            }
        }
    }



    public int countLikesOnPost(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    @Transactional
    public void likeComment(Long userId, Long commentId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (likeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            throw new EntityExistsException("Like already exists");
        }

        Like like = new Like();
        like.setUser(user);
        like.setComment(comment);
        likeRepository.save(like);
    }

    @Transactional
    public void unlikeComment(Long userId, Long commentId) {
        if(!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        if (!likeRepository.existsByUserIdAndCommentId(userId, commentId)) {
            throw new EntityNotFoundException("Like not found");
        }
        likeRepository.deleteByUserIdAndCommentId(userId, commentId);
    }

    public int countLikesOnComment(Long commentId) {
        return likeRepository.countByCommentId(commentId);
    }
}
