package org.practice.jwtauth.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.practice.jwtauth.entity.User;
import org.practice.jwtauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void createUser_shouldSaveUserWithEncodedPassword() {

        User inputUser = new User();
        inputUser.setName("John");
        inputUser.setEmail("example@example.com");
        inputUser.setPassword("password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John");
        savedUser.setEmail("example@example.com");
        savedUser.setPassword("password");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        User result = userService.createUser(inputUser);

        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getName()).isEqualTo(savedUser.getName());
        assertThat(result.getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(result.getPassword()).isNotEqualTo(savedUser.getPassword());
    }
}