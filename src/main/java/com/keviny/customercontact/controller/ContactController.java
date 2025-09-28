package com.keviny.customercontact.controller;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.mapper.ContactMapper;
import com.keviny.customercontact.model.Contact;
import com.keviny.customercontact.repository.ContactRepository;
import com.keviny.customercontact.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ContactService contactService;

    @GetMapping("/{id}")
    public ResponseEntity<ContactDto> getContactById(@PathVariable Long id) {
        return contactRepository.findById(id)
                .map(ContactMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContactDto> createOrUpdateContact(@Valid @RequestBody ContactDto contactDto) {
        Contact savedContact = contactService.createOrUpdateContact(contactDto);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedContact.getId())
                .toUri();
                
        return ResponseEntity.created(location).body(ContactMapper.toDto(savedContact));
    }
}