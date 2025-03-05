package com.nichenetwork.nichenetwork_backend.post;

import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody @Valid PostRequest request) {
        PostResponse response = postService.createPost(request).getBody();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#appUser.username == #username")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostRequest request, @AuthenticationPrincipal AppUser appUser, @RequestParam String username) {
        PostResponse response = postService.updatePost(id, request).getBody();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(Pageable pageable) {
        return postService.getAllPosts(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponse>> getAllPostsByUserId(@PathVariable Long userId, Pageable pageable) {
        return postService.getAllPostsByUserId(userId, pageable);
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<Page<PostResponse>> getAllPostsByCommunityId(@PathVariable Long communityId, Pageable pageable) {
        return postService.getAllPostsByCommunityId(communityId, pageable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#appUser.username == #username or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deletePost(@PathVariable Long id, @AuthenticationPrincipal AppUser appUser, @RequestParam String username) {
        return postService.deletePost(id);
    }

    @DeleteMapping("/delete-by-moderator/{moderatorId}/{postId}")
    public ResponseEntity<String> deletePostAsModerator(
            @PathVariable Long moderatorId,
            @PathVariable Long postId) {
        postService.deletePostAsModerator(moderatorId, postId);
        return ResponseEntity.ok("Post deleted successfully by moderator");
    }
}
