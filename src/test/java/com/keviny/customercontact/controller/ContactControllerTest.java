package com.keviny.customercontact.controller;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.model.Contact;
import com.keviny.customercontact.repository.ContactRepository;
import com.keviny.customercontact.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactRepository contactRepository;

    @MockBean
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getContactById_ShouldReturnContact_WhenContactExists() throws Exception {
        // Given
        Contact contact = new Contact("John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");
        contact.setId(1L);
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));

        // When & Then
        mockMvc.perform(get("/api/contacts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@email.com"));
    }

    @Test
    void createContact_ShouldReturnCreatedContact() throws Exception {
        // Given
        ContactDto contactDto = new ContactDto(null, "Jane", "Smith", "jane.smith@email.com", "987-654-3210", "456 Oak Ave");
        Contact savedContact = new Contact("Jane", "Smith", "jane.smith@email.com", "987-654-3210", "456 Oak Ave");
        savedContact.setId(2L);
        when(contactService.createOrUpdateContact(any(ContactDto.class))).thenReturn(savedContact);

        // When & Then
        mockMvc.perform(post("/api/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }
}