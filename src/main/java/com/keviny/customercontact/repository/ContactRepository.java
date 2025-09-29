package com.keviny.customercontact.repository;

import com.keviny.customercontact.model.Contact;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    Optional<Contact> findByEmail(String email);
    
    boolean existsByEmail(String email);
}