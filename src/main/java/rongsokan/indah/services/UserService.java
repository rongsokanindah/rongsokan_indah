package rongsokan.indah.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rongsokan.indah.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;
}