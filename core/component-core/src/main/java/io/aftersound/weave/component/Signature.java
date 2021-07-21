package io.aftersound.weave.component;

/**
 * Signature of {@link ComponentConfig}, which is used to
 * identify if one {@link ComponentConfig} matches another.
 */
public interface Signature {
    boolean match(Signature another);
}
