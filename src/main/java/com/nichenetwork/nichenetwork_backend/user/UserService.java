package com.nichenetwork.nichenetwork_backend.user;

import com.nichenetwork.nichenetwork_backend.cloudinary.CloudinaryService;
import com.nichenetwork.nichenetwork_backend.comment.CommentRepository;
import com.nichenetwork.nichenetwork_backend.community.CommunityResponse;
import com.nichenetwork.nichenetwork_backend.community.CommunityService;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMemberRepository;
import com.nichenetwork.nichenetwork_backend.exceptions.BadRequestException;
import com.nichenetwork.nichenetwork_backend.like.LikeRepository;
import com.nichenetwork.nichenetwork_backend.post.PostRepository;
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
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommunityMemberRepository communityMemberRepository;


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
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
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

    @Transactional
    public String deleteUser(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
        if (!passwordEncoder.matches(password, appUser.getPassword())) {
            throw new BadRequestException("---La password non é corretta!---");
        }

        likeRepository.deleteByPostUserId(user.getId());
        commentRepository.deleteByUserId(user.getId());
        postRepository.deleteByUserId(user.getId());
        communityMemberRepository.deleteByUser(user);

        appUser.getRoles().clear();
        appUserRepository.save(appUser);

        userRepository.delete(user);
        appUserRepository.delete(appUser);
        return "Utente eliminato con successo";
    }

    public void deleteUserAsAdmin(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));
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

    public UserResponse changeAvatar(String username, MultipartFile file, String imageUrl) throws IOException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username " + username));

        if ((file == null || file.isEmpty()) && (imageUrl == null || imageUrl.isBlank())) {

            throw new BadRequestException("--- L'avatar non é valido! ---");
        }

        if (user.getAvatar() != null) {
            String publicId = extractPublicId(user.getAvatar());
            System.out.println("🔹 Public ID da eliminare: " + publicId);
            cloudinaryService.deleteImage(publicId);
        }

        String newAvatar = null;

        if (file != null && !file.isEmpty()) {

            Map uploadResult = cloudinaryService.uploadImage(file);
            newAvatar = (String) uploadResult.get("secure_url");

        } else if (imageUrl != null && !imageUrl.isBlank()) {

            Map result = cloudinaryService.uploadImageFromUrl(imageUrl);
            newAvatar = (String) result.get("secure_url");

        }

        user.setAvatar(newAvatar);
        userRepository.save(user);

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

        return userResponse;
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
