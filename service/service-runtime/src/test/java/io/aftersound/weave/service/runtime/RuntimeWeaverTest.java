package io.aftersound.weave.service.runtime;

import io.aftersound.func.FuncFactory;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.util.Handle;
import io.aftersound.weave.common.MyValueFuncFactory;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RuntimeWeaverTest {

    @Test
    public void bindAndWeave() throws Exception {
        Handle.of("VALUE_FUNC_FACTORY", FuncFactory.class).setAndLock(
                new MasterFuncFactory(
                        new MyValueFuncFactory()
                )
        );
        RuntimeComponents runtimeComponents = new RuntimeWeaver().bindAndWeave(new ClassResourceRuntimeConfig());
        assertNotNull(runtimeComponents);
        assertNotNull(Handle.of("VALUE_FUNC_FACTORY", FuncFactory.class).get().create("TO_STRING"));
    }

}