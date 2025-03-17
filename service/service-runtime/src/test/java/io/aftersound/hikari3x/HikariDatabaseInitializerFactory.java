package io.aftersound.hikari3x;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.aftersound.component.*;
import io.aftersound.config.Config;
import io.aftersound.common.NamedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.util.Date;

import static io.aftersound.hikari3x.HikariDatabaseInitializerConfigDictionary.*;

public class HikariDatabaseInitializerFactory extends SimpleComponentFactory<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HikariDatabaseInitializerFactory.class);

    public static final NamedType<ComponentConfig> COMPANION_CONTROL_TYPE = NamedType.of("Hikari3xDatabaseInitializer", SimpleComponentConfig.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("Hikari3xDatabaseInitializer", String.class);

    static {
        ComponentConfigKeysRegistry.INSTANCE.registerConfigKeys(COMPANION_CONTROL_TYPE.name(), KEYS);
    }

    public HikariDatabaseInitializerFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected String createComponent(Config config) {
        HikariConfig cfg = new HikariConfig();
        cfg.setPoolName(config.v(POOL_NAME));
        cfg.setCatalog(config.v(CATALOG));
        cfg.setDriverClassName(config.v(DRIVER_CLASS_NAME));
        cfg.setJdbcUrl(config.v(JDBC_URL));
        cfg.setMinimumIdle(config.v(MINIMUM_IDLE));
        cfg.setMaximumPoolSize(config.v(MAXIMUM_POOL_SIZE));
        cfg.setConnectionTimeout(config.v(CONNECTION_TIMEOUT));
        cfg.setIdleTimeout(config.v(IDLE_TIMEOUT));
        cfg.setMaxLifetime(config.v(MAX_LIFE_TIME));
        cfg.setLeakDetectionThreshold(config.v(LEAK_DETECTION_THRESHOLD));
        cfg.setAutoCommit(config.v(AUTO_COMMIT));
        cfg.setConnectionTestQuery(config.v(CONNECTION_TEST_QUERY));
        cfg.setUsername(config.v(USERNAME));
        cfg.setPassword(config.v(PASSWORD));
        HikariDataSource dataSource = new HikariDataSource(cfg);

        String initScript = config.v(INIT_SCRIPT);
        try {
            runInitScript(dataSource, initScript);
            return "Hikari3xDatabaseInitializer triggered @ " + new Date();
        } catch (Exception e) {
            throw new ComponentCreateException("failed to run init script", e);
        } finally {
            dataSource.close();
        }
    }

    @Override
    protected void destroyComponent(String nothing, Config config) {
        // DO NOTHING
    }

    private void runInitScript(HikariDataSource dataSource, String initScript) throws Exception {
        Reader reader = null;
        try (Connection connection = dataSource.getConnection()) {
            reader = new StringReader(initScript);
            new ScriptRunner(connection).runScript(reader);
        } catch (Exception e) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // DO NOTHING
                }
            }
            throw e;
        }
    }
}
