package io.aftersound.weave.common;

import io.aftersound.weave.common.valuefunc.Descriptor;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MasterValueFuncFactoryTest {

    @BeforeClass
    public static void setup() throws Exception {
        MasterValueFuncFactory.init(T1ValueFuncFactory.class.getName(), T2ValueFuncFactory.class.getName());
    }

    @Test
    public void getManagedValueFuncDescriptors() {
        Map<String, Descriptor> descriptors = MasterValueFuncFactory.getManagedValueFuncDescriptors();
        assertEquals(2, descriptors.size());
        assertNotNull(MasterValueFuncFactory.getManagedValueFuncDescriptor("T1:LOWER_CASE"));
        assertNotNull(MasterValueFuncFactory.getManagedValueFuncDescriptor("T1:LC"));
        assertNotNull(MasterValueFuncFactory.getManagedValueFuncDescriptor("T2:UPPER_CASE"));
    }

    @Test
    public void create() {
        ValueFunc<Object, Object> f;

        f = MasterValueFuncFactory.create("T1:LOWER_CASE");
        assertNotNull(f);
        assertEquals("hello", f.apply("HELLO"));

        f = MasterValueFuncFactory.create("T2:UPPER_CASE");
        assertNotNull(f);
        assertEquals("HELLO", f.apply("hello"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNotSupported() {
        MasterValueFuncFactory.create("T1:ABS()");
    }

}