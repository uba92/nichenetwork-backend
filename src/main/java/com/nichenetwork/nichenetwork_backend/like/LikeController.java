package com.nichenetwork.nichenetwork_backend.like;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final LikeRepository likeRepository;

    @PostMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<String> toggleLikePost(@PathVariable Long postId, @PathVariable Long userId) {
        likeService.toggleLike(userId, postId);
        return ResponseEntity.status(HttpStatus.OK).body("Like toggled successfully");
    }

    //aggiungere like ai commenti
    @PostMapping("/comment/{commentId}/user/{userId}")
    public ResponseEntity<String> likeComment(@PathVariable Long commentId, @PathVariable Long userId) {
        likeService.likeComment(commentId, userId);
        return ResponseEntity.status(HttpStatus.OK).body("Comment liked successfully");
    }

    //rimuovere like ai commenti
    @DeleteMapping("/comment/{commentId}/user/{userId}")
    public ResponseEntity<String> unlikeComment(@PathVariable Long commentId, @PathVariable Long userId) {
        likeService.unlikeComment(userId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body("Comment unliked successfully");
    }


    @GetMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<Boolean> hasUserLikedPost(@PathVariable Long postId, @PathVariable Long userId) {
        boolean liked = likeRepository.existsByUserIdAndPostId(userId, postId);
        return ResponseEntity.ok(liked);
    }


    //contare like ai post
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Integer> getPostLikes(@PathVariable Long postId) {
        int likeCount = likeService.countLikesOnPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(likeCount);
    }

    //contare like ai commenti
    @GetMapping("/comment/{commentId}/count")
    public ResponseEntity<Integer> getCommentLikes(@PathVariable Long commentId) {
        int likeCount = likeService.countLikesOnComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(likeCount);
    }


}
