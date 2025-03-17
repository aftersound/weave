package io.aftersound.sample.extension.service.service.rl;

import io.aftersound.common.NamedType;
import io.aftersound.service.rl.RateLimitControl;
import io.aftersound.service.rl.RateLimitDecision;
import io.aftersound.service.rl.RateLimitEvaluator;
import io.aftersound.service.rl.SimpleRateLimitDecision;

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
