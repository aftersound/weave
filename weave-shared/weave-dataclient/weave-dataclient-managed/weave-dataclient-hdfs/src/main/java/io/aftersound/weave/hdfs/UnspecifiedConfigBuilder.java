package io.aftersound.weave.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

class UnspecifiedConfigBuilder extends ConfigBuilder {

    @Override
    Configuration build(Settings settings) {
        Configuration config = new Configuration();
        config.set(FileSystem.FS_DEFAULT_NAME_KEY, settings.getFsDefaultName());
        return config;
    }

}
