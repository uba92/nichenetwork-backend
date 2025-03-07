package com.nichenetwork.nichenetwork_backend.community;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    boolean existsByName(@NotBlank(message = "Name is required") String name);

    @Query("SELECT c FROM Community c LEFT JOIN FETCH c.posts WHERE c.id = :id")
    Optional<Community> findByIdWithPosts(@Param("id") Long id);

}
