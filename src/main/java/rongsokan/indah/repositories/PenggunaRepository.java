package rongsokan.indah.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rongsokan.indah.entities.Pengguna;

@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, UUID> {

    Optional<Pengguna> findByUsername(String username);
}