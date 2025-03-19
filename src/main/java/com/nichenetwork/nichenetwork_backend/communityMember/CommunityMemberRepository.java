package com.nichenetwork.nichenetwork_backend.communityMember;

import com.nichenetwork.nichenetwork_backend.community.Community;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import com.nichenetwork.nichenetwork_backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    Optional<CommunityMember> findByUserAndCommunity(User user, Community community);

    List<CommunityMember> findByCommunity(Community community);

    boolean existsByUserAndCommunity(User user, Community community);

    void deleteByUserAndCommunity(User user, Community community);

    List<CommunityMember> findByUser(User user);

    void deleteByUser(User user);
}
