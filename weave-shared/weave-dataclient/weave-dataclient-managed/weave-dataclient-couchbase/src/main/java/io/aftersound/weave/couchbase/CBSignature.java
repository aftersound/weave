package io.aftersound.weave.couchbase;

import io.aftersound.weave.dataclient.Signature;

import java.util.Map;

class CBSignature implements Signature {

    private CBSignature() {
        // TODO
    }

    static CBSignature of(Map<String, Object> options) {
        // TODO
        return new CBSignature();
    }

}
