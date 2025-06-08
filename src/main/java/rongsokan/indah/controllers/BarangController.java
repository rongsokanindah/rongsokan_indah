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

import rongsokan.indah.entities.Barang;
import rongsokan.indah.services.BarangService;

@RestController
@RequestMapping("/api/barang")
public class BarangController {

    @Autowired
    private BarangService barangService;

    @PostMapping
    public Barang saveBarang(@RequestBody Barang barang) {
        return barangService.saveBarang(barang);
    }

    @GetMapping
    public Page<Barang> getBarang(
        @RequestParam(required = false, defaultValue = "") String cari,
        @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return barangService.getBarang(cari, pageable);
    }

    @PutMapping
    public Barang updateBarang(@RequestBody Barang barang) {
        return barangService.updateBarang(barang);
    }

    @DeleteMapping("/{id}")
    public void deleteBarang(@PathVariable UUID id) {
        barangService.deleteBarang(id);
    }
}