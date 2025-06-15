package rongsokan.indah.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import rongsokan.indah.entities.Barang;
import rongsokan.indah.repositories.BarangRepository;

@Service
public class BarangService {

    @Autowired
    private BarangRepository barangRepository;

    public Barang saveBarang(Barang barang) {
        return barangRepository.save(barang);
    }

    public Page<Barang> getBarang(String cari, Pageable pageable) {
        if (cari.isEmpty()) {
            return barangRepository.findAll(pageable);
        } else {
            return barangRepository.findByNamaBarangContainingIgnoreCase(cari, pageable);
        }
    }

    public Barang updateBarang(Barang barang) {
        return barangRepository.findById(barang.getId()).map(data -> {
            Optional.ofNullable(barang.getNamaBarang()).ifPresent(data::setNamaBarang);
            Optional.ofNullable(barang.getHargaPerKg()).ifPresent(data::setHargaPerKg);

            return barangRepository.save(data);
        }).orElseThrow(() -> new EntityNotFoundException(""));
    }

    public void deleteBarang(UUID id) {
        barangRepository.deleteById(id);
    }
}