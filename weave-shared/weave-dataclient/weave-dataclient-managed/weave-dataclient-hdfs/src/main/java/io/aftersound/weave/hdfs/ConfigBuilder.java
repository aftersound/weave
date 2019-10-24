package io.aftersound.weave.hdfs;

import io.aftersound.weave.exception.InvalidConfigException;
import io.aftersound.weave.file.PathHandle;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

abstract class ConfigBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigBuilder.class);

    abstract Configuration build(Settings settings) throws Exception;

    protected final void processCoreSiteXml(Configuration config, Settings settings) throws Exception {
        String coreSiteXml = settings.getCoreSiteXmlPath();
        String encodedCoreSiteConfigXml = settings.getBase64EncodedCoreSiteConfigXml();
        if (coreSiteXml != null) {
            // if core-site.xml is explicitly specified, use it
            Path coreSiteXmlPath = PathHandle.of(coreSiteXml).path();
            config.addResource(new org.apache.hadoop.fs.Path(coreSiteXmlPath.toString()));
            LOGGER.info("Use core-site.xml at " + coreSiteXmlPath.toString());
        } else if (encodedCoreSiteConfigXml != null) {
            // if base64 encoded core-site.xml is specified, decode and install it, use it
            String coreSiteConfigInstallationPath = settings.getCoreSiteConfigXmlInstallationPath();
            if (coreSiteConfigInstallationPath == null || coreSiteConfigInstallationPath.trim().isEmpty()) {
                throw new InvalidConfigException(Settings.CORE_SITE_CONFIG_XML_INSTALLATION_PATH + "is missing");
            }
            Path coreSiteXmlPath = ConfigInstaller.install(encodedCoreSiteConfigXml, coreSiteConfigInstallationPath);
            config.addResource(new org.apache.hadoop.fs.Path(coreSiteXmlPath.toString()));
            LOGGER.info("Use core-site.xml at " + coreSiteXmlPath.toString());
        } else {
            LOGGER.info("Use no or default core-site.xml in package");
        }
    }

    protected final void processHdfsSiteXml(Configuration config, Settings settings) throws Exception {
        String hdfsSiteXml = settings.getHdfsSiteXmlPath();
        String encodedHdfsSiteConfigXml = settings.getBase64EncodedHdfsSiteConfigXml();
        if (hdfsSiteXml != null) {
            // if hdfs-site.xml is explicitly specified, use it
            Path hdfsSiteXmlPath = PathHandle.of(hdfsSiteXml).path();
            config.addResource(new org.apache.hadoop.fs.Path(hdfsSiteXmlPath.toString()));
            LOGGER.info("Use hdfs-site.xml at " + hdfsSiteXmlPath.toString());
        } else if (encodedHdfsSiteConfigXml != null) {
            // if base64 encoded hdfs-site.xml is specified, decode and install it, use it
            String hdfsSiteConfigInstallationPath = settings.getHdfsSiteConfigXmlInstallationPath();
            if (hdfsSiteConfigInstallationPath == null || hdfsSiteConfigInstallationPath.trim().isEmpty()) {
                throw new InvalidConfigException(Settings.HDFS_SITE_CONFIG_XML_INSTALLATION_PATH + "is missing");
            }
            Path hdfsSiteXmlPath = ConfigInstaller.install(encodedHdfsSiteConfigXml, hdfsSiteConfigInstallationPath);
            config.addResource(new org.apache.hadoop.fs.Path(hdfsSiteXmlPath.toString()));
            LOGGER.info("Use hdfs-site.xml at " + hdfsSiteXmlPath.toString());
        } else {
            LOGGER.info("Use no or default hdfs-site.xml in package");
        }
    }

    protected final void processHadoopConfigs(Configuration config, Settings settings) {
        for (String k : settings.options.keys()) {
            if (k.startsWith("hadoop.")) {
                String v = settings.options.get(k);
                config.set(k, v);
            }
        }
    }

}
