package com.ph4ntoms.authenticate.security.provider;



import com.ph4ntoms.authenticate.model.User;
import com.ph4ntoms.authenticate.repo.UserRepository;
import com.ph4ntoms.authenticate.util.BeanCommonUtils;
import com.ph4ntoms.authenticate.util.ClientUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(JpaAuthenticationProvider.class);

    private final PasswordEncoder passwordEncoder;
    private final ClientUtils clientUtils;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        String username = (String) token.getPrincipal();
        String password = (String) token.getCredentials();

        HttpServletRequest request = (HttpServletRequest) SecurityContextHolder.getContext().getAuthentication();
        String clientIp = (request != null) ? clientUtils.getClientIp(request) : "UNKNOWN";

        logger.info("🎯 Authenticating user: {} from IP: {}", username, clientIp);

        Optional<User> optUser = BeanCommonUtils.getBean(UserRepository.class).findByUsernameOrEmail(username, username);
        if (optUser.isPresent()) {
            User user = optUser.get();
            String encryptedPassword = user.getPassword();
            boolean passwordsMatch = passwordEncoder.matches(password, encryptedPassword);
            logger.info("👺 Password match: {}", passwordsMatch);
            if (passwordsMatch) {
                boolean enabled = user.getEnabled();
                logger.info("👶 Is enabled: {}", enabled);
                if (enabled) {
                    UserDetails userDetails = BeanCommonUtils.getBean(UserDetailsService.class).loadUserByUsername(username);
                    logger.info(userDetails.getUsername());
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    return auth;
                } else {
                    logger.debug("User is disabled: {}", username);
                    throw new DisabledException("User " + username + " is disabled");
                }
            } else {
                logger.debug("Incorrect password for user: {}", username);
                throw new BadCredentialsException("Incorrect password");
            }

        } else {
            logger.debug("User not found: {}", username);
            throw new UsernameNotFoundException(username);
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == UsernamePasswordAuthenticationToken.class;
    }

}