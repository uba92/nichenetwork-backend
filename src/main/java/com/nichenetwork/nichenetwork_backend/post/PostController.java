package com.nichenetwork.nichenetwork_backend.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nichenetwork.nichenetwork_backend.comment.CommentResponse;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(HttpServletRequest request) throws IOException {
        String rawBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        System.out.println("🔍 Raw request body: " + rawBody);



        ObjectMapper objectMapper = new ObjectMapper();
        PostRequest postRequest = objectMapper.readValue(rawBody, PostRequest.class);
        System.out.println("📌 PostRequest deserializzata: " + postRequest);

        String userUsername = request.getUserPrincipal().getName();
        System.out.println("📌 Utente autenticato che sta creando il post: " + userUsername);


        PostResponse response = postService.createPost(postRequest, userUsername);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @PostMapping("/create")
//    public ResponseEntity<PostResponse> createPost(
//            @Valid @RequestBody PostRequest request,
//            @AuthenticationPrincipal AppUser appUser) {
//
//        System.out.println("Request ricevuta dal controller: " + request);
//        if (appUser == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//        }
//
//        System.out.println("📌 Utente autenticato che sta creando il post: " + appUser.getEmail());
//
//        PostResponse response = postService.createPost(request, appUser);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    @PutMapping("/{id}")
    @PreAuthorize("#appUser.username == #username")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostRequest request, @AuthenticationPrincipal AppUser appUser, @RequestParam String username) {
        PostResponse response = postService.updatePost(id, request);
        return ResponseEntity.ok(response);
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
