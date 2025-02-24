package org.practice.jwtauth.repository;

import org.junit.jupiter.api.Test;
import org.practice.jwtauth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindByName() {
        User user = new User();
        user.setName("testUser");
        user.setPassword("password");
        userRepository.save(user);

        Optional<User> foundUser = Optional.ofNullable(userRepository.findByName("testUser"));

        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getName());
    }

    @Test
    void testFindByName_NotFound() {
        Optional<User> foundUser = Optional.ofNullable(userRepository.findByName("nonExistentUser"));

        assertFalse(foundUser.isPresent());
    }
}