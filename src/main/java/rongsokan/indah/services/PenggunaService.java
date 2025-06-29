package rongsokan.indah.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import rongsokan.indah.entities.Pengguna;
import rongsokan.indah.entities.Pengguna.Role;
import rongsokan.indah.repositories.PenggunaRepository;
import rongsokan.indah.utils.Types;

@Service
public class PenggunaService implements UserDetailsService {

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Pengguna pengguna = penggunaRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(""));

        return User.builder()
                .username(pengguna.getUsername())
                .password(pengguna.getPassword())
                .roles(pengguna.getRole().toString()).build();
    }

    public Pengguna savePengguna(Pengguna pengguna) {
        return penggunaRepository.save(pengguna);
    }

    public Page<Pengguna> getPengguna(String cari, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Pengguna pengguna = penggunaRepository.findByUsername(auth.getName()).get();
        boolean isAdmin = pengguna.getRole().equals(Pengguna.Role.ADMIN);

        if (isAdmin) {
            if (cari.isEmpty()) {
                return penggunaRepository.findAll(pageable);
            } else {
                if (Types.isValidRole(cari)) {
                    return penggunaRepository.findByRole(Role.valueOf(cari), pageable);
                } else {
                    return penggunaRepository.findByAnakBuah_NamaContainingIgnoreCaseOrUsername(cari, cari, pageable);
                }
            }
        } else {
            return penggunaRepository.findById(pengguna.getId(), pageable);
        }
    }

    public Pengguna updatePengguna(Pengguna pengguna) {
        return penggunaRepository.findById(pengguna.getId()).map(data -> {
            Optional.ofNullable(pengguna.getUsername()).ifPresent(data::setUsername);
            Optional.ofNullable(pengguna.getPassword()).ifPresent(data::setPassword);
            Optional.ofNullable(pengguna.getRole()).ifPresent(data::setRole);

            return penggunaRepository.save(data);
        }).orElseThrow(() -> new EntityNotFoundException(""));
    }

    public void deletePengguna(UUID id) {
        penggunaRepository.deleteById(id);
    }
}