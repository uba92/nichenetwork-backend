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
import java.util.stream.Collectors;

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

    @Transactional
    public void removeMember(Long adminId, Long userId, Long communityId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin/moderator user not found"));

        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User to remove not found"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));

        CommunityMember adminMember = communityMemberRepository.findByUserAndCommunity(admin, community)
                .orElseThrow(() -> new IllegalStateException("You are not a member of this community"));

        CommunityMember userMember = communityMemberRepository.findByUserAndCommunity(userToRemove, community)
                .orElseThrow(() -> new IllegalStateException("User is not a member of this community"));

        if (adminMember.getRole() != CommunityRole.OWNER && adminMember.getRole() != CommunityRole.MODERATOR) {
            throw new IllegalStateException("Only the owner or a moderator can remove members");
        }

        if (userMember.getRole() == CommunityRole.OWNER) {
            throw new IllegalStateException("You cannot remove the owner of the community");
        }

        communityMemberRepository.delete(userMember);
    }


    public List<CommunityMemberDTO> getCommunityMembers(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community not found"));
        List<CommunityMember> members = communityMemberRepository.findByCommunity(community);

        return members.stream().map(member -> new CommunityMemberDTO(
                member.getUser().getId(),
                member.getUser().getUsername(),
                member.getUser().getFirstName(),
                member.getUser().getLastName(),
                member.getUser().getEmail(),
                member.getRole()
        )).collect(Collectors.toList());
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
