package com.keviny.customercontact.mapper;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.model.Contact;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContactMapperTest {

    @Test
    void toDto_ShouldMapContactToDto_WhenContactIsValid() {
        // Given
        Contact contact = new Contact("John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");
        contact.setId(1L);
        contact.setPrimaryPhone("555-123-4567");
        contact.setPrimaryEmail("primary@email.com");

        // When
        ContactDto result = ContactMapper.toDto(contact);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@email.com", result.getEmail());
        assertEquals("123-456-7890", result.getPhone());
        assertEquals("123 Main St", result.getAddress());
        assertEquals("555-123-4567", result.getPrimaryPhone());
        assertEquals("primary@email.com", result.getPrimaryEmail());
    }

    @Test
    void toDto_ShouldReturnNull_WhenContactIsNull() {
        // When
        ContactDto result = ContactMapper.toDto(null);

        // Then
        assertNull(result);
    }

    @Test
    void toDto_ShouldHandleContactWithNullFields() {
        // Given
        Contact contact = new Contact();
        contact.setId(1L);
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setEmail("john.doe@email.com");
        // phone, address, primaryPhone, primaryEmail are null

        // When
        ContactDto result = ContactMapper.toDto(contact);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@email.com", result.getEmail());
        assertNull(result.getPhone());
        assertNull(result.getAddress());
        assertNull(result.getPrimaryPhone());
        assertNull(result.getPrimaryEmail());
    }

    @Test
    void toEntity_ShouldMapDtoToNewEntity_WhenEntityIsNew() {
        // Given
        ContactDto contactDto = new ContactDto(null, "Jane", "Smith", "jane.smith@email.com", "987-654-3210", "456 Oak Ave");
        contactDto.setPrimaryPhone("555-987-6543");
        contactDto.setPrimaryEmail("primary.jane@email.com");
        Contact existingContact = new Contact();

        // When
        Contact result = ContactMapper.toEntity(contactDto, existingContact);

        // Then
        assertNotNull(result);
        assertSame(existingContact, result); // Should modify the same instance
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@email.com", result.getEmail());
        assertEquals("987-654-3210", result.getPhone());
        assertEquals("456 Oak Ave", result.getAddress());
        assertEquals("555-987-6543", result.getPrimaryPhone());
        assertEquals("primary.jane@email.com", result.getPrimaryEmail());
    }

    @Test
    void toEntity_ShouldMapDtoToExistingEntity_WhenEntityExists() {
        // Given
        ContactDto contactDto = new ContactDto(null, "Jane", "Smith", "jane.smith@email.com", "987-654-3210", "456 Oak Ave");
        contactDto.setPrimaryPhone("555-987-6543");
        contactDto.setPrimaryEmail("primary.jane@email.com");
        
        Contact existingContact = new Contact("Old", "Name", "old@email.com", "111-111-1111", "Old Address");
        existingContact.setId(5L); // Existing entity with ID

        // When
        Contact result = ContactMapper.toEntity(contactDto, existingContact);

        // Then
        assertNotNull(result);
        assertSame(existingContact, result); // Should modify the same instance
        assertEquals(5L, result.getId()); // ID should be preserved
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("old@email.com", result.getEmail()); // Email should NOT be updated for existing entities
        assertEquals("987-654-3210", result.getPhone());
        assertEquals("456 Oak Ave", result.getAddress());
        assertEquals("555-987-6543", result.getPrimaryPhone());
        assertEquals("primary.jane@email.com", result.getPrimaryEmail());
    }

    @Test
    void toEntity_ShouldReturnNull_WhenDtoIsNull() {
        // Given
        Contact existingContact = new Contact();

        // When
        Contact result = ContactMapper.toEntity(null, existingContact);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_ShouldHandleDtoWithNullFields() {
        // Given
        ContactDto contactDto = new ContactDto();
        contactDto.setFirstName("John");
        contactDto.setLastName("Doe");
        contactDto.setEmail("john.doe@email.com");
        // phone, address, primaryPhone, primaryEmail are null
        
        Contact existingContact = new Contact();

        // When
        Contact result = ContactMapper.toEntity(contactDto, existingContact);

        // Then
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@email.com", result.getEmail());
        assertNull(result.getPhone());
        assertNull(result.getAddress());
        assertNull(result.getPrimaryPhone());
        assertNull(result.getPrimaryEmail());
    }

    @Test
    void toEntity_ShouldSetEmailOnNewEntity() {
        // Given
        ContactDto contactDto = new ContactDto(null, "New", "User", "new.user@email.com", "123-456-7890", "123 Street");
        Contact newContact = new Contact(); // New entity (no ID)

        // When
        Contact result = ContactMapper.toEntity(contactDto, newContact);

        // Then
        assertNotNull(result);
        assertNull(result.getId()); // Should still be null (new entity)
        assertEquals("new.user@email.com", result.getEmail()); // Email should be set for new entities
    }

    @Test
    void toEntity_ShouldNotSetEmailOnExistingEntity() {
        // Given
        ContactDto contactDto = new ContactDto(null, "Updated", "User", "updated.email@email.com", "123-456-7890", "123 Street");
        Contact existingContact = new Contact("Old", "User", "original@email.com", "999-999-9999", "Old Street");
        existingContact.setId(10L); // Existing entity with ID

        // When
        Contact result = ContactMapper.toEntity(contactDto, existingContact);

        // Then
        assertNotNull(result);
        assertEquals(10L, result.getId()); // Should preserve existing ID
        assertEquals("original@email.com", result.getEmail()); // Email should NOT be updated for existing entities
        assertEquals("Updated", result.getFirstName()); // Other fields should be updated
        assertEquals("123-456-7890", result.getPhone());
    }
}