package com.nichenetwork.nichenetwork_backend.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    //like ai post
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    int countByPostId(Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);

    //like ai commenti
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
    int countByCommentId(Long commentId);
    void deleteByUserIdAndCommentId(Long userId, Long commentId);

    void deleteByPostId(Long id);
}
