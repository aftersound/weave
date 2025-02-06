package io.aftersound.weave.service.runtime;

import io.aftersound.func.MasterFuncFactory;
import io.aftersound.util.Handle;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RuntimeWeaverTest {

    @Test
    public void bindAndWeave() throws Exception {
        RuntimeComponents runtimeComponents = new RuntimeWeaver().bindAndWeave(new ClassResourceRuntimeConfig());
        assertNotNull(runtimeComponents);
        assertNotNull(Handle.of("VALUE_FUNC_FACTORY", MasterFuncFactory.class).get().create("TO_STRING"));
    }

}