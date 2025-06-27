package rongsokan.indah.repositories;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rongsokan.indah.entities.Modal;

@Repository
public interface ModalRepository extends JpaRepository<Modal, UUID> {

    Page<Modal> findByAnakBuah_Id(UUID id, Pageable pageable);

    Page<Modal> findByJumlah(String jumlah, Pageable pageable);

    Page<Modal> findByTanggalBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Modal> findByAnakBuah_NamaContainingIgnoreCaseOrJumlah(String nama, String jumlah, Pageable pageable);

    Page<Modal> findByAnakBuah_IdAndTanggalBetween(UUID id, LocalDateTime start, LocalDateTime end, Pageable pageable);
}