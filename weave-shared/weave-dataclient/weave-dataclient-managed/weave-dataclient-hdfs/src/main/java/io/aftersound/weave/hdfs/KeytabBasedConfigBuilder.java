package io.aftersound.weave.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;

class KeytabBasedConfigBuilder extends ConfigBuilder {

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

        // 5.Kerberos authentication
        UserGroupInformation.setConfiguration(config);
        UserGroupInformation.loginUserFromKeytab(
                settings.getPrincipal(),
                settings.getKeytab()
        );

        return config;
    }

}
