package rongsokan.indah.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rongsokan.indah.entities.AnakBuah;

@Repository
public interface AnakBuahRepository extends JpaRepository<AnakBuah, UUID> {

}