package com.nichenetwork.nichenetwork_backend.communityMember;

import com.nichenetwork.nichenetwork_backend.community.Community;
import com.nichenetwork.nichenetwork_backend.community.CommunityRepository;
import com.nichenetwork.nichenetwork_backend.enums.CommunityRole;
import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityMemberService {

    private final CommunityMemberRepository communityMemberRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    @Transactional
    public void joinCommunity(Long userId, Long communityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));

        if (communityMemberRepository.existsByUserAndCommunity(user, community)) {
            throw new IllegalStateException("User is already a member of this community");
        }

        CommunityMember member = new CommunityMember();
        member.setUser(user);
        member.setCommunity(community);
        member.setRole(CommunityRole.MEMBER);
        communityMemberRepository.save(member);
    }

    @Transactional
    public void leaveCommunity(Long userId, Long communityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));

        CommunityMember member = communityMemberRepository.findByUserAndCommunity(user, community)
                .orElseThrow(() -> new IllegalStateException("User is not a member of this community"));

        if (member.getRole() == CommunityRole.OWNER) {
            throw new IllegalStateException("The owner cannot leave the community");
        }

        communityMemberRepository.delete(member);
    }

    public List<CommunityMember> getCommunityMembers(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));
        return communityMemberRepository.findByCommunity(community);
    }

    @Transactional
    public void promoteToModerator(Long ownerId, Long userId, Long communityId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Owner user not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));

        CommunityMember ownerMember = communityMemberRepository.findByUserAndCommunity(owner, community)
                .orElseThrow(() -> new IllegalStateException("Owner is not part of the community"));

        if (ownerMember.getRole() != CommunityRole.OWNER) {
            throw new IllegalStateException("Only the owner can promote moderators");
        }

        CommunityMember userMember = communityMemberRepository.findByUserAndCommunity(user, community)
                .orElseThrow(() -> new IllegalStateException("User is not part of the community"));

        userMember.setRole(CommunityRole.MODERATOR);
        communityMemberRepository.save(userMember);
    }
}
