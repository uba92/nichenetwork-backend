package com.nichenetwork.nichenetwork_backend.community;

import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMember;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMemberRepository;
import com.nichenetwork.nichenetwork_backend.enums.CommunityRole;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import com.nichenetwork.nichenetwork_backend.security.auth.Role;
import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (!adminUser.getRole().equals(Role.ROLE_ADMIN)) {
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
                community.getCreatedAt().toString()
        );

        return response;
    }


    public CommunityResponse getCommunityById(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));

        CommunityResponse response = new CommunityResponse(community.getId(), community.getName(), community.getDescription(), community.getCreatedAt().toString());
        return response;
    }

    public List<CommunityResponse> getAllCommunities() {
        List<Community> communities = communityRepository.findAll();
        List<CommunityResponse> response = communities.stream()
                .map(community -> new CommunityResponse(community.getId(), community.getName(), community.getDescription(), community.getCreatedAt().toString()))
                .collect(Collectors.toList());
        return response;
    }


    @Transactional
    public CommunityResponse updateCommunity(Long id, CommunityRequest request) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));

        community.setName(request.getName());
        community.setDescription(request.getDescription());
        communityRepository.save(community);

        CommunityResponse response = new CommunityResponse(community.getId(), community.getName(), community.getDescription(), community.getCreatedAt().toString());
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
