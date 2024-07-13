package io.aftersound.weave.util.map;

import java.util.Map;

public interface Mutation {
    <T> T on(Map<String, Object> map);
}
