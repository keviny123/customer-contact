package com.keviny.customercontact.service;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.model.Contact;
import com.keviny.customercontact.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @Test
    void createOrUpdateContact_ShouldCreateNewContact() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");
        when(contactRepository.findByEmail("john.doe@email.com")).thenReturn(Optional.empty());
        when(contactRepository.save(any(Contact.class))).thenReturn(new Contact("John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St"));

        // When
        Contact result = contactService.createOrUpdateContact(contactDto);

        // Then
        assertNotNull(result);
        verify(contactRepository).findByEmail("john.doe@email.com");
        verify(contactRepository).save(any(Contact.class));
    }
}