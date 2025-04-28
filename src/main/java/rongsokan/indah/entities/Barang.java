package rongsokan.indah.entities;

import java.math.BigDecimal;
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
import jakarta.validation.constraints.PositiveOrZero;
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
public class Barang {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String namaBarang;

    @NotBlank
    @Column(precision = 10, scale = 2, nullable = false)
    @PositiveOrZero(message = MessageConstant.INVALID_PRICE_PER_KG)
    private BigDecimal hargaPerKg;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}