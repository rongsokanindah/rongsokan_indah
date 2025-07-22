package rongsokan.indah.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import rongsokan.indah.entities.TransaksiKeluar;
import rongsokan.indah.services.TransaksiKeluarService;

@RestController
@RequestMapping("/api/transaksi-keluar")
public class TransaksiKeluarController {

    @Autowired
    private TransaksiKeluarService transaksiKeluarService;

    @PostMapping
    public TransaksiKeluar saveTransaksiKeluar(@RequestBody TransaksiKeluar transaksiKeluar) {
        return transaksiKeluarService.saveTransaksiKeluar(transaksiKeluar);
    }

    @GetMapping
    public Page<TransaksiKeluar> getTransaksiKeluar(
        @RequestParam(required = false, defaultValue = "") String cari,
        @PageableDefault(size = 5, sort = "tanggal", direction = Direction.DESC) Pageable pageable) {
        return transaksiKeluarService.getTransaksiKeluar(cari, pageable);
    }

    @PutMapping
    public TransaksiKeluar updateTransaksiKeluar(@RequestBody TransaksiKeluar transaksiKeluar) {
        return transaksiKeluarService.updateTransaksiKeluar(transaksiKeluar);
    }
}