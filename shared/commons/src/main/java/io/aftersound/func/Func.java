package io.aftersound.func;

import io.aftersound.util.WithHints;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;

/**
 * Represents a conceptual function that accepts input and produces output.
 * It might act upon hints, if available.
 *
 * @param <IN> - the type of input to the function
 * @param <OUT> - the type of output of the function
 */

public interface Func<IN, OUT> extends Function<IN, OUT>, Serializable {
}
