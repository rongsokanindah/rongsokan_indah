package rongsokan.indah.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rongsokan.indah.entities.TransaksiKeluar;

@Repository
public interface TransaksiKeluarRepository extends JpaRepository<TransaksiKeluar, UUID> {

    Page<TransaksiKeluar> findByBarang_NamaBarangContainingIgnoreCase(String namaBarang, Pageable pageable);

    Page<TransaksiKeluar> findByBeratKgOrHargaJual(BigDecimal beratKg, BigDecimal hargaJual, Pageable pageable);

    Page<TransaksiKeluar> findByTanggalBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}