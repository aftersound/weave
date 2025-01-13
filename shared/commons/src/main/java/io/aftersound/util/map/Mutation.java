package io.aftersound.util.map;

import java.util.Map;

public interface Mutation {
    <T> T on(Map<String, Object> map);
}
