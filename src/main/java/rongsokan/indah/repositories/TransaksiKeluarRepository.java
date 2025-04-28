package rongsokan.indah.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rongsokan.indah.entities.TransaksiKeluar;

@Repository
public interface TransaksiKeluarRepository extends JpaRepository<TransaksiKeluar, UUID> {

}