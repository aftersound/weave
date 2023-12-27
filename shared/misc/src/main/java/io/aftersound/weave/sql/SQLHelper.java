package io.aftersound.weave.sql;

import java.sql.SQLException;
import java.util.Map;

public abstract class SQLHelper {

    private final DatabaseMetadata databaseMetadata;

    protected SQLHelper(DatabaseMetadata databaseMetadata) {
        this.databaseMetadata = databaseMetadata;
    }

    public final DatabaseMetadata getDatabaseMetadata() {
        return databaseMetadata;
    }

    public final ErrorCode getErrorCode(SQLException e) {
        return getErrorCodeMapping().get(e.getErrorCode());
    }

    protected abstract Map<Integer, ErrorCode> getErrorCodeMapping();
}
