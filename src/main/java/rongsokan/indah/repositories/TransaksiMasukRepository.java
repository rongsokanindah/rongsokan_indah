package rongsokan.indah.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import rongsokan.indah.entities.TransaksiMasuk;

@Repository
public interface TransaksiMasukRepository extends JpaRepository<TransaksiMasuk, UUID> {

    List<TransaksiMasuk> findByAnakBuah_Id(UUID id);

    Page<TransaksiMasuk> findByAnakBuah_Id(UUID id, Pageable pageable);

    Page<TransaksiMasuk> findByAnakBuah_IdAndBarang_NamaBarangContainingIgnoreCase(UUID id, String namaBarang, Pageable pageable);

    Page<TransaksiMasuk> findByAnakBuah_NamaContainingIgnoreCaseOrBarang_NamaBarangContainingIgnoreCase(String namaAnakBuah, String namaBarang, Pageable pageable);

    Page<TransaksiMasuk> findByTanggalBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<TransaksiMasuk> findByAnakBuah_IdAndTanggalBetween(UUID id, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<TransaksiMasuk> findByBeratKgOrTotalHarga(BigDecimal beratKg, BigDecimal totalHarga, Pageable pageable);

    @Query("SELECT t FROM TransaksiMasuk t WHERE t.anakBuah.id = :id AND (t.beratKg = :beratKg OR t.totalHarga = :totalHarga)")
    Page<TransaksiMasuk> findByAnakBuah_IdAndBeratKgOrTotalHarga(UUID id, BigDecimal beratKg, BigDecimal totalHarga, Pageable pageable);
}