package com.nichenetwork.nichenetwork_backend.post;

import com.nichenetwork.nichenetwork_backend.community.Community;
import com.nichenetwork.nichenetwork_backend.community.CommunityRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;

    @Transactional
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        Community community = communityRepository.findById(request.getCommunityId()).orElseThrow(() -> new EntityNotFoundException("Community not found with id " + request.getCommunityId()));

        Post post = new Post();
        BeanUtils.copyProperties(request, post);
        postRepository.save(post);
        PostResponse response = responseFromEntity(post);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<PostResponse> updatePost(Long id, @RequestBody PostRequest request) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with id " + id));
        BeanUtils.copyProperties(request, post);
        postRepository.save(post);

        PostResponse response = responseFromEntity(post);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Page<PostResponse>> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        Page<PostResponse> response = posts.map(this::responseFromEntity);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<PostResponse> getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with id " + id));
        PostResponse response = responseFromEntity(post);
        return ResponseEntity.ok(response);
    }

    //recuperare tutti i post di un utente specifico
    public ResponseEntity<Page<PostResponse>> getAllPostsByUserId(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        if (posts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Page<PostResponse> response = posts.map(this::responseFromEntity);
        return ResponseEntity.ok(response);
    }

    //recuperare tutti i post di una community specifica
    public ResponseEntity<Page<PostResponse>> getAllPostsByCommunityId(Long communityId, Pageable pageable) {
        Page<Post> posts = postRepository.findByCommunityIdOrderByCreatedAtDesc(communityId, pageable);
        if (posts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Page<PostResponse> response = posts.map(this::responseFromEntity);
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<String> deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with id " + id));
        postRepository.delete(post);
        return ResponseEntity.ok("Post deleted successfully");
    }

    //metodi aggiuntivi
    public PostResponse responseFromEntity(Post post) {
        PostResponse response = new PostResponse();
        BeanUtils.copyProperties(post, response);
        return response;
    }

}
