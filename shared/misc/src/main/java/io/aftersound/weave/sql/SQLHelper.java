package io.aftersound.weave.sql;

import java.sql.SQLException;

public abstract class SQLHelper {

    private final DatabaseMetadata databaseMetadata;

    protected SQLHelper(DatabaseMetadata databaseMetadata) {
        this.databaseMetadata = databaseMetadata;
    }

    public final DatabaseMetadata getDatabaseMetadata() {
        return databaseMetadata;
    }

    public abstract ErrorCode getErrorCode(SQLException e);
}
