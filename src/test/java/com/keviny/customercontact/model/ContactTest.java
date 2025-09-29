package com.keviny.customercontact.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContactTest {

    @Test
    void defaultConstructor_ShouldCreateEmptyContact() {
        // When
        Contact contact = new Contact();

        // Then
        assertNull(contact.getId());
        assertNull(contact.getFirstName());
        assertNull(contact.getLastName());
        assertNull(contact.getEmail());
        assertNull(contact.getPhone());
        assertNull(contact.getAddress());
        assertNull(contact.getPrimaryPhone());
        assertNull(contact.getPrimaryEmail());
    }

    @Test
    void parameterizedConstructor_ShouldCreateContactWithValues() {
        // When
        Contact contact = new Contact("John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");

        // Then
        assertNull(contact.getId()); // ID should not be set by constructor
        assertEquals("John", contact.getFirstName());
        assertEquals("Doe", contact.getLastName());
        assertEquals("john.doe@email.com", contact.getEmail());
        assertEquals("123-456-7890", contact.getPhone());
        assertEquals("123 Main St", contact.getAddress());
        assertNull(contact.getPrimaryPhone()); // Should be null by default
        assertNull(contact.getPrimaryEmail()); // Should be null by default
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        Contact contact = new Contact();

        // When
        contact.setId(1L);
        contact.setFirstName("Jane");
        contact.setLastName("Smith");
        contact.setEmail("jane.smith@email.com");
        contact.setPhone("987-654-3210");
        contact.setAddress("456 Oak Ave");
        contact.setPrimaryPhone("555-123-4567");
        contact.setPrimaryEmail("primary@email.com");

        // Then
        assertEquals(1L, contact.getId());
        assertEquals("Jane", contact.getFirstName());
        assertEquals("Smith", contact.getLastName());
        assertEquals("jane.smith@email.com", contact.getEmail());
        assertEquals("987-654-3210", contact.getPhone());
        assertEquals("456 Oak Ave", contact.getAddress());
        assertEquals("555-123-4567", contact.getPrimaryPhone());
        assertEquals("primary@email.com", contact.getPrimaryEmail());
    }

    @Test
    void setId_ShouldAcceptNull() {
        // Given
        Contact contact = new Contact();
        contact.setId(1L);

        // When
        contact.setId(null);

        // Then
        assertNull(contact.getId());
    }

    @Test
    void setEmail_ShouldAcceptValidEmail() {
        // Given
        Contact contact = new Contact();

        // When
        contact.setEmail("test@example.com");

        // Then
        assertEquals("test@example.com", contact.getEmail());
    }

    @Test
    void setPrimaryEmail_ShouldAcceptNull() {
        // Given
        Contact contact = new Contact();
        contact.setPrimaryEmail("primary@email.com");

        // When
        contact.setPrimaryEmail(null);

        // Then
        assertNull(contact.getPrimaryEmail());
    }

    @Test
    void constructor_ShouldHandleNullValues() {
        // When
        Contact contact = new Contact(null, null, null, null, null);

        // Then
        assertNull(contact.getId());
        assertNull(contact.getFirstName());
        assertNull(contact.getLastName());
        assertNull(contact.getEmail());
        assertNull(contact.getPhone());
        assertNull(contact.getAddress());
    }

    @Test
    void contact_ShouldAllowLongNames() {
        // Given
        String longName = "a".repeat(100); // 100 characters (at the limit)
        Contact contact = new Contact();

        // When
        contact.setFirstName(longName);
        contact.setLastName(longName);

        // Then
        assertEquals(longName, contact.getFirstName());
        assertEquals(longName, contact.getLastName());
    }

    @Test
    void contact_ShouldAllowLongEmail() {
        // Given
        String longEmailLocal = "a".repeat(90); // Creates a long but valid email
        String longEmail = longEmailLocal + "@test.com";
        Contact contact = new Contact();

        // When
        contact.setEmail(longEmail);

        // Then
        assertEquals(longEmail, contact.getEmail());
    }

    @Test
    void contact_ShouldAllowLongAddress() {
        // Given
        String longAddress = "a".repeat(200); // 200 characters (at the limit)
        Contact contact = new Contact();

        // When
        contact.setAddress(longAddress);

        // Then
        assertEquals(longAddress, contact.getAddress());
    }

    @Test
    void contact_ShouldAllowLongPhoneNumbers() {
        // Given
        String longPhone = "1".repeat(20); // 20 characters (at the limit)
        Contact contact = new Contact();

        // When
        contact.setPhone(longPhone);
        contact.setPrimaryPhone(longPhone);

        // Then
        assertEquals(longPhone, contact.getPhone());
        assertEquals(longPhone, contact.getPrimaryPhone());
    }
}