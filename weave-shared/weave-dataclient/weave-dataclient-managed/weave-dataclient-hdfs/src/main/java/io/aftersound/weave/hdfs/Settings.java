package io.aftersound.weave.hdfs;

import io.aftersound.weave.utils.Options;
import org.apache.hadoop.fs.FileSystem;

import java.util.Map;

class Settings {

    static final String HADOOP_SECURITY_KERBEROS_TICKET_CACHE_PATH = "hadoop.security.kerberos.ticket.cache.path";
    private static final String HADOOP_FS_DEFAULT_NAME_KEY = FileSystem.FS_DEFAULT_NAME_KEY;

    private static final String CORE_SITE_XML_PATH = "coreSiteXmlPath";
    private static final String BASE64_ENCODED_CORE_SITE_CONFIG_XML = "base64EncodedCoreSiteConfigXml";
    static final String CORE_SITE_CONFIG_XML_INSTALLATION_PATH = "coreSiteConfigXmlInstallationPath";

    private static final String HDFS_SITE_XML_PATH = "hdfsSiteXmlPath";
    private static final String BASE64_ENCODED_HDFS_SITE_CONFIG_XML = "base64EncodedHdfsSiteConfigXml";
    static final String HDFS_SITE_CONFIG_XML_INSTALLATION_PATH = "hdfsSiteConfigXmlInstallationPath";

    private static final String AUTH_METHOD = "authMethod";
    private static final String PRINCIAL = "principal";
    private static final String KEYTAB = "keytab";

    final Options options;

    private Settings(Options options) {
        this.options = options;
    }

    static Settings from(Map<String, Object> options) {
        return new Settings(Options.from(options));
    }

    String getCoreSiteXmlPath() {
        return options.get(CORE_SITE_XML_PATH);
    }

    String getBase64EncodedCoreSiteConfigXml() {
        return options.get(BASE64_ENCODED_CORE_SITE_CONFIG_XML);
    }

    String getCoreSiteConfigXmlInstallationPath() {
        return options.get(CORE_SITE_CONFIG_XML_INSTALLATION_PATH);
    }

    String getHdfsSiteXmlPath() {
        return options.get(HDFS_SITE_XML_PATH);
    }

    String getBase64EncodedHdfsSiteConfigXml() {
        return options.get(BASE64_ENCODED_HDFS_SITE_CONFIG_XML);
    }

    String getHdfsSiteConfigXmlInstallationPath() {
        return options.get(HDFS_SITE_CONFIG_XML_INSTALLATION_PATH);
    }

    String getFsDefaultName() {
        return options.get(HADOOP_FS_DEFAULT_NAME_KEY);
    }

    AuthMethod getAuthMethod() {
        String method = options.get(AUTH_METHOD);
        try {
            return AuthMethod.valueOf(method);
        } catch (Exception e) {
            return AuthMethod.Unspecified;
        }
    }

    String getPrincipal() {
        return options.get(PRINCIAL);
    }

    String getKeytab() {
        return options.get(KEYTAB);
    }

    String getTicketCachePath() {
        return options.get(HADOOP_SECURITY_KERBEROS_TICKET_CACHE_PATH);
    }

}
