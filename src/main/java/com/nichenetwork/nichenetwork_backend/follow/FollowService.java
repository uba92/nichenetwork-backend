package com.nichenetwork.nichenetwork_backend.follow;

import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import com.nichenetwork.nichenetwork_backend.user.UserResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void followUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower user not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new EntityNotFoundException("Following user not found"));

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new EntityExistsException("Already following this user");
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepository.save(follow);
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower user not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new EntityNotFoundException("Following user not found"));

        return followRepository.existsByFollowerAndFollowing(follower, following);
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new EntityNotFoundException("Follower user not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new EntityNotFoundException("Following user not found"));

        if (!followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new EntityNotFoundException("Follow relationship not found");
        }

        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

    public int countFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return followRepository.countByFollowing(user);
    }

    public int countFollowing(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return followRepository.countByFollower(user);
    }

    public List<UserResponse> getFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return followRepository.findByFollowing(user).stream()
                .map(follow -> new UserResponse(
                        follow.getFollower().getId(),
                        follow.getFollower().getUsername(),
                        follow.getFollower().getAvatar(),
                        follow.getFollower().getFirstName(),
                        follow.getFollower().getLastName(),
                        follow.getFollower().getBio(),
                        follow.getFollower().getCreatedAt(),
                        follow.getFollower().getEmail()
                ))
                .collect(Collectors.toList());
    }

    public List<UserResponse> getFollowing(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return followRepository.findByFollower(user).stream()
                .map(follow -> new UserResponse(
                        follow.getFollowing().getId(),
                        follow.getFollowing().getUsername(),
                        follow.getFollowing().getAvatar(),
                        follow.getFollowing().getFirstName(),
                        follow.getFollowing().getLastName(),
                        follow.getFollowing().getBio(),
                        follow.getFollowing().getCreatedAt(),
                        follow.getFollowing().getEmail()
                ))
                .collect(Collectors.toList());
    }

}
