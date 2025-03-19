package com.nichenetwork.nichenetwork_backend.security.auth;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    void deleteByUsername(String username);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM app_user_roles WHERE app_user_id = :userId", nativeQuery = true)
    void deleteRolesByUserId(@Param("userId") Long userId);
}
