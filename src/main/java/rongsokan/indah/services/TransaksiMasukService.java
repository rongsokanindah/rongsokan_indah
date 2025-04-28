package rongsokan.indah.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rongsokan.indah.repositories.TransaksiMasukRepository;

@Service
public class TransaksiMasukService {

    @Autowired
    TransaksiMasukRepository transaksiMasukRepository;
}