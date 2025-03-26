package com.nichenetwork.nichenetwork_backend.community;

import com.nichenetwork.nichenetwork_backend.cloudinary.CloudinaryService;
import com.nichenetwork.nichenetwork_backend.comment.CommentRepository;
import com.nichenetwork.nichenetwork_backend.comment.CommentResponse;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMember;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMemberRepository;
import com.nichenetwork.nichenetwork_backend.enums.CommunityRole;
import com.nichenetwork.nichenetwork_backend.exceptions.UnauthorizedException;
import com.nichenetwork.nichenetwork_backend.like.LikeRepository;
import com.nichenetwork.nichenetwork_backend.notification.NotificationRepository;
import com.nichenetwork.nichenetwork_backend.post.Post;
import com.nichenetwork.nichenetwork_backend.post.PostRepository;
import com.nichenetwork.nichenetwork_backend.post.PostResponse;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import com.nichenetwork.nichenetwork_backend.security.auth.Role;
import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import com.nichenetwork.nichenetwork_backend.user.UserResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CloudinaryService cloudinaryService;
    private final PostRepository postRepository;
    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommunityResponse createCommunity(CommunityRequest request, AppUser adminUser, MultipartFile imageFile) throws IOException {

        if (adminUser == null || adminUser.getRole() == null) {
            throw new UnauthorizedException("Utente non autenticato o ruolo non assegnato");
        }

        if (!adminUser.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("Only admins can create communities");
        }

        Community community = new Community();
        community.setName(request.getName());
        community.setDescription(request.getDescription());
        community.setColor(request.getColor());

        if (imageFile != null && !imageFile.isEmpty()) {
            Map uploadResult = cloudinaryService.uploadImage(imageFile);
            String imageUrl = (String) uploadResult.get("secure_url");
            community.setImageUrl(imageUrl);
        } else {
            community.setImageUrl("https://res.cloudinary.com/dh5lzyq0h/image/upload/v1741883395/ynukn6mxyp9stdujryf6.jpg");
        }
        communityRepository.save(community);


        User admin = userRepository.findByEmail(adminUser.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found"));


        CommunityMember owner = new CommunityMember();
        owner.setUser(admin);
        owner.setCommunity(community);
        owner.setRole(CommunityRole.OWNER);
        communityMemberRepository.save(owner);


        CommunityResponse response = new CommunityResponse(
                community.getId(),
                community.getName(),
                community.getDescription(),
                community.getCreatedAt(),
                community.getImageUrl(),
                community.getColor()
        );

        return response;
    }


    @Transactional(readOnly = true)
    public CommunityResponse getCommunityById(Long id) {
        Community community = communityRepository.findByIdWithPosts(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));


        List<PostResponse> postResponses = community.getPosts().stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getContent(),
                        post.getImage(),
                        new UserResponse(
                                post.getUser().getId(),
                                post.getUser().getUsername(),
                                post.getUser().getAvatar(),
                                post.getUser().getFirstName(),
                                post.getUser().getLastName(),
                                post.getUser().getBio(),
                                post.getUser().getCreatedAt(),
                        post.getUser().getEmail()),
                        post.getCreatedAt()
                        ))
                .collect(Collectors.toList());


        // Mappiamo la community nei DTO
        return new CommunityResponse(
                community.getId(),
                community.getName(),
                community.getDescription(),
                community.getCreatedAt(),
                community.getImageUrl(),
                community.getColor()
//                postResponses
        );
    }

    public List<CommunityResponse> getAllCommunities() {
        List<Community> communities = communityRepository.findAll();

        // Mappiamo le communities nei DTO
        return communities.stream()
                .map(community -> new CommunityResponse(
                        community.getId(),
                        community.getName(),
                        community.getDescription(),
                        community.getCreatedAt(),
                        community.getImageUrl(),
                        community.getColor()))
                .collect(Collectors.toList());
    }


    @Transactional
    public CommunityResponse updateCommunity(Long id, CommunityRequest request) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));

        community.setName(request.getName());
        community.setDescription(request.getDescription());
        communityRepository.save(community);

        CommunityResponse response = new CommunityResponse(community.getId(),
                community.getName(),
                community.getDescription(),
                community.getCreatedAt(),
                community.getImageUrl(),
                community.getColor());
        return response;
    }


    @Transactional
    public void deleteCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community non trovata"));

        List<Post> communityPosts = postRepository.findByCommunity(community);
        List<Long> postIds = communityPosts.stream().map(Post::getId).toList();

        commentRepository.deleteByPostIdIn(postIds);

        likeRepository.deleteByPostIdIn(postIds);

        for (Post post : communityPosts) {
            notificationRepository.deleteByRelatedPost(post);
        }

        for (Post post : communityPosts) {
            post.setComments(new ArrayList<>());
        }

        postRepository.deleteAll(communityPosts);

        communityMemberRepository.deleteByCommunity(community);

        communityRepository.delete(community);
    }




    public List<CommunityResponse> searchCommunities(String query) {
        List<Community> communities = communityRepository.searchCommunities(query);
        return communities.stream()
                .map(community -> new CommunityResponse(
                        community.getId(),
                        community.getName(),
                        community.getDescription(),
                        community.getCreatedAt(),
                        community.getImageUrl(),
                        community.getColor()
                ))
                .collect(Collectors.toList());
    }

    public List<CommunityResponse> getMyCommunities(AppUser appUser) {

        User user = userRepository.findByEmail(appUser.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email " + appUser.getEmail()));
        List<CommunityMember> communityMembers = communityMemberRepository.findByUser(user);
        List<Community> communities = new ArrayList<>();
        for (CommunityMember communityMember : communityMembers) {
            communities.add(communityMember.getCommunity());
        }
        return communities.stream()
                .map(community -> new CommunityResponse(
                        community.getId(),
                        community.getName(),
                        community.getDescription(),
                        community.getCreatedAt(),
                        community.getImageUrl(),
                        community.getColor()
                ))
                .collect(Collectors.toList());
    }

    public boolean isUserMemberOfCommunity(Long userId, Long id) {
        return communityMemberRepository.existsByUserIdAndCommunityId(userId, id);
    }
}
