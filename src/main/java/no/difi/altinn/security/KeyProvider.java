package no.difi.altinn.security;

import java.security.*;

/**
 * Holds privatekey and certificate information in memory.
 */
public class KeyProvider {

    private PrivateKey privateKey;
    private java.security.cert.Certificate certificate;

    private PublicKey publicKey;

    public KeyProvider(KeyStore keyStore, String alias, String password) {
        try {
            privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
            certificate = keyStore.getCertificate(alias);
            publicKey = certificate.getPublicKey();
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public PrivateKey privateKey() {
        return privateKey;
    }

    public java.security.cert.Certificate certificate() {
        return certificate;
    }
}
