package io.aftersound.weave.hdfs;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class FileSystemFactory extends DataClientFactory<FileSystem> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of("HDFS", Endpoint.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("HDFS", FileSystem.class);

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemFactory.class);

    public FileSystemFactory(DataClientRegistry dataClientRegistry) {
        super(dataClientRegistry);
    }

    @Override
    protected FileSystem createDataClient(Map<String, Object> options) {
        try {
            Configuration config = getHdfsConfig(options);
            return FileSystem.get(config);
        } catch (Exception e) {
            LOGGER.error("failed to create Hadoop FileSystem due to", e);
            return null;
        }
    }

    @Override
    protected void destroyDataClient(FileSystem fileSystem) {
        try {
            fileSystem.close();
        } catch (IOException e) {
            LOGGER.error("failed to destroy Hadoop FileSystem due to", e);
        }
    }

    private Configuration getHdfsConfig(Map<String, Object> options) throws Exception {
        Settings settings = Settings.from(options);
        AuthMethod authMethod = settings.getAuthMethod();

        ConfigBuilder configBuilder;
        switch (authMethod) {
            case KerberosKeytab:
                configBuilder = new KeytabBasedConfigBuilder();
                break;
            case KerberosTicketCache:
                configBuilder = new TicketCacheBasedConfigBuilder();
                break;
            default:
                configBuilder = new UnspecifiedConfigBuilder();
                break;
        }
        return configBuilder.build(settings);
    }
}
