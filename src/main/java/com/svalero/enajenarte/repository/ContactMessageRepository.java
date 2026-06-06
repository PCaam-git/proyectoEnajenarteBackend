package com.svalero.enajenarte.repository;

import com.svalero.enajenarte.domain.ContactMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ContactMessageRepository extends CrudRepository<ContactMessage, Long> {

    List<ContactMessage> findAll();

    List<ContactMessage> findByCategory(String category);
    List<ContactMessage> findByEmailContainingIgnoreCase(String email);
}