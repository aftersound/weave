package io.aftersound.weave.service.cache;

import io.aftersound.weave.service.cache.KeyControl;

public class MyKeyControl implements KeyControl {

    @Override
    public String getType() {
        return "MyKey";
    }

}
