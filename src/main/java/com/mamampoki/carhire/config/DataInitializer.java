package com.mamampoki.carhire.config;

import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@Profile("dev")
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(OwnerRepository ownerRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (ownerRepository.findByUsername("mamampoki").isEmpty()) {
                Owner owner = Owner.builder()
                        .username("mamampoki")
                        .password(passwordEncoder.encode("MamaMpoki2026!"))
                        .fullName("Mama Mpoki")
                        .phone("+255712345678")
                        .email("info@mamampoki.co.tz")
                        .build();
                ownerRepository.save(owner);
                log.info("✅ Default owner created: mamampoki / MamaMpoki2026!");
            }
        };
    }
}
