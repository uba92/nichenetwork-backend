package com.nichenetwork.nichenetwork_backend.community;

import com.nichenetwork.nichenetwork_backend.comment.CommentResponse;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMember;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMemberRepository;
import com.nichenetwork.nichenetwork_backend.enums.CommunityRole;
import com.nichenetwork.nichenetwork_backend.exceptions.UnauthorizedException;
import com.nichenetwork.nichenetwork_backend.post.Post;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommunityResponse createCommunity(CommunityRequest request, AppUser adminUser) {

        System.out.println("Admin autenticato: " + (adminUser != null ? adminUser.getEmail() : "null"));
        System.out.println("Ruolo dell'admin: " + (adminUser != null ? adminUser.getRole() : "null"));

        if(communityRepository.existsByName(request.getName())) throw new EntityExistsException("Community with name " + request.getName() + " already exists.");


        if (adminUser == null || adminUser.getRole() == null) {
            throw new UnauthorizedException("Utente non autenticato o ruolo non assegnato");
        }

        if (!adminUser.getRole().equals(Role.ADMIN)) {
            throw new IllegalStateException("Only admins can create communities");
        }

        Community community = new Community();
        community.setName(request.getName());
        community.setDescription(request.getDescription());
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
                community.getCreatedAt()
//                community.getPosts().stream()
//                        .map(post -> new PostResponse(
//                                post.getId(),
//                                post.getContent(),
//                                post.getImage(),
//                                post.getUser().getUsername(),
//                                post.getCreatedAt()
//                        ))
//                        .collect(Collectors.toList())
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
                                post.getUser().getCreatedAt()),
                        post.getCreatedAt()
                ))
                .collect(Collectors.toList());


        // Mappiamo la community nei DTO
        return new CommunityResponse(
                community.getId(),
                community.getName(),
                community.getDescription(),
                community.getCreatedAt()
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
                        community.getCreatedAt()
//                        community.getPosts().stream()
//                                .map(post -> new PostResponse(
//                                        post.getId(),
//                                        post.getContent(),
//                                        post.getImage(),
//                                        post.getUser().getUsername(),
//                                        post.getCreatedAt()
//                                ))
//                                .collect(Collectors.toList()) // Converti in lista
                ))
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
                community.getCreatedAt());
        return response;
    }


    @Transactional
    public String deleteCommunity(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));

        communityRepository.delete(community);
        return ("Community deleted successfully");
    }

}
