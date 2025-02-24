package org.practice.jwtauth.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserEntity() {

        User user = new User();
        user.setId(1L);
        user.setName("testUser");
        user.setEmail("test@example.com");
        user.setPassword("password123");


        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testNoArgsConstructor() {
        User user = new User();


        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
    }
}