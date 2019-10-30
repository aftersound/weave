package io.aftersound.weave.dataclient;

public interface Signature {
    boolean match(Signature another);
}
