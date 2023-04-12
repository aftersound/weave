package io.aftersound.weave.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

public class ContentHandle {

    private static final String BASE64 = "BASE64|";
    private static final String GZIP_BASE64 = "GZIP|BASE64|";
    private static final String ZIP_BASE64 = "ZIP|BASE64|";

    private ByteBuffer byteBuffer;

    private ContentHandle(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public static ContentHandle of(String content) throws IOException {
        byte[] bytes = decode(content);
        return new ContentHandle(ByteBuffer.wrap(bytes));
    }

    public byte[] get() {
        return byteBuffer.array();
    }

    public void clear() {
        byteBuffer.clear();
    }

    private static byte[] decode(String content) throws IOException {
        if (content.startsWith(BASE64)) {
            return Base64.getDecoder().decode(content.substring(BASE64.length()));
        }

        if (content.startsWith(GZIP_BASE64)) {
            byte[] decoded = Base64.getDecoder().decode(content.substring(GZIP_BASE64.length()));
            try (InputStream is = new GZIPInputStream(new ByteArrayInputStream(decoded))) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int res = 0;
                byte buf[] = new byte[1024];
                while (res >= 0) {
                    res = is.read(buf, 0, buf.length);
                    if (res > 0) {
                        baos.write(buf, 0, res);
                    }
                }
                return baos.toByteArray();
            }
        }

        if (content.startsWith(ZIP_BASE64)) {
            byte[] decoded = Base64.getDecoder().decode(content.substring(ZIP_BASE64.length()));
            try (InputStream is = new ZipInputStream(new ByteArrayInputStream(decoded))) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int res = 0;
                byte buf[] = new byte[1024];
                while (res >= 0) {
                    res = is.read(buf, 0, buf.length);
                    if (res > 0) {
                        baos.write(buf, 0, res);
                    }
                }
                return baos.toByteArray();
            }
        }

        return content.getBytes(StandardCharsets.UTF_8);
    }

}
