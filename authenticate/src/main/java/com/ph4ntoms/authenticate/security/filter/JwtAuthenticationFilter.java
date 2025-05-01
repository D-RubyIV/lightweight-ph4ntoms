package com.ph4ntoms.authenticate.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ph4ntoms.authenticate.security.provider.KeyPairProvider;
import com.ph4ntoms.authenticate.util.ClientUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private KeyPairProvider keyPairProvider;
    @Autowired
    private ClientUtils clientUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientIp = clientUtils.getClientIp(request);
        logger.info("🌎 Handle Validate Access Token From IP: {}", clientIp);
        String accessToken = getAccessToken(request);

        if (accessToken != null) {
            DecodedJWT jwt = JWT.decode(accessToken);
            Algorithm algorithm = Algorithm.RSA256(keyPairProvider.getRsaPublicKey(), null);
            algorithm.verify(jwt);

            Date exp = jwt.getExpiresAt();
            boolean expired = exp.before(new Date());
            if (expired) {
                logger.debug("Expired access token: {}", jwt.getId());
                throw new RuntimeException("Expired access token");
            } else {
                String username = jwt.getSubject();
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("SET AUTHENTICATION SUCCESSFULLY");
                filterChain.doFilter(request, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private String getAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if (accessToken == null) {
            accessToken = request.getParameter("accessToken");
        }
        if (accessToken != null) {
            if (accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7);
            }
        }
        return accessToken;
    }
}