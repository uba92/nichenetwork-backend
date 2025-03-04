package com.nichenetwork.nichenetwork_backend.community;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommunityResponse> createCommunity(CommunityRequest request) {
        Community community = new Community();
        community.setName(request.getName());
        community.setDescription(request.getDescription());
        communityRepository.save(community);

        CommunityResponse response = new CommunityResponse(community.getId(), community.getName(), community.getDescription(), community.getCreatedAt().toString());
        return ResponseEntity.ok(response);
    }


    public ResponseEntity<CommunityResponse> getCommunityById(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));

        CommunityResponse response = new CommunityResponse(community.getId(), community.getName(), community.getDescription(), community.getCreatedAt().toString());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<CommunityResponse>> getAllCommunities() {
        List<Community> communities = communityRepository.findAll();
        List<CommunityResponse> response = communities.stream()
                .map(community -> new CommunityResponse(community.getId(), community.getName(), community.getDescription(), community.getCreatedAt().toString()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }


    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CommunityResponse> updateCommunity(Long id, CommunityRequest request) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));

        community.setName(request.getName());
        community.setDescription(request.getDescription());
        communityRepository.save(community);

        CommunityResponse response = new CommunityResponse(community.getId(), community.getName(), community.getDescription(), community.getCreatedAt().toString());
        return ResponseEntity.ok(response);
    }


    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteCommunity(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));

        communityRepository.delete(community);
        return ResponseEntity.ok("Community deleted successfully");
    }

}
