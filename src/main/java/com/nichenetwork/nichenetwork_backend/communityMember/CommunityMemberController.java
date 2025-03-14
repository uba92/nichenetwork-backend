package com.nichenetwork.nichenetwork_backend.communityMember;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community-members")
@RequiredArgsConstructor
public class   CommunityMemberController {

    private final CommunityMemberService communityMemberService;

    @PostMapping("/join/{userId}/{communityId}")
    public ResponseEntity<String> joinCommunity(@PathVariable Long userId, @PathVariable Long communityId) {
        communityMemberService.joinCommunity(userId, communityId);
        return ResponseEntity.ok("User joined the community successfully");
    }

    @DeleteMapping("/leave/{userId}/{communityId}")
    public ResponseEntity<String> leaveCommunity(@PathVariable Long userId, @PathVariable Long communityId) {
        communityMemberService.leaveCommunity(userId, communityId);
        return ResponseEntity.ok("User left the community successfully");
    }

    @GetMapping("/{communityId}/members")
    public ResponseEntity<List<CommunityMemberDTO>> getCommunityMembers(@PathVariable Long communityId) {
        return ResponseEntity.ok(communityMemberService.getCommunityMembers(communityId));
    }

    @PostMapping("/promote/{ownerId}/{userId}/{communityId}")
    public ResponseEntity<String> promoteToModerator(@PathVariable Long ownerId, @PathVariable Long userId, @PathVariable Long communityId) {
        communityMemberService.promoteToModerator(ownerId, userId, communityId);
        return ResponseEntity.ok("User promoted to moderator successfully");
    }

    @DeleteMapping("/remove/{adminId}/{userId}/{communityId}")
    public ResponseEntity<String> removeMember(
            @PathVariable Long adminId,
            @PathVariable Long userId,
            @PathVariable Long communityId) {
        communityMemberService.removeMember(adminId, userId, communityId);
        return ResponseEntity.ok("User removed from the community successfully");
    }

}
