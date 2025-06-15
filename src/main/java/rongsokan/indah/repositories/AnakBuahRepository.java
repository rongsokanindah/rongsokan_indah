package rongsokan.indah.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rongsokan.indah.entities.AnakBuah;

@Repository
public interface AnakBuahRepository extends JpaRepository<AnakBuah, UUID> {

    Page<AnakBuah> findByNamaContainingIgnoreCaseOrNomorWhatsAppContaining(String nama, String nomorWhatsApp, Pageable pageable);
}