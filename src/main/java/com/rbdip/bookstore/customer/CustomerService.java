package com.rbdip.bookstore.customer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer findOrRegistryByPhone(String phone, String name, String address) {
        return customerRepository.findByPhone(phone)
                .orElseGet(() -> customerRepository.save(new Customer(null, name, address, phone)));
    }
}
