package io.aftersound.weave.metadata;

import java.util.List;

public interface ValueParser {
    Object parseMultiValues(List<String> rawValues);
    Object parseSingleValue(List<String> rawValues);
}
