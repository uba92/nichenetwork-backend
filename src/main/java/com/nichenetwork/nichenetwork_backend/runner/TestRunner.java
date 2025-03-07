package com.nichenetwork.nichenetwork_backend.runner;

import com.github.javafaker.Faker;
import com.nichenetwork.nichenetwork_backend.community.*;
import com.nichenetwork.nichenetwork_backend.config.FakerConfig;
import com.nichenetwork.nichenetwork_backend.post.Post;
import com.nichenetwork.nichenetwork_backend.post.PostRepository;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUser;
import com.nichenetwork.nichenetwork_backend.security.auth.AppUserRepository;
import com.nichenetwork.nichenetwork_backend.security.auth.Role;
import com.nichenetwork.nichenetwork_backend.user.User;
import com.nichenetwork.nichenetwork_backend.user.UserRepository;
import com.nichenetwork.nichenetwork_backend.user.UserResponse;
import com.nichenetwork.nichenetwork_backend.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TestRunner implements CommandLineRunner {

    private final CommunityService communityService;
    private final Faker faker;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final AppUserRepository appUserRepository;
    private final CommunityRepository communityRepository;

    @Override
    public void run(String... args) throws Exception {

        //creo utente AppUser per l'autenticazione
        AppUser appUser = new AppUser();
        appUser.setUsername("admin2");
        appUser.setPassword("adminpassword2");
        appUser.setEmail("admin2@example.com");
        appUser.setRoles(Set.of(Role.ADMIN));
        appUserRepository.save(appUser);

        //creo utente User
        User user = new User();
        user.setUsername(appUser.getUsername());
        user.setEmail(appUser.getEmail());
        userRepository.save(user);

        System.out.println("---Creando le communities---");
       for (int i = 0; i < 10; i++) {
           Community community = new Community();
           community.setName(faker.internet().domainName());
           community.setDescription(faker.lorem().sentence());

           CommunityRequest request = new CommunityRequest(community.getName(), community.getDescription());

           communityService.createCommunity(request, appUser);
       }
        System.out.println("Communities salvate nel Database ");

       System.out.println("---Creazione di post per le communities---");

       //recupero le communities e gli utenti dal db
        Page<UserResponse> users = userService.getAllUsers(0, 10, "username");
        List<CommunityResponse> communities = communityService.getAllCommunities();

        if (users.isEmpty() || communities.isEmpty()) {
            System.out.println("Nessun utente o community trovati nel database.");
            return;
        }

        System.out.println("---Creazione di post per le communities---");
       for(int i = 0; i < 10; i++) {
           Post post = new Post();
           post.setContent(faker.lorem().sentence());

           User randomUser = userService.loadUserById((long) faker.number().numberBetween(1, users.getContent().size()));

           Long communityId = (long) faker.number().numberBetween(1, communities.size());

//           CommunityResponse randomCommunityResponse = communityService.getCommunityById(communityId);
//
//           Community randomCommunity = new Community();
//           randomCommunity.setId(randomCommunityResponse.getId());
//           randomCommunity.setName(randomCommunityResponse.getName());
//           randomCommunity.setDescription(randomCommunityResponse.getDescription());
//           randomCommunity.setCreatedAt(randomCommunityResponse.getCreatedAt());
//           randomCommunity.setPosts(postRepository.findByCommunityId(randomCommunityResponse.getId()));

           Community randomCommunity = communityRepository.findById(communityId)
                   .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + communityId));

           post.setUser(randomUser);
           post.setCommunity(randomCommunity);
           postRepository.save(post);
       }

        System.out.println("---Post salvati nel Database---");

       }

}
