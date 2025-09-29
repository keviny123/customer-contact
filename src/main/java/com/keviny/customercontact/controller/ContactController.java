package com.keviny.customercontact.controller;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.mapper.ContactMapper;
import com.keviny.customercontact.model.Contact;
import com.keviny.customercontact.service.ContactService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contacts")
@Validated
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @GetMapping
    public ResponseEntity<List<ContactDto>> getAllContacts() {
        logger.info("Received request to get all contacts");
        
        List<Contact> contacts = contactService.findAllContacts();
        List<ContactDto> contactDtos = contacts.stream()
                .map(ContactMapper::toDto)
                .collect(Collectors.toList());
                
        logger.info("Returning {} contacts", contactDtos.size());
        return ResponseEntity.ok(contactDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(
            @PathVariable @Min(value = 1, message = "Contact ID must be positive") Long id) {
        logger.info("Received request to get contact by ID: {}", id);
        
        return contactService.findContactById(id)
                .map(contact -> {
                    logger.info("Found contact with ID: {}", id);
                    return ContactMapper.toDto(contact);
                })
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.info("Contact not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<ContactDto> createOrUpdateContact(@Valid @RequestBody ContactDto contactDto) {
        logger.info("Received request to create/update contact with email: {}", contactDto.getEmail());
        
        Contact savedContact = contactService.createOrUpdateContact(contactDto);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedContact.getId())
                .toUri();
                
        ContactDto responseDto = ContactMapper.toDto(savedContact);
        logger.info("Successfully created/updated contact with ID: {}", savedContact.getId());
        
        return ResponseEntity.created(location).body(responseDto);
    }
}