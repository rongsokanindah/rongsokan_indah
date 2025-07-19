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

import rongsokan.indah.entities.TransaksiMasuk;
import rongsokan.indah.services.TransaksiMasukService;

@RestController
@RequestMapping("/api/transaksi-masuk")
public class TransaksiMasukController {

    @Autowired
    private TransaksiMasukService transaksiMasukService;

    @PostMapping
    public TransaksiMasuk saveTransaksiMasuk(@RequestBody TransaksiMasuk transaksiMasuk) {
        return transaksiMasukService.saveTransaksiMasuk(transaksiMasuk);
    }

    @GetMapping
    public Page<TransaksiMasuk> getTransaksiMasuk(
        @RequestParam(required = false, defaultValue = "") String cari,
        @PageableDefault(size = 5, sort = "tanggal", direction = Direction.DESC) Pageable pageable) {
        return transaksiMasukService.getTransaksiMasuk(cari, pageable);
    }

    @PutMapping
    public TransaksiMasuk updateTransaksiMasuk(@RequestBody TransaksiMasuk transaksiMasuk) {
        return transaksiMasukService.updateTransaksiMasuk(transaksiMasuk);
    }
}