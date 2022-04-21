package io.aftersound.weave.service.runtime;

import io.aftersound.weave.common.MasterValueFuncFactory;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RuntimeWeaverTest {

    @Test
    public void bindAndWeave() throws Exception {
        RuntimeComponents runtimeComponents = new RuntimeWeaver().bindAndWeave(new ClassResourceRuntimeConfig());
        assertNotNull(runtimeComponents);
        assertNotNull(MasterValueFuncFactory.create("TO_STRING"));
    }

}