package io.aftersound.weave.service.management;

import io.aftersound.weave.fs.Reader;

import java.io.InputStream;

final class NamespacesReader extends Reader<Namespace[]> {
    @Override
    protected Namespace[] read(InputStream is) throws Exception {
        return Helper.MAPPER.readValue(is, Namespace[].class);
    }
}
