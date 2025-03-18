package com.nichenetwork.nichenetwork_backend.post;

import com.nichenetwork.nichenetwork_backend.cloudinary.CloudinaryService;
import com.nichenetwork.nichenetwork_backend.comment.CommentRepository;
import com.nichenetwork.nichenetwork_backend.community.Community;
import com.nichenetwork.nichenetwork_backend.community.CommunityRepository;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMemberRepository;
import com.nichenetwork.nichenetwork_backend.like.LikeRepository;
import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import com.nichenetwork.nichenetwork_backend.user.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CommentRepository commentRepository;
    private final CloudinaryService cloudinaryService;
    private final LikeRepository likeRepository;

    @Transactional
    public PostResponse createPost(PostRequest request, String userUsername, String imageUrl) throws IOException {
        User user = userRepository.findByUsername(userUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));

        if (!communityMemberRepository.existsByUserAndCommunity(user, community)) {
            throw new EntityNotFoundException("User is not a member of this community");
        }

        Post post = new Post();
        post.setContent(request.getContent());
        post.setCommunity(community);
        post.setUser(user);
        post.setImage(getImageUrl(request.getImage(), imageUrl));

        postRepository.save(post);

        return responseFromEntity(post, user.getId());
    }

    private String getImageUrl(String imageUrl, String imageUrlFromRequest) throws IOException {
        if (imageUrl != null && !imageUrl.isBlank()) {
            return imageUrl;
        } else if (imageUrlFromRequest != null && !imageUrlFromRequest.isEmpty()) {
            // Upload da Cloudinary se necessario
            Map<String, Object> uploadResult = cloudinaryService.uploadImageFromUrl(imageUrlFromRequest);
            return (String) uploadResult.get("secure_url");
        }
        return null;
    }

    public Page<PostResponse> getAllPosts(Long currentUserId,int currentPage, int size, String sortBy) {
        Page<Post> posts = postRepository.findAll(PageRequest.of(currentPage, size, Sort.by(sortBy)));
        return posts.map(post -> responseFromEntity(post, currentUserId));
    }

    public PostResponse getPostById(Long id, Long currentUserId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return responseFromEntity(post, currentUserId);
    }

    @Transactional
    public Page<PostResponse> getAllPostsByCommunityId(Long communityId, int currentPage, int size, String sortBy, Long currentUserId) {
        Page<Post> posts = postRepository.findByCommunityIdOrderByCreatedAtDesc(communityId, PageRequest.of(currentPage, size, Sort.by(sortBy)));
        return posts.map(post -> responseFromEntity(post, currentUserId));
    }

    @Transactional
    public void deletePost(Long id, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (!post.getUser().equals(user)) {
            throw new IllegalArgumentException("You can only delete your own posts");
        }

        // Cancella i commenti e i like associati al post
        commentRepository.deleteByPostId(id);
        likeRepository.deleteByPostId(id);
        postRepository.delete(post);
    }

    public PostResponse responseFromEntity(Post post, Long authenticatedUserId) {
        UserResponse authorDTO = new UserResponse(
                post.getUser().getId(),
                post.getUser().getUsername(),
                post.getUser().getAvatar(),
                post.getUser().getFirstName(),
                post.getUser().getLastName(),
                post.getUser().getBio(),
                post.getUser().getCreatedAt(),
                post.getUser().getEmail()
        );

        int likeCount = likeRepository.countByPostId(post.getId());
        boolean likedByUser = likeRepository.existsByUserIdAndPostId(authenticatedUserId, post.getId());

        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getImage(),
                authorDTO,
                post.getCreatedAt()
        );
    }

    public Page<PostResponse> getAllPostsByUserId(Long userId, int currentPage, int size, String sortBy) {
        Page<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(currentPage, size, Sort.by(sortBy)));
        return posts.map(post -> responseFromEntity(post, userId));
    }
}
