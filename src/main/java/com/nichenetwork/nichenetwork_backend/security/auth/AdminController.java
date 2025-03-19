package com.nichenetwork.nichenetwork_backend.security.auth;

import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import com.nichenetwork.nichenetwork_backend.user.UserResponse;
import com.nichenetwork.nichenetwork_backend.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AppUserService appUserService;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<String> createAdminUser(@RequestBody AdminUserRequest adminUserRequest) {
        appUserService.createAdminUser(adminUserRequest);
        return ResponseEntity.ok("Admin user created successfully");
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "username") String sortBy) {
        Page<UserResponse> users = userService.getAllUsers(currentPage, size, sortBy);
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    public ResponseEntity<String> deleteAdminUser(@PathVariable Long id) {
        appUserService.deleteAdminUser(id);
        return ResponseEntity.ok("Admin user deleted successfully");
    }

    @GetMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserRole(@PathVariable Long id) {
        try {

            User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + id));


            Optional<AppUser> appUser = appUserService.findByUsername(user.getUsername());

            if (appUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("AppUser not found");
            }


            Role role = appUser.get().getRoles().stream().findFirst().orElse(null);

            if (role == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No role found");
            }

            return ResponseEntity.ok(Map.of("role", role.name()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
