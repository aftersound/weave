package io.aftersound.weave.sql;

public class DatabaseMetadata {
    private final String databaseProductName;
    private final String databaseProductVersion;
    private final int databaseMajorVersion;
    private final int databaseMinorVersion;
    private final String driverName;
    private final String driverVersion;
    private final int driverMajorVersion;
    private final int driverMinorVersion;
    private final int jdbcMajorVersion;
    private final int jdbcMinorVersion;

    private DatabaseMetadata(
            String databaseProductName,
            String databaseProductVersion,
            int databaseMajorVersion,
            int databaseMinorVersion,
            String driverName,
            String driverVersion,
            int driverMajorVersion,
            int driverMinorVersion,
            int jdbcMajorVersion,
            int jdbcMinorVersion) {
        this.databaseProductName = databaseProductName;
        this.databaseProductVersion = databaseProductVersion;
        this.databaseMajorVersion = databaseMajorVersion;
        this.databaseMinorVersion = databaseMinorVersion;
        this.driverName = driverName;
        this.driverVersion = driverVersion;
        this.driverMajorVersion = driverMajorVersion;
        this.driverMinorVersion = driverMinorVersion;
        this.jdbcMajorVersion = jdbcMajorVersion;
        this.jdbcMinorVersion = jdbcMinorVersion;
    }

    public String getDatabaseProductName() {
        return databaseProductName;
    }

    public String getDatabaseProductVersion() {
        return databaseProductVersion;
    }

    public int getDatabaseMajorVersion() {
        return databaseMajorVersion;
    }

    public int getDatabaseMinorVersion() {
        return databaseMinorVersion;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getDriverVersion() {
        return driverVersion;
    }

    public int getDriverMajorVersion() {
        return driverMajorVersion;
    }

    public int getDriverMinorVersion() {
        return driverMinorVersion;
    }

    public int getJdbcMajorVersion() {
        return jdbcMajorVersion;
    }

    public int getJdbcMinorVersion() {
        return jdbcMinorVersion;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String databaseProductName;
        private String databaseProductVersion;
        private int databaseMajorVersion;
        private int databaseMinorVersion;

        private String driverName;
        private int driverMajorVersion;
        private int driverMinorVersion;
        private String driverVersion;

        private int jdbcMajorVersion;
        private int jdbcMinorVersion;

        private Builder() {
        }

        public Builder withDatabaseProductInfo(String name, String version, int majorVersion, int minorVersion) {
            this.databaseProductName = name;
            this.databaseProductVersion = version;
            this.databaseMajorVersion = majorVersion;
            this.databaseMinorVersion = minorVersion;
            return this;
        }

        public Builder withDriverInfo(String name, String version, int majorVersion, int minorVersion) {
            this.driverName = name;
            this.driverVersion = version;
            this.driverMajorVersion = majorVersion;
            this.driverMinorVersion = minorVersion;
            return this;
        }

        public Builder withJDBCInfo(int majorVersion, int minorVersion) {
            this.jdbcMajorVersion = majorVersion;
            this.jdbcMinorVersion = minorVersion;
            return this;
        }

        public DatabaseMetadata build() {
            return new DatabaseMetadata(
                    databaseProductName,
                    databaseProductVersion,
                    databaseMajorVersion,
                    databaseMinorVersion,
                    driverName,
                    driverVersion,
                    driverMajorVersion,
                    driverMinorVersion,
                    jdbcMajorVersion,
                    jdbcMinorVersion
            );
        }

    }
}
