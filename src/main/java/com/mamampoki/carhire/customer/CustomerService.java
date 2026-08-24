package com.mamampoki.carhire.customer;

import com.mamampoki.carhire.exception.ResourceNotFoundException;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import com.mamampoki.carhire.customer.dto.CustomerRequest;
import com.mamampoki.carhire.customer.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;

    public Page<CustomerResponse> getCustomers(Long ownerId, Pageable pageable) {
        Page<Customer> customers = customerRepository.findByOwnerIdAndDeletedFalse(ownerId, pageable);
        return customers.map(this::toResponse);
    }

    public CustomerResponse getCustomerById(Long ownerId, Long customerId) {
        Customer customer = findCustomer(ownerId, customerId);
        return toResponse(customer);
    }

    @Transactional
    public CustomerResponse createCustomer(Long ownerId, CustomerRequest request) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", ownerId));

        Customer customer = Customer.builder()
                .owner(owner)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .idType(request.getIdType())
                .idNumber(request.getIdNumber())
                .notes(request.getNotes())
                .build();

        customer = customerRepository.save(customer);
        log.info("Customer created: {} ({})", customer.getFullName(), customer.getPhone());
        return toResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long ownerId, Long customerId, CustomerRequest request) {
        Customer customer = findCustomer(ownerId, customerId);

        customer.setFullName(request.getFullName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setIdType(request.getIdType());
        customer.setIdNumber(request.getIdNumber());
        customer.setNotes(request.getNotes());

        customer = customerRepository.save(customer);
        log.info("Customer updated: {}", customer.getFullName());
        return toResponse(customer);
    }

    @Transactional
    public void deleteCustomer(Long ownerId, Long customerId) {
        Customer customer = findCustomer(ownerId, customerId);
        customer.softDelete();
        customerRepository.save(customer);
        log.info("Customer soft-deleted: {}", customer.getFullName());
    }

    public List<CustomerResponse> searchCustomers(Long ownerId, String query) {
        return customerRepository.searchByNameOrPhone(ownerId, query)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Customer findCustomer(Long ownerId, Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        if (!customer.getOwner().getId().equals(ownerId) || customer.isDeleted()) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }

        return customer;
    }

    private CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .idType(customer.getIdType())
                .idNumber(customer.getIdNumber())
                .notes(customer.getNotes())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
