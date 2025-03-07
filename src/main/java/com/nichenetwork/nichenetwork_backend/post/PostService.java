package com.nichenetwork.nichenetwork_backend.post;

import com.nichenetwork.nichenetwork_backend.comment.Comment;
import com.nichenetwork.nichenetwork_backend.comment.CommentRepository;
import com.nichenetwork.nichenetwork_backend.comment.CommentResponse;
import com.nichenetwork.nichenetwork_backend.community.Community;
import com.nichenetwork.nichenetwork_backend.community.CommunityRepository;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMember;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMemberRepository;
import com.nichenetwork.nichenetwork_backend.enums.CommunityRole;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CommentRepository commentRepository;


//    public PostResponse createPost(PostRequest request, AppUser appUser) {
//        System.out.println("📌 Richiesta ricevuta nel Service: " + request);
//        System.out.println("📌 Utente autenticato: " + appUser.getEmail());
//
//        // Troviamo la community
//        Community community = communityRepository.findById(request.getCommunityId())
//                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + request.getCommunityId()));
//
//        // Troviamo l'utente dal DB per collegarlo al post
//        User user = userRepository.findByEmail(appUser.getEmail())
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//        // Creiamo il post e assegniamo i dati
//        Post post = new Post();
//        post.setContent(request.getContent());
//        post.setImage(request.getImage());
//        post.setCommunity(community);
//        post.setUser(user);
//
//        // Salviamo il post
//        postRepository.save(post);
//        System.out.println("✅ Post salvato con ID: " + post.getId());
//
//        return responseFromEntity(post);
//    }

    @Transactional
    public PostResponse createPost(PostRequest request, String userUsername) {

        System.out.println("Request per post: " + request);

        User user = userRepository.findByUsername(userUsername).orElseThrow(() -> new EntityNotFoundException("User not found"));

        Community community = communityRepository.findById(request.getCommunityId()).orElseThrow(() -> new EntityNotFoundException("Community not found with id " + request.getCommunityId()));
        Post post = new Post();
        post.setContent(request.getContent());
        post.setImage(request.getImage());
        post.setCommunity(community);
        post.setUser(user);


        System.out.println("Post creato: " + post);
        postRepository.save(post);

        System.out.println("✅ Post salvato con ID: " + post.getId());

        PostResponse response = responseFromEntity(post);

        System.out.println("Response del post " + response);
        return response;
    }

    @Transactional
    public PostResponse updatePost(Long id, PostRequest request) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with id " + id));
        BeanUtils.copyProperties(request, post);
        postRepository.save(post);

        PostResponse response = responseFromEntity(post);
        return response;
    }

    public Page<PostResponse> getAllPosts(int currentPage, int size, String sortBy) {
        Page<Post> posts = postRepository.findAll(PageRequest.of(currentPage, size, Sort.by(sortBy)));
        Page<PostResponse> response = posts.map(this::responseFromEntity);
        return response;
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with id " + id));
        PostResponse response = responseFromEntity(post);
        return response;
    }

//    recuperare tutti i post di un utente specifico
    public Page<PostResponse> getAllPostsByUserId(Long userId, int currentPage, int size, String sortBy) {
        Page<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(currentPage, size, Sort.by(sortBy)));
        if (posts.isEmpty()) {
            return null;
        }
        Page<PostResponse> response = posts.map(this::responseFromEntity);
        return response;
    }
//
//    recuperare tutti i post di una community specifica
    @Transactional
    public Page<PostResponse> getAllPostsByCommunityId(Long communityId, int currentPage, int size, String sortBy) {
        Page<Post> posts = postRepository.findByCommunityIdOrderByCreatedAtDesc(communityId, PageRequest.of(currentPage, size, Sort.by(sortBy)));
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

    public Page<CommentResponse> getCommentsByPostId(Long postId, int page, int size, String sortBy) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found with id " + postId));

        Page<Comment> comments = commentRepository.findByPostId(postId, PageRequest.of(page, size, Sort.by(sortBy)));

        return comments.map(this::commentResponseFromEntity);
    }

    private CommentResponse commentResponseFromEntity(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}
