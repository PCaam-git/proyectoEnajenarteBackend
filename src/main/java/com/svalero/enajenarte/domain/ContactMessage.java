package com.svalero.enajenarte.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ContactMessage")
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "full_name")
    @NotNull(message = "fullName is mandatory")
    private String fullName;

    @Column
    @NotNull(message = "email is mandatory")
    private String email;

    @Column
    @NotNull(message = "category is mandatory")
    private String category;

    @Column(name = "reference_id")
    @NotNull(message = "referenceId is mandatory")
    private Long referenceId;

    @Column(columnDefinition = "TEXT")
    @NotNull(message = "message is mandatory")
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}