package com.nichenetwork.nichenetwork_backend.post;

import com.nichenetwork.nichenetwork_backend.community.Community;
import com.nichenetwork.nichenetwork_backend.community.CommunityRepository;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMember;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMemberRepository;
import com.nichenetwork.nichenetwork_backend.enums.CommunityRole;
import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
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
    private final UserRepository userRepository;
    private final CommunityMemberRepository communityMemberRepository;

    @Transactional
    public PostResponse createPost(@RequestBody PostRequest request) {
        Community community = communityRepository.findById(request.getCommunityId()).orElseThrow(() -> new EntityNotFoundException("Community not found with id " + request.getCommunityId()));

        Post post = new Post();
        BeanUtils.copyProperties(request, post);
        postRepository.save(post);
        PostResponse response = responseFromEntity(post);
        return response;
    }

    @Transactional
    public PostResponse updatePost(Long id, @RequestBody PostRequest request) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with id " + id));
        BeanUtils.copyProperties(request, post);
        postRepository.save(post);

        PostResponse response = responseFromEntity(post);
        return response;
    }

    public Page<PostResponse> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        Page<PostResponse> response = posts.map(this::responseFromEntity);
        return response;
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with id " + id));
        PostResponse response = responseFromEntity(post);
        return response;
    }

    //recuperare tutti i post di un utente specifico
    public Page<PostResponse> getAllPostsByUserId(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        if (posts.isEmpty()) {
            return null;
        }
        Page<PostResponse> response = posts.map(this::responseFromEntity);
        return response;
    }

    //recuperare tutti i post di una community specifica
    public Page<PostResponse> getAllPostsByCommunityId(Long communityId, Pageable pageable) {
        Page<Post> posts = postRepository.findByCommunityIdOrderByCreatedAtDesc(communityId, pageable);
        if (posts.isEmpty()) {
            return null;
        }
        Page<PostResponse> response = posts.map(this::responseFromEntity);
        return response;
    }

    @Transactional
    public String deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with id " + id));
        postRepository.delete(post);
        return "Post deleted successfully";
    }

    @Transactional
    public void deletePostAsModerator(Long moderatorId, Long postId) {
        User moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new EntityNotFoundException("Moderator not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        Community community = post.getCommunity();

        CommunityMember moderatorMember = communityMemberRepository.findByUserAndCommunity(moderator, community)
                .orElseThrow(() -> new IllegalStateException("You are not a member of this community"));

        if (moderatorMember.getRole() != CommunityRole.OWNER && moderatorMember.getRole() != CommunityRole.MODERATOR) {
            throw new IllegalStateException("Only the owner or a moderator can delete posts");
        }

        postRepository.delete(post);
    }


    //metodi aggiuntivi
    public PostResponse responseFromEntity(Post post) {
        PostResponse response = new PostResponse();
        BeanUtils.copyProperties(post, response);
        return response;
    }

}
