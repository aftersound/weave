package io.aftersound.weave.sql;

import io.aftersound.weave.utils.MapBuilder;

import java.sql.SQLException;
import java.util.Map;

public class MySQLHelper extends SQLHelper {

    private static final Map<Integer, ErrorCode> STANDARD_ERROR_CODE_MAP = MapBuilder.hashMap()
            .kv(1062, ErrorCode.DuplicateEntry)
            .build();

    public MySQLHelper(DatabaseMetadata databaseMetadata) {
        super(databaseMetadata);
    }

    @Override
    public ErrorCode getErrorCode(SQLException e) {
        return STANDARD_ERROR_CODE_MAP.get(e.getErrorCode());
    }
}
