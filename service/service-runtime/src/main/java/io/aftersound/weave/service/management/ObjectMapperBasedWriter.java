package io.aftersound.weave.service.management;

import io.aftersound.weave.fs.Writer;

import java.io.OutputStream;

final class ObjectMapperBasedWriter<T> extends Writer<T> {

    @Override
    protected void write(OutputStream outputStream, T value) throws Exception {
        Helper.MAPPER.writeValue(outputStream, value);
    }

}
