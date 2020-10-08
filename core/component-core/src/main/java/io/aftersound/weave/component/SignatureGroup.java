package io.aftersound.weave.component;

import java.util.ArrayList;
import java.util.List;

class SignatureGroup {

    private final String id;

    private List<Signature> signatures = new ArrayList<>();

    SignatureGroup(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public void addSignature(Signature signature) {
        signatures.add(signature);
    }

    public boolean hasMatching(Signature signature) {
        for (Signature existing : signatures) {
            if (existing.match(signature)) {
                return true;
            }
        }
        return false;
    }

}
