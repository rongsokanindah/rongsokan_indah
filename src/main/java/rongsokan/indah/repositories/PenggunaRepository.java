package rongsokan.indah.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rongsokan.indah.entities.Pengguna;
import rongsokan.indah.entities.Pengguna.Role;

@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, UUID> {

    Optional<Pengguna> findByUsername(String username);

    Page<Pengguna> findById(UUID id, Pageable pageable);

    Page<Pengguna> findByRole(Role role, Pageable pageable);

    Page<Pengguna> findByAnakBuah_NamaContainingIgnoreCaseOrUsername(String nama, String username, Pageable pageable);
}