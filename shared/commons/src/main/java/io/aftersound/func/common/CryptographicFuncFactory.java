package io.aftersound.func.common;

import io.aftersound.crypto.KeyProvider;
import io.aftersound.func.*;
import io.aftersound.schema.Constraint;
import io.aftersound.schema.Field;
import io.aftersound.schema.Type;
import io.aftersound.util.TreeNode;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static io.aftersound.func.FuncHelper.createCreationException;
import static io.aftersound.func.FuncHelper.getRequiredDependency;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CryptographicFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
            Descriptor.builder("RSA:ENCRYPT")
                    .withControls(
                            Field.stringFieldBuilder("publicKey")
                                    .withConstraint(Constraint.required())
                                    .withDescription("Base64-encoded RSA public key for encryption. Required.")
                                    .build()
                    )
                    .withInput(
                            Field.builder("input", Type.builder("String").build())
                                    .withDescription("Plaintext string to encrypt.")
                                    .build()
                    )
                    .withOutput(
                            Field.builder("output", Type.builder("String").build())
                                    .withDescription("Base64-encoded encrypted text.")
                                    .build()
                    )
                    .withExamples(
                            Example.as(
                                    "RSA:ENCRYPT",
                                    "Encrypt 'Hello, RSA!' with given public key and return Base64-encoded ciphertext."
                            )
                    )
                    .build(),

            Descriptor.builder("RSA:DECRYPT")
                    .withControls(
                            Field.stringFieldBuilder("privateKey")
                                    .withConstraint(Constraint.required())
                                    .withDescription("Base64-encoded RSA private key for decryption. Required.")
                                    .build()
                    )
                    .withInput(
                            Field.builder("input", Type.builder("String").build())
                                    .withDescription("Base64-encoded encrypted text to decrypt.")
                                    .build()
                    )
                    .withOutput(
                            Field.builder("output", Type.builder("String").build())
                                    .withDescription("Decrypted plaintext string.")
                                    .build()
                    )
                    .withExamples(
                            Example.as(
                                    "RSA:DECRYPT",
                                    "Decrypt Base64-encoded ciphertext with given private key and return plaintext."
                            )
                    )
                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        String funcName = spec.getData();

        switch (funcName) {
            case "RSA:DECRYPT": {
                return createRSADecryptFunc(spec);
            }
            case "RSA:ENCRYPT": {
                return createRSAEncryptFunc(spec);
            }
        }

        return null;
    }

    static class RSADecryptFunc extends AbstractFuncWithHints<byte[], byte[]> {

        private final Cipher cipher;

        public RSADecryptFunc(Cipher cipher) {
            this.cipher = cipher;
        }

        @Override
        public byte[] apply(byte[] bytes) {
            if (bytes != null) {
                try {
                    return cipher.doFinal(bytes);
                } catch (Exception e) {
                    throw new ExecutionException("failed to decrypt with given private key", e);
                }
            } else {
                return null;
            }
        }

    }

    static class RSAEncryptFunc extends AbstractFuncWithHints<byte[], byte[]> {

        private final Cipher cipher;

        public RSAEncryptFunc(Cipher cipher) {
            this.cipher = cipher;
        }

        @Override
        public byte[] apply(byte[] bytes) {
            if (bytes != null) {
                try {
                    return cipher.doFinal(bytes);
                } catch (Exception e) {
                    throw new ExecutionException("failed to encrypt with given public key", e);
                }
            } else {
                return null;
            }
        }

    }

    private Func createRSADecryptFunc(TreeNode spec) {
        final String keyId = spec.getDataOfChildAt(0);
        final String keyProviderId = spec.getDataOfChildAt(1, KeyProvider.class.getSimpleName());

        try {
            KeyProvider keyProvider = getRequiredDependency(keyProviderId, KeyProvider.class);

            byte[] privateKeyBytes = keyProvider.getRSAPrivateKey(keyId);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new X509EncodedKeySpec(privateKeyBytes));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return new RSADecryptFunc(cipher);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "RSA:DECRYPT(keyId) or RSA:DECRYPT(keyId,keyProviderId)",
                    "RSA:DECRYPT(exchange)"
            );
        }
    }

    private Func createRSAEncryptFunc(TreeNode spec) {
        final String keyId = spec.getDataOfChildAt(0);
        final String keyProviderId = spec.getDataOfChildAt(1, KeyProvider.class.getSimpleName());

        try {
            KeyProvider keyProvider = getRequiredDependency(keyProviderId, KeyProvider.class);

            byte[] publicKeyBytes = keyProvider.getRSAPublicKey(keyId);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new RSAEncryptFunc(cipher);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "RSA:ENCRYPT(keyId) or RSA:ENCRYPT(keyId,keyProviderId)",
                    "RSA:ENCRYPT(exchange)"
            );
        }
    }

}