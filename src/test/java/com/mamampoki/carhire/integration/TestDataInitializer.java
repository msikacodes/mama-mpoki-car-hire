package com.mamampoki.carhire.integration;

import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("test")
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final OwnerRepository ownerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (ownerRepository.count() == 0) {
            Owner owner = Owner.builder()
                    .username("testowner")
                    .password(passwordEncoder.encode("TestPass123!"))
                    .fullName("Test Owner")
                    .phone("+255700000000")
                    .email("test@mamampoki.co.tz")
                    .build();

            ownerRepository.save(owner);
            log.info("Test owner account created: testowner / TestPass123!");
        }
    }
}
