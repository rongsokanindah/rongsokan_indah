package rongsokan.indah.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rongsokan.indah.repositories.BarangRepository;

@Service
public class BarangService {

    @Autowired
    BarangRepository barangRepository;
}