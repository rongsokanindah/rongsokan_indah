package rongsokan.indah.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import rongsokan.indah.entities.Pengguna;
import rongsokan.indah.repositories.PenggunaRepository;

@Service
public class PenggunaService implements UserDetailsService {

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Pengguna pengguna = penggunaRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(""));

        return User.builder()
            .username(pengguna.getUsername())
            .password(pengguna.getPassword())
            .roles(pengguna.getRole().toString()).build();
    }
}