package com.nichenetwork.nichenetwork_backend.user;

import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/updateProfile")
    @PreAuthorize("#appUser.username == #username")
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
    @PreAuthorize("#appUser.username == #username")
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
    @PreAuthorize("#appUser.username == #username or hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal AppUser appUser, @RequestParam String password) {
        String username = appUser.getUsername();
        userService.deleteUser(username, password);
        return ResponseEntity.ok("Utente eliminato con successo");
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> searchUser(@RequestParam String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(user);
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
