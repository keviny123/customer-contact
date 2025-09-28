package com.keviny.customercontact.mapper;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.model.Contact;

public class ContactMapper {

    public static ContactDto toDto(Contact contact) {
        if (contact == null) {
            return null;
        }
        return new ContactDto(
            contact.getId(),
            contact.getFirstName(),
            contact.getLastName(),
            contact.getEmail(),
            contact.getPhone(),
            contact.getAddress()
        );
    }

    public static Contact toEntity(ContactDto contactDto, Contact existingContact) {
        if (contactDto == null) {
            return null;
        }
        
        // Don't map ID to entity - let JPA handle it
        // Only set email on new entities (when existingContact.getId() == null)
        if (existingContact.getId() == null) {
            existingContact.setEmail(contactDto.getEmail());
        }
        
        existingContact.setFirstName(contactDto.getFirstName());
        existingContact.setLastName(contactDto.getLastName());
        existingContact.setPhone(contactDto.getPhone());
        existingContact.setAddress(contactDto.getAddress());
        
        return existingContact;
    }
}