package com.nichenetwork.nichenetwork_backend.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AppUserService appUserService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<String> createAdminUser(@RequestBody AdminUserRequest adminUserRequest) {
        appUserService.createAdminUser(adminUserRequest);
        return ResponseEntity.ok("Admin user created successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<String> deleteAdminUser(@PathVariable Long id) {
        appUserService.deleteAdminUser(id);
        return ResponseEntity.ok("Admin user deleted successfully");
    }

}
