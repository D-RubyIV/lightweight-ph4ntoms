package com.ph4ntoms.authenticate.service.authenticate;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.ph4ntoms.authenticate.exception.AuthenticateException;
import com.ph4ntoms.authenticate.model.User;
import com.ph4ntoms.authenticate.producer.Producer;
import com.ph4ntoms.authenticate.repo.UserRepository;
import com.ph4ntoms.authenticate.request.auth.ActivateRequest;
import com.ph4ntoms.authenticate.request.auth.SignInRequest;
import com.ph4ntoms.authenticate.request.auth.SignUpRequest;
import com.ph4ntoms.authenticate.request.kafka.SendEmailRequest;
import com.ph4ntoms.authenticate.response.token.ActivationObject;
import com.ph4ntoms.authenticate.response.token.AuthenticateResponse;
import com.ph4ntoms.authenticate.security.provider.JwtTokenProvider;
import com.ph4ntoms.authenticate.util.HashUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final Producer producer;
    private final UserRepository userRepo;
    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String redisActivationKey = "Activation";
    private static final long accessTokenExpiration = 12 * 60 * 60 * 1000L;
    private static final long refreshTokenExpiration = 24 * 60 * 60 * 1000L;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private static final String activationEmailFrom = "Ph4ntoms";
    private static final String activationEmailSubject = "Authenticate Code";
    private static final String activationEmailTemplate = "Hello. This is activation code: %s";

    private String getMessage(String code, Object... args) {
        try {
            return messageSource.getMessage(code, args, code, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            logger.error("Error getting message for code: {}", code, e);
            return code;
        }
    }

    private String generateToken(String username, Date expiration) {
        try {
            return userRepo.findByUsername(username)
                    .map(user -> jwtTokenProvider.generateToken(username, expiration))
                    .orElseThrow(() -> {
                        logger.warn("Token generation failed: User not found - {}", username);
                        return new AuthenticateException(getMessage("user.not.found"));
                    });
        } catch (Exception e) {
            logger.error("Error generating token for user: {}", username, e);
            throw new AuthenticateException(getMessage("token.generation.failed"));
        }
    }

    public AuthenticateResponse handleTokenObject(String username) {
        String accessToken = generateToken(username, new Date(System.currentTimeMillis() + accessTokenExpiration));
        String refreshToken = generateToken(username, new Date(System.currentTimeMillis() + refreshTokenExpiration));
        return AuthenticateResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public Map<String, String> signUp(SignUpRequest request) {
        try {
            userRepo.findByUsernameOrEmail(request.getUsername(), request.getEmail())
                    .ifPresent(entity -> {
                        logger.warn("Sign up failed: User already exists - username: {}, email: {}",
                                request.getUsername(), request.getEmail());
                        throw new AuthenticateException(getMessage("user.already.exists"));
                    });

            User user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .enabled(false)
                    .build();

            userRepo.save(user);
            ActivationObject activationObject = sendActivationCode(user);
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("sign", activationObject.getSign());
            logger.info("User registered successfully: {}", request.getUsername());
            return hashMap;
        } catch (Exception e) {
            logger.error("Error during sign up process for user: {}", request.getUsername(), e);
            throw new AuthenticateException(getMessage("signup.failed"));
        }
    }

    @Transactional
    public AuthenticateResponse signIn(@Valid SignInRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        return userRepo.findByUsername(username)
                .map(user -> {
                    if (!user.isEnabled()) {
                        throw new AuthenticateException(getMessage("user.inactive"));
                    }
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        throw new AuthenticateException(getMessage("auth.login.failed"));
                    }
                    logger.info("User logged in successfully: {}", username);
                    return handleTokenObject(username);
                })
                .orElseThrow(() -> new AuthenticateException(getMessage("user.not.found")));
    }

    public String activate(ActivateRequest request) {
        try {
            ActivationObject activationObject = (ActivationObject) redisTemplate.opsForHash().get(redisActivationKey, request.getCode());
            if (activationObject == null) {
                logger.warn("Activation failed: Invalid activation code - {}", request.getCode());
                throw new AuthenticateException(getMessage("invalid.activation.code"));
            }

            String username = HashUtils.decrypt(request.getSign(), activationObject.getPrivateKey());
            userRepo.findByUsername(username).ifPresentOrElse(
                    user -> {
                        user.setEnabled(true);
                        userRepo.save(user);
                        redisTemplate.opsForHash().delete(redisActivationKey, request.getCode());
                        logger.info("User activated successfully: {}", username);
                    },
                    () -> {
                        logger.warn("Activation failed: User not found - {}", username);
                        throw new AuthenticateException(getMessage("user.not.found"));
                    }
            );
            return getMessage("activation.successful");
        } catch (Exception e) {
            logger.error("Error during activation process for code: {}", request.getCode(), e);
            throw new AuthenticateException(getMessage("activation.failed"));
        }
    }

    public Boolean isValidToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                String username = jwtTokenProvider.extractUsername(token);
                return jwtTokenProvider.validateToken(token) && username != null;
            }
            catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Transactional
    public ActivationObject sendActivationCode(User user) {
        try {
            String code = UUID.randomUUID().toString();
            KeyPair keyPair = HashUtils.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            String sign = HashUtils.encrypt(user.getUsername(), publicKey);

            var activationObject = ActivationObject.builder()
                    .code(code)
                    .privateKey(privateKey)
                    .publicKey(publicKey)
                    .sign(sign)
                    .build();

            redisTemplate.opsForHash().put(redisActivationKey, code, activationObject);

            var email = SendEmailRequest.builder()
                    .subject(activationEmailSubject)
                    .from(activationEmailFrom)
                    .body(activationEmailTemplate.formatted(code))
                    .to(user.getEmail())
                    .build();

            producer.sendMessage(email);
            logger.info("Activation code sent successfully to user: {}", user.getUsername());
            return activationObject;
        } catch (Exception e) {
            logger.error("Error sending activation code to user: {}", user.getUsername(), e);
            throw new AuthenticateException(getMessage("activation.code.send.failed"));
        }
    }
}