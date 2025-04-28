package rongsokan.indah.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rongsokan.indah.entities.Modal;

@Repository
public interface ModalRepository extends JpaRepository<Modal, UUID> {

}