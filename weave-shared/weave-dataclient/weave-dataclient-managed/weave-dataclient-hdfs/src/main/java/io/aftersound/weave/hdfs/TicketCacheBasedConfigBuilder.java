package io.aftersound.weave.hdfs;

import io.aftersound.weave.exception.InvalidConfigException;
import io.aftersound.weave.file.FileUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TicketCacheBasedConfigBuilder extends ConfigBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(TicketCacheBasedConfigBuilder.class);

    @Override
    Configuration build(Settings settings) throws Exception {
        Configuration config = new Configuration();

        // 1.core-site.xml
        processCoreSiteXml(config, settings);

        // 2.hdfs-site.xml
        processHdfsSiteXml(config, settings);

        // 3.any configuration starts with 'hadoop.'
        processHadoopConfigs(config, settings);

        // 4.fs.defaultFS
        config.set(FileSystem.FS_DEFAULT_NAME_KEY, settings.getFsDefaultName());

        // 5.Kerberos ticket cache path
        // Note: Kerberos ticket cache lifecycle and path are managed somewhere else

        // Ensure ticket cache path is valid
        if (settings.getTicketCachePath() == null) {
            throw new InvalidConfigException("Kerberos ticket cache path must be specified");
        }
        if (!FileUtil.fileExists(settings.getTicketCachePath())) {
            throw new InvalidConfigException("Kerberos ticket cache path doesn't exist on file system");
        }
        // in case ticket cache is specified, it's already set in config by processHadoopConfigs

        return config;
    }

}
