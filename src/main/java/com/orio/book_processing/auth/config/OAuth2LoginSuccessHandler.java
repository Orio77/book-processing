package com.orio.book_processing.auth.config;

import com.orio.book_processing.auth.models.User;
import com.orio.book_processing.auth.repositories.UserRepository;
import com.orio.book_processing.auth.services.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2User oauth2User = oauthToken.getPrincipal();
            
            // 1. Extract user info from 'authentication' object
            String email = oauth2User.getAttribute("email");
            if (email == null) {
                throw new ServletException("Email not found from OAuth2 provider");
            }
            
            // 2. Fetch or auto-register user in DB
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = User.builder()
                        .email(email)
                        .passwordHash("") // Local auth password is not used/needed for OAuth2 users
                        .build();
                return userRepository.save(newUser);
            });

            // 3. Generate a JWT token
            JwtService.TokenPayload tokenPayload = jwtService.generateToken(user);
            String token = tokenPayload.accessToken();
            
            // 4. Redirect back to Svelte with the token
            String frontendUrl = "http://localhost:5173/auth-callback?token=" + token;
            getRedirectStrategy().sendRedirect(request, response, frontendUrl);
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
