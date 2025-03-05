package io.aftersound.crypto;

public interface KeyProvider {
    byte[] getRSAPublicKey(String keyId);
    byte[] getRSAPrivateKey(String keyId);
}
