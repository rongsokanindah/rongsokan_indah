package rongsokan.indah.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rongsokan.indah.repositories.AnakBuahRepository;

@Service
public class AnakBuahService {

    @Autowired
    AnakBuahRepository anakBuahRepository;
}