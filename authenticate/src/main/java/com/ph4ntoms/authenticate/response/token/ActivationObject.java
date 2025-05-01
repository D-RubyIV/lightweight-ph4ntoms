package com.ph4ntoms.authenticate.response.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ActivationObject implements Serializable {
    private String sign;
    private String code;

    @JsonIgnore
    private PublicKey publicKey;

    @JsonProperty("publicKey")
    public String getPublicKeyAsBase64() {
        return publicKey != null
                ? Base64.getEncoder().encodeToString(publicKey.getEncoded())
                : null;
    }

    @JsonIgnore
    private PrivateKey privateKey;

    @JsonProperty("privateKey")
    public String getPrivateKeyAsBase64() {
        return privateKey != null
                ? Base64.getEncoder().encodeToString(privateKey.getEncoded())
                : null;
    }

    @JsonSetter("publicKey")
    public void setPublicKeyFromBase64(String key) {
        try {
            byte[] bytes = Base64.getDecoder().decode(key);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.publicKey = kf.generatePublic(new X509EncodedKeySpec(bytes));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid public key format", e);
        }
    }

    @JsonSetter("privateKey")
    public void setPrivateKeyFromBase64(String key) {
        try {
            byte[] bytes = Base64.getDecoder().decode(key);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(bytes));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid private key format", e);
        }
    }
}
