package com.example.demo;

import com.example.demo.database.User;
import com.example.demo.database.UserRepository;
import com.example.demo.exceptions.EmailAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("El mail ya existe: " + user.getEmail());
        }
    }
}
