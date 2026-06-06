package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.ContactMessage;
import com.svalero.enajenarte.dto.ContactMessageInDto;
import com.svalero.enajenarte.dto.ContactMessageOutDto;
import com.svalero.enajenarte.exception.ContactMessageNotFoundException;
import com.svalero.enajenarte.repository.ContactMessageRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactMessageService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmailService emailService;

    // POST
    public ContactMessageOutDto add(ContactMessageInDto contactMessageInDto) {
        ContactMessage contactMessage = modelMapper.map(contactMessageInDto, ContactMessage.class);
        contactMessage.setCreatedAt(LocalDateTime.now());

        ContactMessage newContactMessage = contactMessageRepository.save(contactMessage);

        emailService.sendContactNotification(
                "Nuevo mensaje de contacto - " + newContactMessage.getCategory(),
                "Se ha recibido un nuevo mensaje de contacto.\n\n"
                        + "Nombre: " + newContactMessage.getFullName() + "\n"
                        + "Email: " + newContactMessage.getEmail() + "\n"
                        + "Categoría: " + newContactMessage.getCategory() + "\n"
                        + "Referencia: " + newContactMessage.getReferenceId() + "\n\n"
                        + "Mensaje:\n" + newContactMessage.getMessage()
        );

        return modelMapper.map(newContactMessage, ContactMessageOutDto.class);
    }

    // GET ALL
    public List<ContactMessageOutDto> findAll(String category, String email) {

        final String finalCategory = category.isEmpty() ? null : category;
        final String finalEmail = email.isEmpty() ? null : email.toLowerCase();

        List<ContactMessage> filteredMessages = contactMessageRepository.findAll().stream()
                .filter(contactMessage -> finalCategory == null || contactMessage.getCategory().equals(finalCategory))
                .filter(contactMessage -> finalEmail == null || contactMessage.getEmail().toLowerCase().contains(finalEmail))
                .toList();

        return modelMapper.map(filteredMessages, new TypeToken<List<ContactMessageOutDto>>() {}.getType());
    }

    // GET by id
    public ContactMessageOutDto findById(long id) throws ContactMessageNotFoundException {
        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(ContactMessageNotFoundException::new);

        return modelMapper.map(contactMessage, ContactMessageOutDto.class);
    }

    // DELETE
    public void delete(long id) throws ContactMessageNotFoundException {
        ContactMessage contactMessage = contactMessageRepository.findById(id)
                .orElseThrow(ContactMessageNotFoundException::new);

        contactMessageRepository.delete(contactMessage);
    }
}