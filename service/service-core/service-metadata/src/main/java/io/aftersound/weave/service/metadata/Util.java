package io.aftersound.weave.service.metadata;

public class Util {

    public static <EC extends ExecutionControl> EC safeCast(ExecutionControl control, Class<EC> extended) {
        if (extended.isInstance(control)) {
            return extended.cast(control);
        } else {
            return null;
        }
    }
}
