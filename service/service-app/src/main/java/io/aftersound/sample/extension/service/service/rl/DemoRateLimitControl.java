package io.aftersound.sample.extension.service.service.rl;

import io.aftersound.common.NamedType;
import io.aftersound.service.rl.RateLimitControl;

public class DemoRateLimitControl implements RateLimitControl {

    public static final NamedType<RateLimitControl> TYPE = NamedType.of("Demo", DemoRateLimitControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

}
