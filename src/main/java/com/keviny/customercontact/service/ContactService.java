package com.keviny.customercontact.service;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.mapper.ContactMapper;
import com.keviny.customercontact.model.Contact;
import com.keviny.customercontact.repository.ContactRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);
    private static final String CONTACT_SERVICE = "contactService";

    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    @CircuitBreaker(name = CONTACT_SERVICE, fallbackMethod = "createContactFallback")
    @Retry(name = CONTACT_SERVICE)
    public Contact createOrUpdateContact(ContactDto contactDto) {
        logger.info("Creating or updating contact with email: {}", contactDto.getEmail());
        
        if (!StringUtils.hasText(contactDto.getEmail())) {
            throw new IllegalArgumentException("Email is required for contact creation/update");
        }
        
        try {
            Contact contact = contactRepository.findByEmail(contactDto.getEmail())
                    .orElseGet(() -> {
                        logger.info("Creating new contact for email: {}", contactDto.getEmail());
                        return new Contact();
                    });
            
            ContactMapper.toEntity(contactDto, contact);
            Contact savedContact = contactRepository.save(contact);
            
            logger.info("Successfully saved contact with ID: {}", savedContact.getId());
            return savedContact;
            
        } catch (DataAccessException e) {
            logger.error("Database error while saving contact: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while saving contact: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create/update contact", e);
        }
    }
    
    @Transactional(readOnly = true)
    @CircuitBreaker(name = CONTACT_SERVICE, fallbackMethod = "findContactByIdFallback")
    @Retry(name = CONTACT_SERVICE)
    public Optional<Contact> findContactById(Long id) {
        logger.debug("Finding contact by ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Contact ID must be a positive number");
        }
        
        try {
            return contactRepository.findById(id);
        } catch (DataAccessException e) {
            logger.error("Database error while finding contact by ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    @CircuitBreaker(name = CONTACT_SERVICE, fallbackMethod = "findAllContactsFallback")
    @Retry(name = CONTACT_SERVICE)
    public List<Contact> findAllContacts() {
        logger.debug("Finding all contacts");
        
        try {
            List<Contact> contacts = contactRepository.findAll();
            logger.info("Found {} contacts", contacts.size());
            return contacts;
        } catch (DataAccessException e) {
            logger.error("Database error while finding all contacts: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    @CircuitBreaker(name = CONTACT_SERVICE, fallbackMethod = "findContactByEmailFallback")
    @Retry(name = CONTACT_SERVICE)
    public Optional<Contact> findContactByEmail(String email) {
        logger.debug("Finding contact by email: {}", email);
        
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        try {
            return contactRepository.findByEmail(email);
        } catch (DataAccessException e) {
            logger.error("Database error while finding contact by email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }
    
    // Fallback methods for circuit breaker
    public Contact createContactFallback(ContactDto contactDto, Exception ex) {
        logger.error("Circuit breaker activated for createOrUpdateContact: {}", ex.getMessage());
        throw new RuntimeException("Contact service is temporarily unavailable. Please try again later.");
    }
    
    public Optional<Contact> findContactByIdFallback(Long id, Exception ex) {
        logger.error("Circuit breaker activated for findContactById: {}", ex.getMessage());
        return Optional.empty();
    }
    
    public List<Contact> findAllContactsFallback(Exception ex) {
        logger.error("Circuit breaker activated for findAllContacts: {}", ex.getMessage());
        return List.of(); // Return empty list as fallback
    }
    
    public Optional<Contact> findContactByEmailFallback(String email, Exception ex) {
        logger.error("Circuit breaker activated for findContactByEmail: {}", ex.getMessage());
        return Optional.empty();
    }
}