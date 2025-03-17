package io.aftersound.service.runtime;

import java.util.Collection;
import java.util.Collections;

class InitializerComposite implements Initializer {

    private final Collection<Initializer> initializers;

    InitializerComposite(Collection<Initializer> initializers) {
        if (initializers != null) {
            this.initializers = Collections.unmodifiableCollection(initializers);
        } else {
            this.initializers = Collections.emptyList();
        }
    }

    @Override
    public void init(boolean tolerateException) throws Exception {
        for (Initializer initializer : initializers) {
            initializer.init(tolerateException);
        }
    }
}
