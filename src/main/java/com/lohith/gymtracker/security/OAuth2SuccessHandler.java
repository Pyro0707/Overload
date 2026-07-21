package com.lohith.gymtracker.security;

import com.lohith.gymtracker.model.User;
import com.lohith.gymtracker.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public OAuth2SuccessHandler(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        if (email == null) {
            response.sendRedirect("/index.html?error=no_email_provided");
            return;
        }

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // User does not exist, register them
            String baseUsername = (name != null && !name.trim().isEmpty()) 
                    ? name.replaceAll("\\s+", "").toLowerCase()
                    : email.split("@")[0].toLowerCase();
            
            String username = baseUsername;
            int counter = 1;
            while (userRepository.existsByUsername(username)) {
                username = baseUsername + counter++;
            }

            // Google OAuth users are registered with a random UUID password. 
            // They cannot login via regular password auth unless a password-setting/reset mechanism is added.
            String randomPassword = UUID.randomUUID().toString();
            user = new User(
                    username,
                    email,
                    passwordEncoder.encode(randomPassword)
            );
            user = userRepository.save(user);
            logger.info("Automatically registered new Google OAuth2 user: {}", username);
        } else {
            logger.info("Logged in existing Google OAuth2 user: {}", user.getUsername());
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // Deliver the token in the URL fragment, not the query string: fragments are
        // never sent to the server, so they stay out of access logs and Referer headers.
        String targetUrl = "/index.html#" + UriComponentsBuilder.newInstance()
                .queryParam("token", token)
                .queryParam("username", user.getUsername())
                .build().encode().getQuery();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
