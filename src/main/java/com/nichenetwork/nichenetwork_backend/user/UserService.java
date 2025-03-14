package com.nichenetwork.nichenetwork_backend.user;

import com.nichenetwork.nichenetwork_backend.cloudinary.CloudinaryService;
import com.nichenetwork.nichenetwork_backend.community.CommunityResponse;
import com.nichenetwork.nichenetwork_backend.community.CommunityService;
import com.nichenetwork.nichenetwork_backend.exceptions.BadRequestException;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final CloudinaryService cloudinaryService;
    private final CommunityService communityService;

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
                        user.getCreatedAt(),
                        user.getEmail()
                ));
    }


    public Page<User> searchUsersForAdmin(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.searchUsersForAdmin(query, pageable);
    }


    public Page<UserResponse> getAllUsers(int currentPage, int size, String sortBy) {
        return userRepository.findAll(PageRequest.of(currentPage, size, Sort.by(sortBy)))
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getAvatar(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getBio(),
                        user.getCreatedAt(),
                        user.getEmail()
                ));
    }


    @Transactional
    public String updateProfile(String username, UpdateUserRequest request) {
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
        return "Profilo aggiornato con successo";
    }

    public String changePassword(String username, ChangePasswordRequest request) {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
            if (!passwordEncoder.matches(request.getOldPassword(), appUser.getPassword())) {
                throw new BadRequestException("---La vecchia password non è corretta!---");
            }

        if (request.getNewPassword().length() < 8) {
            throw new BadRequestException("La nuova password deve avere almeno 8 caratteri");
        }

        appUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        appUserRepository.save(appUser);
        return"Password aggiornata con successo";
    }

    public String deleteUser(String username, String password) {
        User user = findByUsername(username);
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
        if (!passwordEncoder.matches(password, appUser.getPassword())) {
            throw new BadRequestException("---La password non é corretta!---");
        }
        appUserRepository.delete(appUser);
        userRepository.delete(user);
        return "Utente eliminato con successo";
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

    public User changeAvatar(String username, MultipartFile file, String imageUrl) throws IOException {
        User user = findByUsername(username);

        // 🔹 LOG DI DEBUG
        System.out.println("📥 File ricevuto: " + (file != null ? file.getOriginalFilename() : "Nessun file"));
        System.out.println("📥 URL ricevuto: " + imageUrl);

        // 🔹 CONTROLLIAMO SE IL FILE O L'URL SONO PRESENTI
        if ((file == null || file.isEmpty()) && (imageUrl == null || imageUrl.isBlank())) {
            System.out.println("❌ Nessun file o URL valido ricevuto!");
            throw new BadRequestException("--- L'avatar non é valido! ---");
        }

        // 🔹 SE C'È UN AVATAR PRECEDENTE, LO ELIMINIAMO DA CLOUDINARY
        if (user.getAvatar() != null) {
            String publicId = extractPublicId(user.getAvatar());
            System.out.println("🔹 Public ID da eliminare: " + publicId);
            cloudinaryService.deleteImage(publicId);
        }

        String newAvatar = null;

        // 🔹 UPLOAD DA FILE LOCALE
        if (file != null && !file.isEmpty()) {
            System.out.println("🟢 Upload da file locale in corso...");
            Map uploadResult = cloudinaryService.uploadImage(file);
            newAvatar = (String) uploadResult.get("secure_url");
            System.out.println("✅ Nuovo avatar da file caricato: " + newAvatar);

            // 🔹 UPLOAD DA URL
        } else if (imageUrl != null && !imageUrl.isBlank()) {
            System.out.println("🟡 Upload da URL rilevato...");
            System.out.println("🔹 Tentativo di upload da URL: " + imageUrl);
            Map result = cloudinaryService.uploadImageFromUrl(imageUrl);
            newAvatar = (String) result.get("secure_url");
            System.out.println("✅ Nuovo avatar da URL caricato: " + newAvatar);
        }

        // 🔹 AGGIORNIAMO L'AVATAR DELL'UTENTE
        user.setAvatar(newAvatar);
        userRepository.save(user);
        System.out.println("🟢 Avatar aggiornato con successo per l'utente: " + username);

        return user;
    }


    private String extractPublicId(String avatarUrl) {
        try {
            String[] parts = avatarUrl.split("/");
            String fileName = parts[parts.length - 1];
            String fileWithoutExtension = fileName.substring(0, fileName.lastIndexOf("."));

            if(avatarUrl.contains("uploads/")) {
                return "uploads/" + fileWithoutExtension;
            }
            return fileWithoutExtension;
        } catch (Exception e) {
            System.out.println("⚠️ Errore durante l'extrazione del public ID: " + e.getMessage());
            return null;
        }


    }

    public List<CommunityResponse> getMyCommunities(String username) {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
        return communityService.getMyCommunities(appUser);
    }
}
