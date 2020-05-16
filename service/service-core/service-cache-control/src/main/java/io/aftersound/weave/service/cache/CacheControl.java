package io.aftersound.weave.service.cache;

import io.aftersound.weave.metadata.Control;

public interface CacheControl extends Control {
    KeyControl getKeyControl();
}
