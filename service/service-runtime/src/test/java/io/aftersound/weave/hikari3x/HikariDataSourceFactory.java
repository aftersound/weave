package io.aftersound.weave.hikari3x;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.aftersound.config.Config;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.*;

import static io.aftersound.weave.hikari3x.HikariDataSourceConfigDictionary.*;

public class HikariDataSourceFactory extends SimpleComponentFactory<HikariDataSource> {

    public static final NamedType<ComponentConfig> COMPANION_CONTROL_TYPE = NamedType.of("Hikari3xDataSource", SimpleComponentConfig.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("Hikari3xDataSource", HikariDataSource.class);

    static {
        ComponentConfigKeysRegistry.INSTANCE.registerConfigKeys(COMPANION_CONTROL_TYPE.name(), KEYS);
    }

    public HikariDataSourceFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected HikariDataSource createComponent(Config config) {
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
        return new HikariDataSource(cfg);
    }

    @Override
    protected void destroyComponent(HikariDataSource hikariDataSource, Config config) {
        hikariDataSource.close();
    }
}
