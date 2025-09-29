package com.keviny.customercontact.controller;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.model.Contact;
import com.keviny.customercontact.repository.ContactRepository;
import com.keviny.customercontact.service.ContactService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ContactControllerFindByIdTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ContactRepository contactRepository;

    @Test
    public void testFindById_ExistingContact_ReturnsContact() {
        // Arrange - Create a test contact first
        ContactDto createRequest = new ContactDto();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setEmail("john.findbyid@example.com");
        createRequest.setPhone("123-456-7890");
        createRequest.setPrimaryPhone("123-456-7890");
        createRequest.setPrimaryEmail("john.findbyid@example.com");

        // Create the contact
        ResponseEntity<ContactDto> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/contacts",
                createRequest,
                ContactDto.class
        );

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Long contactId = createResponse.getBody().getId();
        assertNotNull(contactId);

        // Act - Retrieve the contact by ID
        ResponseEntity<ContactDto> getResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/contacts/" + contactId,
                ContactDto.class
        );

        // Assert
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("John", getResponse.getBody().getFirstName());
        assertEquals("Doe", getResponse.getBody().getLastName());
        assertEquals("john.findbyid@example.com", getResponse.getBody().getEmail());
        assertEquals(contactId, getResponse.getBody().getId());
    }

    @Test
    public void testFindById_NonExistentContact_Returns404() {
        // Act - Try to retrieve a non-existent contact
        ResponseEntity<ContactDto> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/contacts/99999",
                ContactDto.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Note: Testing invalid ID (non-numeric) is complex with TestRestTemplate
    // as Spring Boot's type conversion handles this at the framework level
    // The main findById functionality is tested with the other two tests above
}