package com.nichenetwork.nichenetwork_backend.user;

import com.nichenetwork.nichenetwork_backend.exceptions.BadRequestException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id " + id));
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
        User user = findByUsername(username);
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new BadRequestException("---La vecchia password non è corretta!---");
            }

        if (request.getNewPassword().length() < 8) {
            throw new BadRequestException("La nuova password deve avere almeno 8 caratteri");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Password aggiornata con successo");
    }

    public ResponseEntity<String> deleteUser(String username, String password) {
        User user = findByUsername(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("---La password non é corretta!---");
        }
        userRepository.delete(user);
        return ResponseEntity.ok("Utente eliminato con successo");
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
