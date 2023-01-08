package io.aftersound.weave.sample.extension.service.rl;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.rl.RateLimitControl;

public class DemoRateLimitControl implements RateLimitControl {

    public static final NamedType<RateLimitControl> TYPE = NamedType.of("Demo", DemoRateLimitControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

}
