package com.reservaction.test_sec.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "rsa")
public record RsaKeysConfig(RSAPublicKey publicKey) {
}
