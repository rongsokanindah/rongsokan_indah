package rongsokan.indah.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rongsokan.indah.repositories.ModalRepository;

@Service
public class ModalService {

    @Autowired
    ModalRepository modalRepository;
}