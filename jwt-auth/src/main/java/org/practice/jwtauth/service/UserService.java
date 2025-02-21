package org.practice.jwtauth.service;

import org.practice.jwtauth.entity.User;
import org.practice.jwtauth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User loginUser) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        loginUser.setPassword(bCryptPasswordEncoder.encode(loginUser.getPassword()));
        return userRepository.save(loginUser);
    }
}
