package rongsokan.indah.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import rongsokan.indah.constants.MessageConstant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnakBuah {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String nama;

    @NotBlank
    @Column(length = 20, nullable = false)
    @Pattern(regexp = "^\\+?\\d{8,20}$", message = MessageConstant.INVALID_WHATSAPP_NUMBER)
    private String nomorWhatsapp;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}