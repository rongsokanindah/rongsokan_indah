package rongsokan.indah.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rongsokan.indah.entities.Barang;

@Repository
public interface BarangRepository extends JpaRepository<Barang, UUID> {

    Page<Barang> findByNamaBarangContainingIgnoreCase(String namaBarang, Pageable pageable);
}