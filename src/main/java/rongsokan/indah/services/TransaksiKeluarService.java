package rongsokan.indah.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import rongsokan.indah.entities.TransaksiKeluar;
import rongsokan.indah.repositories.TransaksiKeluarRepository;
import rongsokan.indah.utils.Dates;
import rongsokan.indah.utils.Types;

@Service
public class TransaksiKeluarService {

    @Autowired
    private TransaksiKeluarRepository transaksiKeluarRepository;

    public TransaksiKeluar saveTransaksiKeluar(TransaksiKeluar transaksiKeluar) {
        return transaksiKeluarRepository.save(transaksiKeluar);
    }

    public Page<TransaksiKeluar> getTransaksiKeluar(String cari, Pageable pageable) {
        if (cari.isEmpty()) {
            return transaksiKeluarRepository.findAll(pageable);
        }

        if (Types.isBigDecimal(cari)) {
            BigDecimal decimal = new BigDecimal(cari);
            return transaksiKeluarRepository.findByBeratKgOrHargaJual(decimal, decimal, pageable);
        }

        if (Dates.isLocalDate(cari)) {
            LocalDate date = Dates.parseDate(cari);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(LocalTime.MAX);

            return transaksiKeluarRepository.findByTanggalBetween(start, end, pageable);
        }

        return transaksiKeluarRepository.findByBarang_NamaBarangContainingIgnoreCase(cari, pageable);
    }

    public TransaksiKeluar updateTransaksiKeluar(TransaksiKeluar transaksiKeluar) {
        return transaksiKeluarRepository.findById(transaksiKeluar.getId()).map(data -> {
            Optional.ofNullable(transaksiKeluar.getBarang()).ifPresent(data::setBarang);
            Optional.ofNullable(transaksiKeluar.getBeratKg()).ifPresent(data::setBeratKg);
            Optional.ofNullable(transaksiKeluar.getHargaJual()).ifPresent(data::setHargaJual);

            return transaksiKeluarRepository.save(data);
        }).orElseThrow(() -> new EntityNotFoundException(""));
    }
}