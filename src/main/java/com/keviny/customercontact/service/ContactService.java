package com.keviny.customercontact.service;

import com.keviny.customercontact.dto.ContactDto;
import com.keviny.customercontact.mapper.ContactMapper;
import com.keviny.customercontact.model.Contact;
import com.keviny.customercontact.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public Contact createOrUpdateContact(ContactDto contactDto) {
        Contact contact = contactRepository.findByEmail(contactDto.getEmail())
                .orElseGet(Contact::new);
        
        ContactMapper.toEntity(contactDto, contact);
        return contactRepository.save(contact);
    }
}