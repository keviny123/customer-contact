package com.keviny.customercontact.repository;

import com.keviny.customercontact.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    Optional<Contact> findByEmail(String email);
    
    boolean existsByEmail(String email);
}