package rongsokan.indah.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rongsokan.indah.entities.AnakBuah;
import rongsokan.indah.services.AnakBuahService;

@RestController
@RequestMapping("/api/anak-buah")
public class AnakBuahController {

    @Autowired
    private AnakBuahService anakBuahService;

    @PostMapping
    public AnakBuah saveAnakBuah(@RequestBody AnakBuah anakBuah) {
        return anakBuahService.saveAnakBuah(anakBuah);
    }

    @GetMapping
    public Page<AnakBuah> getAnakBuah(
        @RequestParam(required = false, defaultValue = "") String cari,
        @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return anakBuahService.getAnakBuah(cari, pageable);
    }

    @PutMapping
    public AnakBuah updateAnakBuah(@RequestBody AnakBuah anakBuah) {
        return anakBuahService.updateAnakBuah(anakBuah);
    }

    @DeleteMapping("/{id}")
    public void deleteAnakBuah(@PathVariable UUID id) {
        anakBuahService.deleteAnakBuah(id);
    }
}