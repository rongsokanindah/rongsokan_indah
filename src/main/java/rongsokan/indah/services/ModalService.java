package rongsokan.indah.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import rongsokan.indah.entities.Modal;
import rongsokan.indah.entities.Pengguna;
import rongsokan.indah.repositories.ModalRepository;
import rongsokan.indah.repositories.PenggunaRepository;
import rongsokan.indah.utils.Dates;

@Service
public class ModalService {

    @Autowired
    private ModalRepository modalRepository;

    @Autowired
    private PenggunaRepository penggunaRepository;

    public Modal saveModal(Modal modal) {
        return modalRepository.save(modal);
    }

    public Page<Modal> getModal(String cari, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Pengguna pengguna = penggunaRepository.findByUsername(auth.getName()).get();
        boolean isAdmin = pengguna.getRole().equals(Pengguna.Role.ADMIN);

        if (cari.isEmpty()) {
            return isAdmin
                    ? modalRepository.findAll(pageable)
                    : modalRepository.findByAnakBuah_Id(pengguna.getId(), pageable);
        }

        if (Dates.isLocalDate(cari)) {
            LocalDate date = LocalDate.parse(cari);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            return isAdmin
                    ? modalRepository.findByTanggalBetween(start, end, pageable)
                    : modalRepository.findByAnakBuah_IdAndTanggalBetween(pengguna.getId(), start, end, pageable);
        }

        return isAdmin
                ? modalRepository.findByAnakBuah_NamaContainingIgnoreCaseOrJumlah(cari, cari, pageable)
                : modalRepository.findByJumlah(cari, pageable);
    }

    public Modal updateModal(Modal modal) {
        return modalRepository.findById(modal.getId()).map(data -> {
            Optional.ofNullable(modal.getAnakBuah()).ifPresent(data::setAnakBuah);
            Optional.ofNullable(modal.getJumlah()).ifPresent(data::setJumlah);

            return modalRepository.save(data);
        }).orElseThrow(() -> new EntityNotFoundException(""));
    }

    public void deleteModal(UUID id) {
        modalRepository.deleteById(id);
    }
}