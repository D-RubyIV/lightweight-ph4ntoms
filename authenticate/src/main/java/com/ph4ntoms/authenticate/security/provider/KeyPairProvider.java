package com.ph4ntoms.authenticate.security.provider;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public interface KeyPairProvider {
    RSAPublicKey getRsaPublicKey();

    RSAPrivateKey getRsaPrivateKey();
}