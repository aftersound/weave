package io.aftersound.weave.service.rl;

import io.aftersound.metadata.Control;

import java.io.Serializable;

/**
 * Conceptual entity represents the {@link Control} which controls
 * the behavior of pairing rate limit handler
 */
public interface RateLimitControl extends Control, Serializable {
}
