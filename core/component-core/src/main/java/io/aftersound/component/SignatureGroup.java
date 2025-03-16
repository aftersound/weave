package io.aftersound.component;

import io.aftersound.util.Pair;

import java.util.ArrayList;
import java.util.List;

class SignatureGroup {

    private final String componentFactoryType;

    private final List<Pair<String, Signature>> idAndSignatureList = new ArrayList<>();

    SignatureGroup(String componentFactoryType) {
        this.componentFactoryType = componentFactoryType;
    }

    public String type() {
        return componentFactoryType;
    }

    public void addSignature(String componentId, Signature signature) {
        idAndSignatureList.add(Pair.of(componentId, signature));
    }

    public String getComponentIdWithMatchingSignature(Signature signature) {
        for (Pair<String, Signature> p : idAndSignatureList) {
            if (p.second().match(signature)) {
                return p.first();
            }
        }
        return null;
    }

    public void removeSignature(String componentId) {
        Pair<String, Signature> target = null;
        for (Pair<String, Signature> p : idAndSignatureList) {
            if (p.first().equals(componentId)) {
                target = p;
            }
        }
        idAndSignatureList.remove(target);
    }

}
