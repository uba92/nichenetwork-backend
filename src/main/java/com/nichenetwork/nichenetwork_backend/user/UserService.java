package com.nichenetwork.nichenetwork_backend.user;

import com.github.javafaker.App;
import com.nichenetwork.nichenetwork_backend.exceptions.BadRequestException;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + id));
    }

    public Page<UserResponse> searchUsers(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchUsers(query, pageable)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getAvatar(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBio(),
                        user.getCreatedAt()
                ));
    }


    public Page<User> searchUsersForAdmin(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchUsersForAdmin(query, pageable);
    }


    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getAvatar(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBio(),
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public ResponseEntity<String> updateProfile(String username, UpdateUserRequest request) {
        User user = findByUsername(username);
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        userRepository.save(user);
        return ResponseEntity.ok("Profilo aggiornato con successo");
    }

    public ResponseEntity<String> changePassword(String username, ChangePasswordRequest request) {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
            if (!passwordEncoder.matches(request.getOldPassword(), appUser.getPassword())) {
                throw new BadRequestException("---La vecchia password non è corretta!---");
            }

        if (request.getNewPassword().length() < 8) {
            throw new BadRequestException("La nuova password deve avere almeno 8 caratteri");
        }

        appUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        appUserRepository.save(appUser);
        return ResponseEntity.ok("Password aggiornata con successo");
    }

    public ResponseEntity<String> deleteUser(String username, String password) {
        User user = findByUsername(username);
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
        if (!passwordEncoder.matches(password, appUser.getPassword())) {
            throw new BadRequestException("---La password non é corretta!---");
        }
        appUserRepository.delete(appUser);
        userRepository.delete(user);
        return ResponseEntity.ok("Utente eliminato con successo");
    }

    public void deleteUserAsAdmin(String username) {
        User user = findByUsername(username);
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
        appUserRepository.delete(appUser);
        userRepository.delete(user);
    }


    public User loadUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + id));
    }

    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con email " + email));
    }

    public ResponseEntity<String> changeAvatar(String username, ChangeAvatarRequest request) {
        User user = findByUsername(username);
        if (request.getAvatar() == null) {
            throw new BadRequestException("---L'avatar non é valido!---");
        }
        user.setAvatar(request.getAvatar());
        userRepository.save(user);
        return ResponseEntity.ok("Avatar aggiornato con successo");
    }
}
