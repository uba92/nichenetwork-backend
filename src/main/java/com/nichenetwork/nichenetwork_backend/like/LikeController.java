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

    //aggiungere like ai post
    @PostMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<String> likePost(@PathVariable Long postId, @PathVariable Long userId) {
        likeService.likePost(userId, postId);
        return ResponseEntity.status(HttpStatus.OK).body("Post liked successfully");
    }

    //aggiungere like ai commenti
    @PostMapping("/comment/{commentId}/user/{userId}")
    public ResponseEntity<String> likeComment(@PathVariable Long commentId, @PathVariable Long userId) {
        likeService.likeComment(commentId, userId);
        return ResponseEntity.status(HttpStatus.OK).body("Comment liked successfully");
    }

    //rimuovere like ai post
    @DeleteMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<String> unlikePost(@PathVariable Long postId, @PathVariable Long userId) {
        likeService.unlikePost(userId, postId);
        return ResponseEntity.status(HttpStatus.OK).body("Post unliked successfully");
    }

    //rimuovere like ai commenti
    @DeleteMapping("/comment/{commentId}/user/{userId}")
    public ResponseEntity<String> unlikeComment(@PathVariable Long commentId, @PathVariable Long userId) {
        likeService.unlikeComment(userId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body("Comment unliked successfully");
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
