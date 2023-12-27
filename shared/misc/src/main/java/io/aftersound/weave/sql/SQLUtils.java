package io.aftersound.weave.sql;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class SQLUtils {

    public static DatabaseMetadata getDatabaseMetadata(DataSource dataSource) {
        try (Connection c = dataSource.getConnection()) {
            DatabaseMetaData m = c.getMetaData();
            return DatabaseMetadata.builder()
                    .withDatabaseProductInfo(
                            m.getDatabaseProductName(),
                            m.getDatabaseProductVersion(),
                            m.getDatabaseMajorVersion(),
                            m.getDatabaseMinorVersion()
                    )
                    .withDriverInfo(
                            m.getDriverName(),
                            m.getDriverVersion(),
                            m.getDriverMajorVersion(),
                            m.getDriverMinorVersion()
                    )
                    .withJDBCInfo(
                            m.getJDBCMajorVersion(),
                            m.getJDBCMinorVersion()
                    )
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static SQLHelper getSQLHelper(DataSource dataSource) {
        DatabaseMetadata dm = SQLUtils.getDatabaseMetadata(dataSource);
        switch (dm.getDatabaseProductName()) {
            case "MySQL": {
                return new MySQLHelper(dm);
            }
            case "HSQL Database Engine": {
                return new HSQLHelper(dm);
            }
            default: {
                throw new RuntimeException(String.format("'%s' is not supported", dm.getDriverName()));
            }
        }
    }

}
