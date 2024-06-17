package com.example.demo;

import com.example.demo.database.User;
import com.example.demo.database.UserRepository;
import com.example.demo.exceptions.EmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldWork() {
        User user = new User();

        when(userRepository.save(user)).thenReturn(user);

        userService.createUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void shouldThrowExceptionWhenEmailExists() {
        User user = new User();

        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("Fake exception"));

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createUser(user);
        });
        assertTrue(exception.getMessage().contains("El mail ya existe"));
    }
}
