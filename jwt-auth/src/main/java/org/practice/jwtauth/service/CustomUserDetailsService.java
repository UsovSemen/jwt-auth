package org.practice.jwtauth.service;

import org.practice.jwtauth.entity.User;
import org.practice.jwtauth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User byEmail = userRepository.findByName(username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(byEmail.getName())
                .password(byEmail.getPassword())
                .roles(new String[]{"ADMIN"})
                .build();

    }
}
