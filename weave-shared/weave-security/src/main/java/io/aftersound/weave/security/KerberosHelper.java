package io.aftersound.weave.security;

import io.aftersound.weave.file.FileUtil;
import io.aftersound.weave.utils.Base64;

import java.io.IOException;
import java.nio.file.Path;

public class KerberosHelper {

    public static Path installKeytab(String base64EncodedKeytab, String keytabFile) throws IOException {
        return FileUtil.writeBytesInFile(Base64.getDecoder().decode(base64EncodedKeytab), keytabFile);
    }

    public static Path installKrb5Conf(String base64EncodedKrb5Conf, String krb5File) throws IOException {
        return FileUtil.writeBytesInFile(Base64.getDecoder().decode(base64EncodedKrb5Conf), krb5File);
    }

}
