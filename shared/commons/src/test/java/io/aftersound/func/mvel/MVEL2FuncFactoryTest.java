package io.aftersound.func.mvel;

import io.aftersound.func.CreationException;
import io.aftersound.func.Func;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.func.ResourceAware;
import io.aftersound.util.Handle;
import io.aftersound.util.ResourceRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MVEL2FuncFactoryTest {

    @Test
    public void getFuncDescriptors() {
        assertEquals(2, new MVEL2FuncFactory().getFuncDescriptors().size());
    }

    @Test
    public void evalFunc() throws Exception {
        final MasterFuncFactory funcFactory = MasterFuncFactory.of(MVEL2FuncFactory.class.getName());

        final Person person = new Person();
        person.setFirstName("Nikola");
        person.setLastName("Tesla");

        Func<Object, Object> func = funcFactory.create("MVEL2:EVAL(user)");
        if (func instanceof ResourceAware) {
            ((ResourceAware) func).bindResources(Map.of("expression", "user.firstName"));
        }
        assertEquals("Nikola", func.apply(person));

        func = funcFactory.create("MVEL2:EVAL(user)");
        if (func instanceof ResourceAware) {
            ((ResourceAware) func).bindResources(Map.of("expression", "user.lastName"));
        }
        assertEquals("Tesla", func.apply(person));

        func = funcFactory.create("MVEL2:EVAL(user)");
        if (func instanceof ResourceAware) {
            ((ResourceAware) func).bindResources(Map.of("expression", "user.getFirstName()"));
        }
        assertEquals("Nikola", func.apply(person));

        func = funcFactory.create("MVEL2:EVAL(user)");
        if (func instanceof ResourceAware) {
            ((ResourceAware) func).bindResources(Map.of("expression", "user.getLastName()"));
        }
        assertEquals("Tesla", func.apply(person));

        // Exception case: required dependency is missing
        assertThrows(
                CreationException.class,
                () -> funcFactory.create("MVEL2:EVAL(user,fn.mvel)")
        );

        ResourceRegistry resourceRegistry = Handle.of(ResourceRegistry.class.getSimpleName(), ResourceRegistry.class)
                .setAndLock(new ResourceRegistry())
                .get();

        // Exception case: MVEL2 expression is missing
        assertThrows(
                CreationException.class,
                () -> funcFactory.create("MVEL2:EVAL(user,fn.mvel)")
        );

        resourceRegistry.register("fn.mvel", "user.getFirstName()");
        func = funcFactory.create("MVEL2:EVAL(user,fn.mvel)");
        assertEquals("Nikola", func.apply(person));
    }

    @Test
    public void evalFunc1() throws Exception {
        final MasterFuncFactory funcFactory = MasterFuncFactory.of(MVEL2FuncFactory.class.getName());

        final Person person = new Person();
        person.setFirstName("Nikola");
        person.setLastName("Tesla");

        Map<String, Object> map = Map.of("user", person);

        assertEquals("Nikola", funcFactory.create("MVEL2:EVAL1(user.firstName)").apply(map));
        assertEquals("Tesla", funcFactory.create("MVEL2:EVAL1(user.lastName)").apply(map));
        assertEquals("Nikola", funcFactory.create("MVEL2:EVAL1(user.getFirstName())").apply(map));
        assertEquals("Tesla", funcFactory.create("MVEL2:EVAL1(user.getLastName())").apply(map));
    }

    public static class Person {
        private String firstName;
        private String lastName;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

}