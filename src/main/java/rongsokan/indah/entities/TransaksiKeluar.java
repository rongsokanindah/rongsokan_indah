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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
public class TransaksiKeluar {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "barang_id", nullable = false)
    private Barang barang;

    @Positive
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal beratKg;

    @Positive
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal hargaJual;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime tanggal;
}