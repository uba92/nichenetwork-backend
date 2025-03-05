package com.nichenetwork.nichenetwork_backend.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class AuthRunner implements ApplicationRunner {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //Creazioine del superadmin se non esiste
        Optional<AppUser> superAdminUser = appUserService.findByUsername("superadmin");
        if (superAdminUser.isEmpty()) {
            // Creiamo il superadmin con un ruolo speciale
            appUserService.registerUser("superadmin", "superadminpassword", "superadmin@example.com", "Super", "Admin", Set.of(Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN));
        }

        // Creazione dell'utente admin se non esiste
        Optional<AppUser> adminUser = appUserService.findByUsername("admin");
        if (adminUser.isEmpty()) {
            appUserService.registerUser("admin", "adminpassword", "admin@example.com", "Admin", "User", Set.of(Role.ROLE_ADMIN));
        }

        // Creazione dell'utente user se non esiste
        Optional<AppUser> normalUser = appUserService.findByUsername("user");
        if (normalUser.isEmpty()) {
            appUserService.registerUser("user", "userpassword", "user@example.com", "Normal", "User", Set.of(Role.ROLE_USER));
        }
    }
}
