package io.aftersound.weave.hdfs;

import io.aftersound.weave.file.FileUtil;
import io.aftersound.weave.utils.Base64;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigInstaller {

    public static Path install(String base64EncodedConfig, String filePath) throws IOException {
        return FileUtil.writeBytesInFile(Base64.getDecoder().decode(base64EncodedConfig), filePath);
    }

}
