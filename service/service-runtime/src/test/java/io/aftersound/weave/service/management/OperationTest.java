package io.aftersound.weave.service.management;

import org.junit.Test;

import static org.junit.Assert.*;

public class OperationTest {

    @Test
    public void test() {
        assertNotNull(Operation.byScopeAndName(Scope.Namespace, "register"));
        assertNotNull(Operation.byScopeAndName(Scope.Namespace, "unregister"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeSpec, "create"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeSpec, "get"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeSpec, "update"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeSpec, "delete"));
    }

}