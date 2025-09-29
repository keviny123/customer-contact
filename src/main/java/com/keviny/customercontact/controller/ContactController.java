package com.keviny.customercontact.controller;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.mapper.ContactMapper;
import com.keviny.customercontact.model.Contact;
import com.keviny.customercontact.service.ContactService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/api/contacts")
@Validated
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @Get
    public HttpResponse<List<ContactDto>> getAllContacts() {
        logger.info("Received request to get all contacts");
        
        List<Contact> contacts = contactService.findAllContacts();
        List<ContactDto> contactDtos = contacts.stream()
                .map(ContactMapper::toDto)
                .collect(Collectors.toList());
                
        logger.info("Returning {} contacts", contactDtos.size());
        return HttpResponse.ok(contactDtos);
    }

    @Get("/{id}")
    public HttpResponse<ContactDto> getContactById(
            @PathVariable @Min(value = 1, message = "Contact ID must be positive") Long id) {
        logger.info("Received request to get contact by ID: {}", id);
        
        return contactService.findContactById(id)
                .map(contact -> {
                    logger.info("Found contact with ID: {}", id);
                    return ContactMapper.toDto(contact);
                })
                .map(HttpResponse::ok)
                .orElseGet(() -> {
                    logger.info("Contact not found with ID: {}", id);
                    return HttpResponse.notFound();
                });
    }

    @Post
    public HttpResponse<ContactDto> createOrUpdateContact(@Valid @Body ContactDto contactDto) {
        logger.info("Received request to create/update contact with email: {}", contactDto.getEmail());
        
        Contact savedContact = contactService.createOrUpdateContact(contactDto);
        
        URI location = URI.create("/api/contacts/" + savedContact.getId());
                
        ContactDto responseDto = ContactMapper.toDto(savedContact);
        logger.info("Successfully created/updated contact with ID: {}", savedContact.getId());
        
        return HttpResponse.created(responseDto).headers(headers -> 
            headers.location(location));
    }
}