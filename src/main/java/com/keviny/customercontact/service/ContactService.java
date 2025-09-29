package com.keviny.customercontact.service;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.mapper.ContactMapper;
import com.keviny.customercontact.model.Contact;
import com.keviny.customercontact.repository.ContactRepository;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Singleton
public class ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Transactional
    public Contact createOrUpdateContact(ContactDto contactDto) {
        logger.info("Creating or updating contact with email: {}", contactDto.getEmail());
        
        if (contactDto.getEmail() == null || contactDto.getEmail().trim().isEmpty()) {
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
            
        } catch (RuntimeException e) {
            logger.error("Database error while saving contact: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while saving contact: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create/update contact", e);
        }
    }
    
    @Transactional(readOnly = true)
    public Optional<Contact> findContactById(Long id) {
        logger.debug("Finding contact by ID: {}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Contact ID must be a positive number");
        }
        
        try {
            return contactRepository.findById(id);
        } catch (RuntimeException e) {
            logger.error("Database error while finding contact by ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public List<Contact> findAllContacts() {
        logger.debug("Finding all contacts");
        
        try {
            List<Contact> contacts = contactRepository.findAll();
            logger.info("Found {} contacts", contacts.size());
            return contacts;
        } catch (RuntimeException e) {
            logger.error("Database error while finding all contacts: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Transactional(readOnly = true)
    public Optional<Contact> findContactByEmail(String email) {
        logger.debug("Finding contact by email: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        try {
            return contactRepository.findByEmail(email);
        } catch (RuntimeException e) {
            logger.error("Database error while finding contact by email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }
}