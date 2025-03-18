package com.nichenetwork.nichenetwork_backend.post;

import com.nichenetwork.nichenetwork_backend.cloudinary.CloudinaryService;
import com.nichenetwork.nichenetwork_backend.exceptions.UnauthorizedException;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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


        if (appUser == null) {
            throw new UnauthorizedException("Utente non autenticato");
        }


        PostRequest postRequest = new PostRequest();
        postRequest.setContent(content);
        postRequest.setCommunityId(communityId);


        String postImageUrl = null;
        if (file != null && !file.isEmpty()) {
            Map<String, Object> uploadResult = cloudinaryService.uploadImage(file);
            postImageUrl = (String) uploadResult.get("secure_url");
        }


        PostResponse response = postService.createPost(postRequest, appUser.getUsername(), postImageUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int currentPage,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @AuthenticationPrincipal AppUser appUser) {

        Page<PostResponse> response = postService.getAllPosts( appUser.getId(),currentPage, size, sortBy);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id, @AuthenticationPrincipal AppUser appUser) {
        PostResponse response = postService.getPostById(id, appUser.getId());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponse>> getAllPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int currentPage,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Page<PostResponse> response = postService.getAllPostsByUserId(userId, currentPage, size, sortBy);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/community/{communityId}")
    public ResponseEntity<Page<PostResponse>> getAllPostsByCommunityId(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "0") int currentPage,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @AuthenticationPrincipal AppUser appUser) {

        Page<PostResponse> response = postService.getAllPostsByCommunityId(communityId, currentPage, size, sortBy, appUser.getId());
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id, @AuthenticationPrincipal AppUser appUser) {
        postService.deletePost(id, appUser.getUsername());
        return ResponseEntity.ok("Post deleted successfully");
    }
}
