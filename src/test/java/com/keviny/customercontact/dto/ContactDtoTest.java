package com.keviny.customercontact.dto;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ContactDtoTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void constructor_ShouldCreateContactDto_WithAllParameters() {
        // When
        ContactDto contactDto = new ContactDto(1L, "John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");

        // Then
        assertEquals(1L, contactDto.getId());
        assertEquals("John", contactDto.getFirstName());
        assertEquals("Doe", contactDto.getLastName());
        assertEquals("john.doe@email.com", contactDto.getEmail());
        assertEquals("123-456-7890", contactDto.getPhone());
        assertEquals("123 Main St", contactDto.getAddress());
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyContactDto() {
        // When
        ContactDto contactDto = new ContactDto();

        // Then
        assertNull(contactDto.getId());
        assertNull(contactDto.getFirstName());
        assertNull(contactDto.getLastName());
        assertNull(contactDto.getEmail());
        assertNull(contactDto.getPhone());
        assertNull(contactDto.getAddress());
        assertNull(contactDto.getPrimaryPhone());
        assertNull(contactDto.getPrimaryEmail());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        ContactDto contactDto = new ContactDto();

        // When
        contactDto.setId(2L);
        contactDto.setFirstName("Jane");
        contactDto.setLastName("Smith");
        contactDto.setEmail("jane.smith@email.com");
        contactDto.setPhone("987-654-3210");
        contactDto.setAddress("456 Oak Ave");
        contactDto.setPrimaryPhone("555-123-4567");
        contactDto.setPrimaryEmail("primary@email.com");

        // Then
        assertEquals(2L, contactDto.getId());
        assertEquals("Jane", contactDto.getFirstName());
        assertEquals("Smith", contactDto.getLastName());
        assertEquals("jane.smith@email.com", contactDto.getEmail());
        assertEquals("987-654-3210", contactDto.getPhone());
        assertEquals("456 Oak Ave", contactDto.getAddress());
        assertEquals("555-123-4567", contactDto.getPrimaryPhone());
        assertEquals("primary@email.com", contactDto.getPrimaryEmail());
    }

    @Test
    void validation_ShouldPass_WithValidData() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_ShouldFail_WhenFirstNameIsBlank() {
        // Given
        ContactDto contactDto = new ContactDto(null, "", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("First name is required")));
    }

    @Test
    void validation_ShouldFail_WhenFirstNameIsNull() {
        // Given
        ContactDto contactDto = new ContactDto(null, null, "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("First name is required")));
    }

    @Test
    void validation_ShouldFail_WhenLastNameIsBlank() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "", "john.doe@email.com", "123-456-7890", "123 Main St");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Last name is required")));
    }

    @Test
    void validation_ShouldFail_WhenEmailIsBlank() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "", "123-456-7890", "123 Main St");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email is required")));
    }

    @Test
    void validation_ShouldFail_WhenEmailIsInvalid() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "invalid-email", "123-456-7890", "123 Main St");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email should be valid")));
    }

    @Test
    void validation_ShouldFail_WhenFirstNameExceedsMaxLength() {
        // Given
        String longFirstName = "a".repeat(101); // Exceeds 100 character limit
        ContactDto contactDto = new ContactDto(null, longFirstName, "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("First name must not exceed 100 characters")));
    }

    @Test
    void validation_ShouldFail_WhenLastNameExceedsMaxLength() {
        // Given
        String longLastName = "a".repeat(101); // Exceeds 100 character limit
        ContactDto contactDto = new ContactDto(null, "John", longLastName, "john.doe@email.com", "123-456-7890", "123 Main St");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Last name must not exceed 100 characters")));
    }

    @Test
    void validation_ShouldFail_WhenEmailExceedsMaxLength() {
        // Given
        String longEmail = "a".repeat(95) + "@email.com"; // 95 + 10 = 105 characters, exceeds 100 limit
        ContactDto contactDto = new ContactDto(null, "John", "Doe", longEmail, "123-456-7890", "123 Main St");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email must not exceed 100 characters")));
    }

    @Test
    void validation_ShouldFail_WhenPhoneExceedsMaxLength() {
        // Given
        String longPhone = "1".repeat(21); // Exceeds 20 character limit
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "john.doe@email.com", longPhone, "123 Main St");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Phone must not exceed 20 characters")));
    }

    @Test
    void validation_ShouldFail_WhenAddressExceedsMaxLength() {
        // Given
        String longAddress = "a".repeat(201); // Exceeds 200 character limit
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "john.doe@email.com", "123-456-7890", longAddress);

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Address must not exceed 200 characters")));
    }

    @Test
    void validation_ShouldFail_WhenPrimaryEmailIsInvalid() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");
        contactDto.setPrimaryEmail("invalid-email");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Primary email should be valid")));
    }

    @Test
    void validation_ShouldPass_WhenOptionalFieldsAreNull() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "john.doe@email.com", null, null);
        contactDto.setPrimaryPhone(null);
        contactDto.setPrimaryEmail(null);

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_ShouldPass_WhenPrimaryEmailIsValid() {
        // Given
        ContactDto contactDto = new ContactDto(null, "John", "Doe", "john.doe@email.com", "123-456-7890", "123 Main St");
        contactDto.setPrimaryEmail("primary@email.com");

        // When
        Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

        // Then
        assertTrue(violations.isEmpty());
    }
}