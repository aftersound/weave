package io.aftersound.weave.sql;

import io.aftersound.weave.utils.MapBuilder;

import java.util.Map;

public class HSQLHelper extends SQLHelper {

    private static final Map<Integer, ErrorCode> STANDARD_ERROR_CODE_MAP = MapBuilder.hashMap()
            .kv(-104, ErrorCode.DuplicateEntry)
            .build();

    public HSQLHelper(DatabaseMetadata dm) {
        super(dm);
    }

    @Override
    protected Map<Integer, ErrorCode> getErrorCodeMapping() {
        return STANDARD_ERROR_CODE_MAP;
    }

}
