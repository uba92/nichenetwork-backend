package com.nichenetwork.nichenetwork_backend.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

//    @PostMapping
//    public ResponseEntity<PostResponse> createPost(
//            @RequestBody PostRequest request,
//            @AuthenticationPrincipal AppUser appUser) {
//
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
    public ResponseEntity<Page<PostResponse>> getAllPosts(Pageable pageable) {
        Page<PostResponse> response = postService.getAllPosts(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        PostResponse response = postService.getPostById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponse>> getAllPostsByUserId(@PathVariable Long userId, Pageable pageable) {
        Page<PostResponse> response = postService.getAllPostsByUserId(userId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<Page<PostResponse>> getAllPostsByCommunityId(@PathVariable Long communityId, Pageable pageable) {
        Page<PostResponse> response = postService.getAllPostsByCommunityId(communityId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#appUser.username == #username or hasRole('ADMIN')")
    public ResponseEntity<String> deletePost(@PathVariable Long id, @AuthenticationPrincipal AppUser appUser, @RequestParam String username) {
         postService.deletePost(id);
        return ResponseEntity.ok("Post deleted successfully");
    }

//    @DeleteMapping("/delete-by-moderator/{moderatorId}/{postId}")
//    public ResponseEntity<String> deletePostAsModerator(
//            @PathVariable Long moderatorId,
//            @PathVariable Long postId) {
//        postService.deletePostAsModerator(moderatorId, postId);
//        return ResponseEntity.ok("Post deleted successfully by moderator");
//    }
}
