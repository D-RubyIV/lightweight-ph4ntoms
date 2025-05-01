package com.ph4ntoms.authenticate.security.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class StaticKeyPairProvider implements KeyPairProvider {
    private static final String publicKeyContent = """
            -----BEGIN PUBLIC KEY-----
            MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAqUbOP+eLKSXVOrhycU+s
            gokdmG+sHLa5HuUYS+VFqXdptuO5nXzgTHUPNUk2yVZITrRpS+FDr4YvTcHEaISE
            WbAhrN0vZtZOa7wOnlZt6rHRCo8dFup99bQCOHsUewHxFQb1kpbXts0VyGNYFKDE
            TVOpZA5RzxJ7BUzSfTnp9GOlW4fcrUhhYbv4clFcYNVPLP0lZMgCznAoUu7yMCxD
            oa58sj4YYRfhI2/dGhytKfmsCZ6a133cmvsrDKc3DAOZH67dLZhEI/mWhbTwdZK0
            hrvGKPxkzhtcLJ8jWF08ipcaGVLzYsSsGYZALkOcRuQlqFkMml8FSIc67hBWS6Jd
            xrGAyzEn7bAP5wEbmPH9f8OwcklbHuo/jIhca2nMBPb0BwW44YSWx518Amx2NrDx
            ubVJIz/1n3uTFO+eG3SZ93jToIDUteB/gB22W//ERpYhg+5IEkkSOT1R/rp4n3Ge
            AaVcxVuIcEHY/wrZtsRZmDiNZRSfC3Abx/ZF6aGk25P6t0XkUPGR5Hk3nc3UjkOU
            5bEtUr04fNGge9SH8QXG+SxHMggAB0cdqmLE8SuVU5Dp58ERx7waSZLBFs2b3mw/
            swBU2s1QQ0nCbIbIfUQZ/sbeAlY3cglcVORMiN7Td7ntncwuWStXzjurCcO1IC2H
            mwQmLDovy6/qNklortkM7tsCAwEAAQ==
            -----END PUBLIC KEY-----
            """;

    private static final String privateKeyContent = """
            -----BEGIN PRIVATE KEY-----
            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
            8dZcADhd1r98rGkHB5LqmPooYQAuSQ==
            -----END PRIVATE KEY-----
            """;
    private static final Logger logger = LoggerFactory.getLogger(StaticKeyPairProvider.class);

    @Override
    public RSAPublicKey getRsaPublicKey() {
        try {
            String content = publicKeyContent;

            content = content.replaceAll("\\n", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
            KeyFactory kf = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(content));
            return (RSAPublicKey) kf.generatePublic(keySpecX509);
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    @Override
    public RSAPrivateKey getRsaPrivateKey() {
        try {
            String content = privateKeyContent;
            content = content.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
            KeyFactory kf = KeyFactory.getInstance("RSA");

            PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(content));
            return (RSAPrivateKey) kf.generatePrivate(keySpecPKCS8);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("", e);
            return null;
        }
    }
}