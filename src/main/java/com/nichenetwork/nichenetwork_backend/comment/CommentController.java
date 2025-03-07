package com.nichenetwork.nichenetwork_backend.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long postId,
            @PathVariable Long userId,
            @RequestParam String content) {
        return ResponseEntity.ok(commentService.createComment(userId, postId, content));
    }

    @PutMapping("/{commentId}/user/{userId}")
    public ResponseEntity<String> updateComment(
            @PathVariable Long commentId,
            @PathVariable Long userId,
            @RequestParam String newContent) {
        commentService.updateComment(userId, commentId, newContent);
        return ResponseEntity.ok("Comment updated successfully");
    }

    @DeleteMapping("/{commentId}/user/{userId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    @DeleteMapping("/delete-by-moderator/{moderatorId}/{commentId}")
    public ResponseEntity<String> deleteCommentAsModerator(
            @PathVariable Long moderatorId,
            @PathVariable Long commentId) {
        commentService.deleteCommentAsModerator(moderatorId, commentId);
        return ResponseEntity.ok("Comment deleted successfully by moderator");
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId));
    }
}
