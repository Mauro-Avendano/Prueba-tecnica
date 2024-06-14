package com.example.demo;

import com.example.demo.database.User;
import com.example.demo.database.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (emailExists(user.getEmail())) {
            throw new RuntimeException("El email ya existe");
        }

        return userRepository.save(user);
    }

    private boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}
