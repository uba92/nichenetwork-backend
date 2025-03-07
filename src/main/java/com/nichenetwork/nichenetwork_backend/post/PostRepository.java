package com.nichenetwork.nichenetwork_backend.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Post> findByCommunityIdOrderByCreatedAtDesc(Long communityId, Pageable pageable);

    List<Post> findByCommunityId(Long id);
}
