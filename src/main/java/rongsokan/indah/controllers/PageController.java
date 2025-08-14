package rongsokan.indah.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import rongsokan.indah.services.PageService;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("/")
    public String splashPage() {
        return "pages/splash";
    }

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, Model model) {
        return pageService.getLoginPage(request, model);
    }

    @GetMapping("/kelola-akun")
    public String getKelolaAkunPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return pageService.getKelolaAkunPage(model, cari, pageable);
    }

    @PostMapping("/kelola-akun")
    public String postKelolaAkunPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return pageService.postKelolaAkunPage(model, cari, pageable);
    }

    @GetMapping("/profil")
    public String getProfilPage(Model model) {
        return pageService.getProfilPage(model);
    }

    @GetMapping("/dashboard")
    public String getDashboardPage(Model model) {
        return pageService.getDashboardPage(model);
    }

    @GetMapping("/modal")
    public String getModalPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "tanggal", direction = Direction.DESC) Pageable pageable) {
        return pageService.getModalPage(model, cari, pageable);
    }

    @PostMapping("/modal")
    public String postModalPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "tanggal", direction = Direction.DESC) Pageable pageable) {
        return pageService.postModalPage(model, cari, pageable);
    }

    @GetMapping("/barang")
    public String getBarangPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return pageService.getBarangPage(model, cari, pageable);
    }

    @PostMapping("/barang")
    public String postBarangPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return pageService.postBarangPage(model, cari, pageable);
    }

    @GetMapping("/anak-buah")
    public String getAnakBuahPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return pageService.getAnakBuahPage(model, cari, pageable);
    }

    @PostMapping("/anak-buah")
    public String postAnakBuahPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        return pageService.postAnakBuahPage(model, cari, pageable);
    }

    @GetMapping("/transaksi-masuk")
    public String getTransaksiMasukPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "tanggal", direction = Direction.DESC) Pageable pageable) {
        return pageService.getTransaksiMasukPage(model, cari, pageable);
    }

    @PostMapping("/transaksi-masuk")
    public String postTransaksiMasukPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "tanggal", direction = Direction.DESC) Pageable pageable) {
        return pageService.postTransaksiMasukPage(model, cari, pageable);
    }

    @GetMapping("/transaksi-keluar")
    public String getTransaksiKeluarPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "tanggal", direction = Direction.DESC) Pageable pageable) {
        return pageService.getTransaksiKeluarPage(model, cari, pageable);
    }

    @PostMapping("/transaksi-keluar")
    public String postTransaksiKeluarPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String cari,
            @PageableDefault(size = 5, sort = "tanggal", direction = Direction.DESC) Pageable pageable) {
        return pageService.postTransaksiKeluarPage(model, cari, pageable);
    }

    @GetMapping("/rekapitulasi-laporan")
    public String getRekapitulasiLaporanPage(
            Model model,
            @RequestParam(required = false, defaultValue = "") String laporan) {
        return pageService.getRekapitulasiLaporanPage(model, laporan);
    }

    @PostMapping("/rekapitulasi-laporan")
    public Object postRekapitulasiLaporanPage(
            Model model,
            HttpServletResponse response,
            @RequestParam(required = false) boolean export,
            @RequestParam(required = false, defaultValue = "") String laporan) {
        return pageService.postRekapitulasiLaporanPage(model, response, export, laporan);
    }







    @GetMapping("/perhitungan-saw")
    public String getPerhitunganSAW(Model model) {
        return pageService.getPerhitunganSAW(model);
    }
}