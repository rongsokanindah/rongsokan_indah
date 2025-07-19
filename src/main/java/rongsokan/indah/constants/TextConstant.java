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

    private final String kelolaAkun = "Kelola Akun";
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

    private final String dataModal = "Data Modal";
    private final String cariModal = "Cari Modal";
    private final String deskripsiModalAnakBuah = "Daftar semua jumlah modal yang telah Anda terima.";
    private final String deskripsiModalAdmin = "Daftar semua jumlah modal yang telah diberikan ke Anak Buah.";

    private final String jumlahModal = "Jumlah Modal";
    private final String tanggalDiBerikan = "Tanggal di Berikan";

    private final String editModal = "Edit Modal";
    private final String tambahModal = "Tambah Modal";

    private final String wajibMemilihAnakBuah = "Wajib diisi dengan memilih Anak Buah.";
    private final String wajibMin50Ribu = "Wajib diisi dengan minimal nominal Rp50.000.";

    private final String dataAkun = "Data Akun";
    private final String cariAkun = "Cari Akun";
    private final String deskripsiAkun = "Daftar semua akun milik Admin dan Anak Buah.";

    private final String nama = "Nama";
    private final String role = "Role";
    private final String pilihRole = "Pilih Role";
    private final String konfirmasiPassword = "Konfirmasi Password";

    private final String editAkun = "Edit Akun";
    private final String hapusAkun = "Hapus Akun";
    private final String tambahAkun = "Tambah Akun";

    private final String usernameTelahDigunakan = "Username telah digunakan.";
    private final String wajibMemilihRole = "Wajib memilih Role terlebih dahulu.";
    private final String wajibHurufAngkaMin5Karakter = "Wajib diisi hanya huruf, angka, dan garis bawah minimal 5 karakter.";
    private final String wajibSamaDenganPassword = "Wajib diisi dan harus sama dengan sebelumnya.";
    private final String wajibHurufAngkaSimbolMin6Karakter = "Wajib diisi hanya huruf besar/kecil, angka, dan simbol minimal 6 karakter.";

    private final String dataProfil = "Data Profil";
    private final String gantiPassword = "Ganti Password";
    private final String deskripsiProfil = "Menampilkan informasi profil Anda.";

    private final String dataTransaksiMasuk = "Data Transaksi Masuk";
    private final String cariTransaksiMasuk = "Cari Transaksi Masuk";
    private final String deskripsiTransaksiMasukAnakBuah = "Daftar semua transaksi masuk yang telah Anda buat.";
    private final String deskripsiTransaksiMasukAdmin = "Daftar semua transaksi masuk yang telah dibuat Admin atau Anak Buah.";

    private final String beratKg = "Berat (Kg)";
    private final String totalHarga = "Total Harga";
    private final String tanggalTransaksi = "Tanggal Transaksi";

    private final String editTransaksi = "Edit Transaksi";
    private final String hapusTransaksi = "Hapus Transaksi";
    private final String tambahTransaksi = "Tambah Transaksi";

    private final String wajibMemilihBarang = "Wajib diisi dengan memilih Barang.";
    private final String wajibMin1Kg = "Wajib diisi dengan minimal satu Kilogram.";
}