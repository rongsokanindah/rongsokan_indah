package rongsokan.indah.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaksiKeluar {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "barang_id", nullable = false)
    private Barang barang;

    @NotBlank
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal beratKg;

    @NotBlank
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal hargaJual;

    @NotBlank
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime tanggal;
}