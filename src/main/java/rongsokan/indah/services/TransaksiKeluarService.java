package rongsokan.indah.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rongsokan.indah.repositories.TransaksiKeluarRepository;

@Service
public class TransaksiKeluarService {

    @Autowired
    TransaksiKeluarRepository transaksiKeluarRepository;
}