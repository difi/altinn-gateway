package no.difi.altinn.security;

import no.difi.altinn.config.ClientProperties;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.Objects;

public class KeyStoreProvider {

    private final KeyStore keyStore;

    private KeyStoreProvider(KeyStore keyStore) {
        this.keyStore = Objects.requireNonNull(keyStore);
    }

    public static KeyStoreProvider from(ClientProperties.KeyStoreProperties properties) {
        return new KeyStoreProvider(loadKeyStore(Objects.requireNonNull(properties)));
    }

    private static KeyStore loadKeyStore(ClientProperties.KeyStoreProperties properties) {
        try {
            KeyStore store = KeyStore.getInstance(properties.getType());
            InputStream stream = properties.getPath().getInputStream();
            store.load(stream, properties.getPassword().toCharArray());
            return store;
        } catch (Exception e) {
            throw new IllegalStateException("Could not initialize keystore.", e);
        }
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }
}
