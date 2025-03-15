package com.nichenetwork.nichenetwork_backend.post;

import com.nichenetwork.nichenetwork_backend.cloudinary.CloudinaryService;
import com.nichenetwork.nichenetwork_backend.comment.CommentResponse;
import com.nichenetwork.nichenetwork_backend.exceptions.UnauthorizedException;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CloudinaryService cloudinaryService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @RequestParam("content") String content,
            @RequestParam("communityId") Long communityId,
            @RequestParam(value = "image", required = false) MultipartFile file,
            @AuthenticationPrincipal AppUser appUser) throws IOException {

        System.out.println("🔍 SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication());
        System.out.println("🔍 @AuthenticationPrincipal: " + appUser);

        if (appUser == null) {
            throw new UnauthorizedException("Utente non autenticato");
        }

        PostRequest postRequest = new PostRequest();
        postRequest.setContent(content);
        postRequest.setCommunityId(communityId);

        String postImageUrl = null;
        if (file != null && !file.isEmpty()) {
            Map uploadResult = cloudinaryService.uploadImage(file);
            postImageUrl = (String) uploadResult.get("secure_url");
        }

        PostResponse response = postService.createPost(postRequest, appUser.getUsername(), postImageUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(@RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy) {

        Page<PostResponse> response = postService.getAllPosts(currentPage, size, sortBy);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        PostResponse response = postService.getPostById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponse>> getAllPostsByUserId(@PathVariable Long userId, @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy) {
        Page<PostResponse> response = postService.getAllPostsByUserId(userId, currentPage, size, sortBy);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<Page<PostResponse>> getAllPostsByCommunityId(@PathVariable Long communityId, @RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy) {
        Page<PostResponse> response = postService.getAllPostsByCommunityId(communityId, currentPage, size, sortBy);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("{id}/comments")
    public ResponseEntity<Page<CommentResponse>> getCommentsByPostId(@PathVariable Long postId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "createdAt") String sortBy) {
        Page<CommentResponse> comments = postService.getCommentsByPostId(postId, page, size, "createdAt");
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("#appUser.username == #username or hasRole('ADMIN')")
    public ResponseEntity<String> deletePost(@PathVariable Long id, @AuthenticationPrincipal AppUser appUser, @RequestParam String username) {
         postService.deletePost(id);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @DeleteMapping("/delete-by-moderator/{moderatorId}/{postId}")
    public ResponseEntity<String> deletePostAsModerator(
            @PathVariable Long moderatorId,
            @PathVariable Long postId) {
        postService.deletePostAsModerator(moderatorId, postId);
        return ResponseEntity.ok("Post deleted successfully by moderator");
    }
}
