package org.practice.jwtauth.controller;

import org.junit.jupiter.api.Test;
import org.practice.jwtauth.entity.User;
import org.practice.jwtauth.model.LoginUser;
import org.practice.jwtauth.repository.UserRepository;
import org.practice.jwtauth.service.CustomUserDetailsService;
import org.practice.jwtauth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void login_ShouldReturnTokens_WhenCredentialsAreValid() throws Exception {
        LoginUser loginUser = new LoginUser("testUser", "password123");
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        User user = new User();
        user.setName("testUser");
        user.setPassword("password");
        user.setEmail("<EMAIL>");
        user.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(customUserDetailsService.loadUserByUsername(loginUser.getName()))
                .thenReturn(org.springframework.security.core.userdetails.User.builder()
                        .username(loginUser.getName())
                        .password(loginUser.getPassword())
                        .roles(new String[]{"ADMIN"})
                        .build());
        when(userRepository.findByName(loginUser.getName())).thenReturn(user);
        when(authentication.getName()).thenReturn(loginUser.getName());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateAccessToken(loginUser.getName())).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(loginUser.getName())).thenReturn(refreshToken);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"testUser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(loginUser.getName())))
                .andExpect(jsonPath("$.accessToken", is(accessToken)))
                .andExpect(jsonPath("$.refreshToken", is(refreshToken)));
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad Credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"invalidUser\",\"password\":\"wrongPassword\"}"))
                .andExpect(status().isForbidden());
    }
}