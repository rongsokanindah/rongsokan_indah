package rongsokan.indah.constants;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
public class TextConstant {
    private final String title = "Rongsokan Indah";
    private final String description = "Ini adalah project Sistem Rongsokan Indah";
    private final String email = "rongsakan.indah@gmail.com";
    private final String masukAkunAnda = "Masuk ke Akun Anda";
    private final String username = "Username";
    private final String password = "Password";
    private final String login = "Login";

    private final String modal = "Modal";
    private final String barang = "Barang";
    private final String anakBuah = "Anak Buah";
    private final String dashboard = "Dashboard";
    private final String transaksiMasuk = "Transaksi Masuk";
    private final String transaksiKeluar = "Transaksi Keluar";
    private final String rekapitulasiLaporan = "Rekapitulasi Laporan";

    private final String profil = "Profil";
    private final String logout = "Logout";

    private final String dataBarang = "Data Barang";
    private final String cariBarang = "Cari Barang";
    private final String belumAdaData = "Belum ada data yang tersedia.";
    private final String deskripsiBarang = "Daftar semua barang beserta harga per kilogramnya.";

    private final String no = "No.";
    private final String namaBarang = "Nama Barang";
    private final String hargaPerKg = "Harga per Kg";

    private final String aksi = "Aksi";
    private final String hapus = "Hapus";
    private final String simpan = "Simpan";
    private final String editBarang = "Edit Barang";
    private final String hapusBarang = "Hapus Barang";
    private final String tambahBarang = "Tambah Barang";

    private final String wajibMin3Karakter = "Wajib diisi minimal 3 karakter.";
    private final String wajibTidakNegatif = "Wajib diisi dan tidak boleh negatif.";
    private final String confirmationDelete = "Apakah Anda yakin ingin menghapus ini";

    private final String dataAnakBuah = "Data Anak Buah";
    private final String cariAnakBuah = "Cari Anak Buah";
    private final String deskripsiAnakBuah = "Daftar semua anak buah beserta nomor WhatsApp yang dapat dihubungi.";

    private final String namaAnakBuah = "Nama Anak Buah";
    private final String nomorWhatsApp = "Nomor WhatsApp";

    private final String editAnakBuah = "Edit Anak Buah";
    private final String hapusAnakBuah = "Hapus Anak Buah";
    private final String tambahAnakBuah = "Tambah Anak Buah";

    private final String wajibHurufMin3Karakter = "Wajib diisi hanya huruf dan spasi minimal 3 karakter.";
    private final String wajibAwal08Or62Length10Until15Digit = "Wajib diisi diawali dengan 08 atau 62 dengan panjang 10-15 digit.";
}