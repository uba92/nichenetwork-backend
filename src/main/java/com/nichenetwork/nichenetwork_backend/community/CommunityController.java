package com.nichenetwork.nichenetwork_backend.community;

import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final CommunityRepository communityRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<CommunityResponse> createCommunity(@RequestParam("name") String name,
                                                             @RequestParam("description") String description,
                                                             @RequestParam(value = "image", required = false) MultipartFile imageFile,
                                                             @RequestParam(value = "color", required = false) String color,
                                                             @AuthenticationPrincipal AppUser adminUser) throws IOException {

        CommunityRequest request = new CommunityRequest(name, description, null, color);
        CommunityResponse response = communityService.createCommunity(request, adminUser, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommunityResponse>> getAllCommunities() {
        List<CommunityResponse> response = communityService.getAllCommunities();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponse> getCommunityById(@PathVariable Long id, @AuthenticationPrincipal AppUser appUser) {

        Long userId = appUser.getId();

        boolean isMember = communityService.isUserMemberOfCommunity(userId, id);

        if (!isMember) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 🔒 Blocca l'accesso
        }
        CommunityResponse response = communityService.getCommunityById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommunityResponse> updateCommunity(@PathVariable Long id, @RequestBody CommunityRequest request) {
        CommunityResponse response = communityService.updateCommunity(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCommunity(@PathVariable Long id) {
        communityService.deleteCommunity(id);
        return ResponseEntity.status(HttpStatus.OK).body("Community deleted successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<List<CommunityResponse>> searchCommunities(@RequestParam String query) {
        List<CommunityResponse> response = communityService.searchCommunities(query);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
