package rongsokan.indah.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import rongsokan.indah.entities.Pengguna;
import rongsokan.indah.entities.TransaksiMasuk;
import rongsokan.indah.repositories.PenggunaRepository;
import rongsokan.indah.repositories.TransaksiMasukRepository;
import rongsokan.indah.utils.Dates;
import rongsokan.indah.utils.Types;

@Service
public class TransaksiMasukService {

    @Autowired
    private TransaksiMasukRepository transaksiMasukRepository;

    @Autowired
    private PenggunaRepository penggunaRepository;

    public TransaksiMasuk saveTransaksiMasuk(TransaksiMasuk transaksiMasuk) {
        return transaksiMasukRepository.save(transaksiMasuk);
    }

    public Page<TransaksiMasuk> getTransaksiMasuk(String cari, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Pengguna pengguna = penggunaRepository.findByUsername(auth.getName()).get();
        boolean isAdmin = pengguna.getRole().equals(Pengguna.Role.ADMIN);

        if (cari.isEmpty()) {
            return isAdmin
                    ? transaksiMasukRepository.findAll(pageable)
                    : transaksiMasukRepository.findByAnakBuah_Id(pengguna.getAnakBuah().getId(), pageable);
        }

        if (Types.isBigDecimal(cari)) {
            BigDecimal decimal = new BigDecimal(cari);

            return isAdmin
                    ? transaksiMasukRepository.findByBeratKgOrTotalHarga(decimal, decimal, pageable)
                    : transaksiMasukRepository.findByAnakBuah_IdAndBeratKgOrTotalHarga(pengguna.getAnakBuah().getId(), decimal, decimal, pageable);
        }

        if (Dates.isLocalDate(cari)) {
            LocalDate date = Dates.parseDate(cari);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            return isAdmin
                    ? transaksiMasukRepository.findByTanggalBetween(start, end, pageable)
                    : transaksiMasukRepository.findByAnakBuah_IdAndTanggalBetween(pengguna.getAnakBuah().getId(), start, end, pageable);
        }

        return isAdmin
                ? transaksiMasukRepository.findByAnakBuah_NamaContainingIgnoreCaseOrBarang_NamaBarangContainingIgnoreCase(cari, cari, pageable)
                : transaksiMasukRepository.findByAnakBuah_IdAndBarang_NamaBarangContainingIgnoreCase(pengguna.getAnakBuah().getId(), cari, pageable);
    }

    public TransaksiMasuk updateTransaksiMasuk(TransaksiMasuk transaksiMasuk) {
        return transaksiMasukRepository.findById(transaksiMasuk.getId()).map(data -> {
            Optional.ofNullable(transaksiMasuk.getBarang()).ifPresent(data::setBarang);
            Optional.ofNullable(transaksiMasuk.getBeratKg()).ifPresent(data::setBeratKg);
            Optional.ofNullable(transaksiMasuk.getAnakBuah()).ifPresent(data::setAnakBuah);
            Optional.ofNullable(transaksiMasuk.getTotalHarga()).ifPresent(data::setTotalHarga);

            return transaksiMasukRepository.save(data);
        }).orElseThrow(() -> new EntityNotFoundException(""));
    }
}