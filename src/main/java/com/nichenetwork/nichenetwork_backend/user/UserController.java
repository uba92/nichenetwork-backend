package com.nichenetwork.nichenetwork_backend.user;

import com.nichenetwork.nichenetwork_backend.community.CommunityResponse;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import com.nichenetwork.nichenetwork_backend.security.auth.Role;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal AppUser appUser) {
        String username = appUser.getUsername();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        System.out.println("🔹 Utente trovato: " + user);
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getAvatar(),
                user.getFirstName(),
                user.getLastName(),
                user.getBio(),
                user.getCreatedAt(),
                user.getEmail()
        );

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/me/communities")
    public ResponseEntity<List<CommunityResponse>> getMyCommunities(@AuthenticationPrincipal AppUser appUser) {
        String username = appUser.getUsername();
        List<CommunityResponse> communities = userService.getMyCommunities(username);
        return ResponseEntity.ok(communities);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(@RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "username") String sortBy) {
        Page<UserResponse> users = userService.getAllUsers(currentPage, size, sortBy);
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }


    @PutMapping("/updateProfile")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal AppUser appUser, @RequestBody UpdateUserRequest request) {
        String username = appUser.getUsername();

        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        userService.updateProfile(username, request);

        User updatedUser = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/changePassword")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal AppUser appUser, @RequestBody ChangePasswordRequest request) {
        String username = appUser.getUsername();

        userService.changePassword(username, request);

        return ResponseEntity.ok("Password aggiornata con successo");
    }

    @PutMapping(value = "/changeAvatar", consumes = "multipart/form-data")
    public ResponseEntity<UserResponse> changeAvatar(@AuthenticationPrincipal AppUser appUser, @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam(value = "imageUrl", required = false) String imageUrl) {
        try {
            String username = appUser.getUsername();
            UserResponse user = userService.changeAvatar(username, file, imageUrl);
            return ResponseEntity.ok(user);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/deleteUser")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal AppUser appUser,
                                             @RequestBody Map<String, String> requestBody) {
        String targetUsername = requestBody.get("targetUsername");
        String password = requestBody.get("password");
        String authenticatedUsername = appUser.getUsername();

        if (appUser.getRoles().contains(Role.ADMIN)) {

            if (targetUsername == null || targetUsername.isEmpty()) {
                return ResponseEntity.badRequest().body("Devi specificare lo username dell'utente da eliminare");
            }
            userService.deleteUserAsAdmin(targetUsername);
            return ResponseEntity.ok("Utente eliminato con successo dall'admin");
        } else {

            if (password == null || password.isEmpty()) {
                return ResponseEntity.badRequest().body("Devi inserire la password per eliminare il tuo account");
            }
            userService.deleteUser(authenticatedUsername, password);
            return ResponseEntity.ok("Il tuo account è stato eliminato con successo");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserResponse> users = userService.searchUsers(query, page, size);
        return users.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).build() : ResponseEntity.ok(users);
    }

    @GetMapping("/admin/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> searchUsersForAdmin(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<User> users = userService.searchUsersForAdmin(query, page, size);
        return users.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).build() : ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + id));
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }
}
