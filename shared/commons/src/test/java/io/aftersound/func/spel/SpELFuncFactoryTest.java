package io.aftersound.func.spel;

import io.aftersound.bean.Person;
import io.aftersound.func.MasterFuncFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpELFuncFactoryTest {

    @Test
    public void getFuncDescriptors() {
        assertEquals(1, new SpELFuncFactory().getFuncDescriptors().size());
    }

    @Test
    public void evalFunc() throws Exception {
        MasterFuncFactory masterFuncFactory = MasterFuncFactory.of(SpELFuncFactory.class.getName());
        assertEquals(3, masterFuncFactory.create("SpEL:EVAL(1+2)").apply(null));

        assertEquals("John", masterFuncFactory.create("SpEL:EVAL(firstName)").apply(Person.as("John", "Doe")));
    }

}
