package com.mamampoki.carhire.security;

import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerDetailsService implements UserDetailsService {

    private final OwnerRepository ownerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Owner owner = ownerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Owner not found with username: " + username));
        return new OwnerDetails(owner);
    }

    public UserDetails loadUserById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Owner not found with id: " + id));
        return new OwnerDetails(owner);
    }
}
