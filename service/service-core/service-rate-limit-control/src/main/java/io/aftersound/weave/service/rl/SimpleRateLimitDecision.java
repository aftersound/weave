package io.aftersound.weave.service.rl;

import io.aftersound.weave.common.Key;

import java.util.HashMap;
import java.util.Map;

public class SimpleRateLimitDecision implements RateLimitDecision {

    private final String type;
    private final Code code;

    private final Map<Key<?>, Object> attributes = new HashMap<>();

    private SimpleRateLimitDecision(String type, Code decisionCode) {
        this.type = type;
        this.code = decisionCode;
    }

    public static RateLimitDecision serve(String rateLimitType) {
        return new SimpleRateLimitDecision(rateLimitType, Code.Serve);
    }

    public static RateLimitDecision block(String rateLimitType) {
        return new SimpleRateLimitDecision(rateLimitType, Code.Block);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Code getCode() {
        return code;
    }

    @Override
    public boolean isBlock() {
        return Code.Block == code;
    }

    public <T> SimpleRateLimitDecision withAttr(Key<T> attrKey, T attrValue) {
        attributes.put(attrKey, attrKey);
        return this;
    }

    @Override
    public <T> T get(Key<T> key) {
        return (T) attributes.get(key);
    }

}
