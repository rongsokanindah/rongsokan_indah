package rongsokan.indah.services;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import rongsokan.indah.entities.AnakBuah;
import rongsokan.indah.repositories.AnakBuahRepository;

@Service
public class AnakBuahService {

    @Autowired
    private AnakBuahRepository anakBuahRepository;

    public AnakBuah saveAnakBuah(AnakBuah anakBuah) {
        return anakBuahRepository.save(anakBuah);
    }

    public Page<AnakBuah> getAnakBuah(String cari, Pageable pageable) {
        if (cari.isEmpty()) {
            return anakBuahRepository.findAll(pageable);
        } else {
            return anakBuahRepository.findByNamaContainingIgnoreCaseOrNomorWhatsAppContaining(cari, cari, pageable);
        }
    }

    public AnakBuah updateAnakBuah(AnakBuah anakBuah) {
        return anakBuahRepository.findById(anakBuah.getId()).map(data -> {
            Optional.ofNullable(anakBuah.getNama()).ifPresent(data::setNama);
            Optional.ofNullable(anakBuah.getNomorWhatsApp()).ifPresent(data::setNomorWhatsApp);

            return anakBuahRepository.save(data);
        }).orElseThrow(() -> new EntityNotFoundException(""));
    }

    public void deleteAnakBuah(UUID id) {
        anakBuahRepository.deleteById(id);
    }

    public Map<String, Object> getDashboard() {
        return Map.of(
            "totalAnakBuah", anakBuahRepository.count()
        );
    }
}