package com.nichenetwork.nichenetwork_backend.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Order(1)
@Component
public class AuthRunner implements ApplicationRunner {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //Creazione del superadmin se non esiste
        Optional<AppUser> superAdminUser = appUserService.findByUsername("superadmin");
        if (superAdminUser.isEmpty()) {
            // Creiamo il superadmin con un ruolo speciale
            appUserService.registerUser("superadmin", "superadminpassword", "superadmin@example.com", "Super", "Admin", Set.of(Role.ADMIN, Role.SUPERADMIN));
        }

        // Creazione dell'utente admin se non esiste
        Optional<AppUser> adminUser = appUserService.findByUsername("admin");
        if (adminUser.isEmpty()) {
            appUserService.registerUser("admin", "adminpassword", "admin@example.com", "Admin", "User", Set.of(Role.ADMIN));
        }

        // Creazione dell'utente user se non esiste
        Optional<AppUser> normalUser = appUserService.findByUsername("user");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user", "userpassword", "user@example.com", "Normal", "User", Set.of(Role.USER));
        }Optional<AppUser> normalUser1 = appUserService.findByUsername("user1");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user1", "userpassword", "user1@example.com", "Normal1", "User1", Set.of(Role.USER));
        }Optional<AppUser> normalUser2 = appUserService.findByUsername("user2");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user2", "userpassword", "user2@example.com", "Normal2", "User2", Set.of(Role.USER));
        }Optional<AppUser> normalUser3 = appUserService.findByUsername("user3");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user3", "userpassword", "user3@example.com", "Normal3", "User3", Set.of(Role.USER));
        }Optional<AppUser> normalUser4 = appUserService.findByUsername("user4");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user4", "userpassword", "user4@example.com", "Normal4", "User4", Set.of(Role.USER));
        }Optional<AppUser> normalUser5= appUserService.findByUsername("user5");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user5", "userpassword", "user5@example.com", "Normal5", "User5", Set.of(Role.USER));
        }Optional<AppUser> normalUser6 = appUserService.findByUsername("user6");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user6", "userpassword", "user6@example.com", "Normal6", "User6", Set.of(Role.USER));
        }Optional<AppUser> normalUser7 = appUserService.findByUsername("user7");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user7", "userpassword", "user7@example.com", "Normal7", "User7", Set.of(Role.USER));
        }Optional<AppUser> normalUser8 = appUserService.findByUsername("user8");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user8", "userpassword", "user8@example.com", "Normal8", "User8", Set.of(Role.USER));
        }Optional<AppUser> normalUser9 = appUserService.findByUsername("user9");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user9", "userpassword", "user9@example.com", "Normal9", "User9", Set.of(Role.USER));
        }
    }
}
