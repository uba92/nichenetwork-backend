package com.nichenetwork.nichenetwork_backend.user;

import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUserService;
import com.nichenetwork.nichenetwork_backend.security.auth.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping("/me")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal AppUser appUser) {
        String username = appUser.getUsername();

        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
    }


    @PutMapping("/updateProfile")
    public ResponseEntity<User> updateProfile(@AuthenticationPrincipal AppUser appUser, @RequestBody UpdateUserRequest request) {
        String username = appUser.getUsername();

        User user = userService.findByUsername(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        userService.updateProfile(username, request);

        User updatedUser = userService.findByUsername(username);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/changePassword")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal AppUser appUser, @RequestBody ChangePasswordRequest request) {
        String username = appUser.getUsername();

        return userService.changePassword(username, request);
    }

    @PutMapping("/changeAvatar")
    public ResponseEntity<User> changeAvatar(@AuthenticationPrincipal AppUser appUser, @RequestBody ChangeAvatarRequest request) {
        String username = appUser.getUsername();
        userService.changeAvatar(username, request);
        User updatedUser = userService.findByUsername(username);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/deleteUser")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal AppUser appUser,
                                             @RequestParam(required = false) String password,
                                             @RequestParam(required = false) String targetUsername) {
        String authenticatedUsername = appUser.getUsername();

        if (appUser.getRoles().contains(Role.ROLE_ADMIN)) {

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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<User>> searchUsersForAdmin(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<User> users = userService.searchUsersForAdmin(query, page, size);
        return users.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).build() : ResponseEntity.ok(users);
    }


    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
    }
}
