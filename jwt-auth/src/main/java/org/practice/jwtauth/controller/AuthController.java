package org.practice.jwtauth.controller;

import org.practice.jwtauth.entity.User;
import org.practice.jwtauth.model.LoginResponse;
import org.practice.jwtauth.model.LoginUser;
import org.practice.jwtauth.service.UserService;
import org.practice.jwtauth.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @GetMapping("/test")
    public String auth() {
        return "test";
    }

    @PostMapping(value = "/signup")
    public User register(@RequestBody User loginUser) {
        User user = userService.createUser(loginUser);
        user.setPassword(null);
        return user;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginUser loginUser) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginUser.getName(), loginUser.getPassword()) {
        });
        String name = authentication.getName();
        return generateTokes(name);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody String refreshToken) {
        if(jwtUtil.isValid(refreshToken)) {
            String userNameFromToken = jwtUtil.getUserNameFromToken(refreshToken);
            return generateTokes(userNameFromToken);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private ResponseEntity<LoginResponse> generateTokes(String name) {
        String acc = jwtUtil.generateAccessToken(name);
        String refresh = jwtUtil.generateRefreshToken(name);
        return new ResponseEntity<>(new LoginResponse(name, acc, refresh), HttpStatus.OK);
    }

}
