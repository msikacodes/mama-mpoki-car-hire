package com.mamampoki.carhire.owner;

import com.mamampoki.carhire.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public Owner getOwnerById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", id));
    }

    public Owner getOwnerByUsername(String username) {
        return ownerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "username", username));
    }

    public boolean existsByUsername(String username) {
        return ownerRepository.existsByUsername(username);
    }

    @Transactional
    public Owner updateProfile(Long id, String fullName, String phone, String email) {
        Owner owner = getOwnerById(id);
        if (fullName != null) owner.setFullName(fullName);
        if (phone != null) owner.setPhone(phone);
        if (email != null) owner.setEmail(email);
        return ownerRepository.save(owner);
    }
}
