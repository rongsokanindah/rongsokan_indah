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

import rongsokan.indah.entities.Pengguna;
import rongsokan.indah.services.PenggunaService;

@RestController
@RequestMapping("/api/pengguna")
public class PenggunaController {

    @Autowired
    private PenggunaService penggunaService;

    @PostMapping
    public Pengguna savePengguna(@RequestBody Pengguna pengguna) {
        return penggunaService.savePengguna(pengguna);
    }

    @GetMapping
    public Page<Pengguna> getPengguna(
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return penggunaService.getPengguna(cari, pageable);
    }

    @PutMapping
    public Pengguna updatePengguna(@RequestBody Pengguna pengguna) {
        return penggunaService.updatePengguna(pengguna);
    }

    @DeleteMapping("/{id}")
    public void deletePengguna(@PathVariable UUID id) {
        penggunaService.deletePengguna(id);
    }
}