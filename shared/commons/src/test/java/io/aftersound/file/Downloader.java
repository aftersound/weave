package io.aftersound.file;

import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Downloader {

//    @Test
    public void download() throws Exception {
        final String output = "entity-models.pptx";
        final String password = "TxxxJwll@12345";
        final String salt = "Hello";

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 65536, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        secretKey = new SecretKeySpec(secretKey.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        File file = new File("/home/xuxiaochun/Downloads/" + output);
        file.createNewFile();

        try (InputStream is = Downloader.class.getResourceAsStream("/entity models.pptx.txt"); FileOutputStream fos = new FileOutputStream(file)) {
            byte[] bytes = is.readAllBytes();
            byte[] decoded = Base64.getDecoder().decode(bytes);
            byte[] decrypted = cipher.doFinal(decoded);
            fos.write(decrypted);
        }

    }

}
