package rongsokan.indah.services;

import java.math.BigDecimal;
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
import rongsokan.indah.utils.Types;

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

        if (Types.isBigDecimal(cari)) {
            BigDecimal jumlah = new BigDecimal(cari);

            return isAdmin
                    ? modalRepository.findByJumlah(jumlah, pageable)
                    : modalRepository.findByAnakBuah_IdAndJumlah(pengguna.getId(), jumlah, pageable);
        }

        if (Dates.isLocalDate(cari)) {
            LocalDate date = Dates.parseDate(cari);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            return isAdmin
                    ? modalRepository.findByTanggalBetween(start, end, pageable)
                    : modalRepository.findByAnakBuah_IdAndTanggalBetween(pengguna.getId(), start, end, pageable);
        }

        return isAdmin
                ? modalRepository.findByAnakBuah_NamaContainingIgnoreCase(cari, pageable)
                : modalRepository.findByAnakBuah_IdAndAnakBuah_NamaContainingIgnoreCase(pengguna.getId(), cari, pageable);
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