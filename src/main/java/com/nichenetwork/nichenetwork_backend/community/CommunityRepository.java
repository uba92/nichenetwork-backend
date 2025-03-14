package com.nichenetwork.nichenetwork_backend.community;

import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMember;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    boolean existsByName(@NotBlank(message = "Name is required") String name);

    @Query("SELECT c FROM Community c LEFT JOIN FETCH c.posts WHERE c.id = :id")
    Optional<Community> findByIdWithPosts(@Param("id") Long id);

@Query("SELECT c FROM Community c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Community> searchCommunities(@Param("query") String query);
}
