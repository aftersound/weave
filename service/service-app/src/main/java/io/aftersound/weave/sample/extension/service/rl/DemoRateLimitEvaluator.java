package io.aftersound.weave.sample.extension.service.rl;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.rl.RateLimitControl;
import io.aftersound.weave.service.rl.RateLimitDecision;
import io.aftersound.weave.service.rl.RateLimitEvaluator;
import io.aftersound.weave.service.rl.SimpleRateLimitDecision;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

public class DemoRateLimitEvaluator extends RateLimitEvaluator<HttpServletRequest> {

    public static final NamedType<RateLimitControl> COMPANION_CONTROL_TYPE = DemoRateLimitControl.TYPE;

    private final Random random = new Random();

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public RateLimitDecision evaluate(RateLimitControl control, HttpServletRequest request) {
        if (random.nextInt() % 10 == 0) {
            return SimpleRateLimitDecision.block(COMPANION_CONTROL_TYPE.name());
        } else {
            return SimpleRateLimitDecision.serve(COMPANION_CONTROL_TYPE.name());
        }
    }

}
