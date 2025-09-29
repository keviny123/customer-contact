package com.keviny.customercontact.service;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.model.Contact;
import com.keviny.customercontact.repository.ContactRepository;
import org.junit.jupiter.a    @Test
    void createContactFallback_ShouldThrowRuntimeException() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");
        Exception exception = new RuntimeException("Service unavailable");

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class, 
            () -> contactService.createContactFallback(contactDto, exception));
        
        assertEquals("Contact service is temporarily unavailable. Please try again later.", thrown.getMessage());
    }t org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        Contact savedContact = new Contact("John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");
        savedContact.setId(1L);
        
        when(contactRepository.findByEmail("john.doe@email.com")).thenReturn(Optional.empty());
        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        // When
        Contact result = contactService.createOrUpdateContact(contactDto);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@email.com", result.getEmail());
        assertEquals(1L, result.getId());
        verify(contactRepository).findByEmail("john.doe@email.com");
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void createOrUpdateContact_ShouldUpdateExistingContact() {
        // Given
        ContactDto contactDto = new ContactDto(null, "Jane", "Smith", "jane.smith@email.com", "987-654-3210", "456 Oak Ave");
        Contact existingContact = new Contact("Jane", "Doe", "jane.smith@email.com", "555-123-4567", "123 Old St");
        existingContact.setId(2L);
        Contact updatedContact = new Contact("Jane", "Smith", "jane.smith@email.com", "987-654-3210", "456 Oak Ave");
        updatedContact.setId(2L);
        
        when(contactRepository.findByEmail("jane.smith@email.com")).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);

        // When
        Contact result = contactService.createOrUpdateContact(contactDto);

        // Then
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("987-654-3210", result.getPhone());
        assertEquals("456 Oak Ave", result.getAddress());
        verify(contactRepository).findByEmail("jane.smith@email.com");
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void createOrUpdateContact_ShouldThrowException_WhenEmailIsBlank() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "", "123-456-7890", "123 Main St");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> contactService.createOrUpdateContact(contactDto));
        verify(contactRepository, never()).findByEmail(anyString());
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void createOrUpdateContact_ShouldThrowException_WhenEmailIsNull() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", null, "123-456-7890", "123 Main St");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> contactService.createOrUpdateContact(contactDto));
        verify(contactRepository, never()).findByEmail(anyString());
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void createOrUpdateContact_ShouldHandleDatabaseException() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");
        when(contactRepository.findByEmail("john.doe@email.com")).thenReturn(Optional.empty());
        when(contactRepository.save(any(Contact.class))).thenThrow(new DataAccessException("Database error") {});

        // When & Then
        assertThrows(DataAccessException.class, () -> contactService.createOrUpdateContact(contactDto));
        verify(contactRepository).findByEmail("john.doe@email.com");
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void findContactById_ShouldReturnContact_WhenExists() {
        // Given
        Long contactId = 1L;
        Contact expectedContact = new Contact("John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");
        expectedContact.setId(contactId);
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(expectedContact));

        // When
        Optional<Contact> result = contactService.findContactById(contactId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedContact, result.get());
        verify(contactRepository).findById(contactId);
    }

    @Test
    void findContactById_ShouldReturnEmpty_WhenNotExists() {
        // Given
        Long contactId = 999L;
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        // When
        Optional<Contact> result = contactService.findContactById(contactId);

        // Then
        assertFalse(result.isPresent());
        verify(contactRepository).findById(contactId);
    }

    @Test
    void findContactById_ShouldHandleDatabaseException() {
        // Given
        Long contactId = 1L;
        when(contactRepository.findById(contactId)).thenThrow(new DataAccessException("Database error") {});

        // When & Then
        assertThrows(DataAccessException.class, () -> contactService.findContactById(contactId));
        verify(contactRepository).findById(contactId);
    }

    @Test
    void findAllContacts_ShouldReturnAllContacts() {
        // Given
        Contact contact1 = new Contact("John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");
        Contact contact2 = new Contact("Jane", "Smith", "jane.smith@email.com", "987-654-3210", "456 Oak Ave");
        List<Contact> expectedContacts = Arrays.asList(contact1, contact2);
        when(contactRepository.findAll()).thenReturn(expectedContacts);

        // When
        List<Contact> result = contactService.findAllContacts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedContacts, result);
        verify(contactRepository).findAll();
    }

    @Test
    void findAllContacts_ShouldReturnEmptyList_WhenNoContacts() {
        // Given
        when(contactRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Contact> result = contactService.findAllContacts();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(contactRepository).findAll();
    }

    @Test
    void findAllContacts_ShouldHandleDatabaseException() {
        // Given
        when(contactRepository.findAll()).thenThrow(new DataAccessException("Database error") {});

        // When & Then
        assertThrows(DataAccessException.class, () -> contactService.findAllContacts());
        verify(contactRepository).findAll();
    }

    @Test
    void findContactByEmail_ShouldReturnContact_WhenExists() {
        // Given
        String email = "john.doe@email.com";
        Contact expectedContact = new Contact("John", "Doe", email, "123-456-7890", "123 Main St");
        when(contactRepository.findByEmail(email)).thenReturn(Optional.of(expectedContact));

        // When
        Optional<Contact> result = contactService.findContactByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedContact, result.get());
        verify(contactRepository).findByEmail(email);
    }

    @Test
    void findContactByEmail_ShouldReturnEmpty_WhenNotExists() {
        // Given
        String email = "nonexistent@email.com";
        when(contactRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<Contact> result = contactService.findContactByEmail(email);

        // Then
        assertFalse(result.isPresent());
        verify(contactRepository).findByEmail(email);
    }

    @Test
    void findContactByEmail_ShouldHandleDatabaseException() {
        // Given
        String email = "john.doe@email.com";
        when(contactRepository.findByEmail(email)).thenThrow(new DataAccessException("Database error") {});

        // When & Then
        assertThrows(DataAccessException.class, () -> contactService.findContactByEmail(email));
        verify(contactRepository).findByEmail(email);
    }

    // Test fallback methods (circuit breaker scenarios)
    @Test
    void createContactFallback_ShouldReturnNewContact() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");
        Exception exception = new RuntimeException("Service unavailable");

        // When
        Contact result = contactService.createContactFallback(contactDto, exception);

        // Then
        assertNotNull(result);
        assertEquals("Fallback", result.getFirstName());
        assertEquals("Contact", result.getLastName());
        assertEquals("fallback@email.com", result.getEmail());
    }

    @Test
    void findContactByIdFallback_ShouldReturnEmptyOptional() {
        // Given
        Long contactId = 1L;
        Exception exception = new RuntimeException("Service unavailable");

        // When
        Optional<Contact> result = contactService.findContactByIdFallback(contactId, exception);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findAllContactsFallback_ShouldReturnEmptyList() {
        // Given
        Exception exception = new RuntimeException("Service unavailable");

        // When
        List<Contact> result = contactService.findAllContactsFallback(exception);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findContactByEmailFallback_ShouldReturnEmptyOptional() {
        // Given
        String email = "john.doe@email.com";
        Exception exception = new RuntimeException("Service unavailable");

        // When
        Optional<Contact> result = contactService.findContactByEmailFallback(email, exception);

        // Then
        assertFalse(result.isPresent());
    }
}