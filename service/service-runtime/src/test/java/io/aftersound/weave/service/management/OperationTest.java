package io.aftersound.weave.service.management;

import org.junit.Test;

import static org.junit.Assert.*;

public class OperationTest {

    @Test
    public void test() {
        assertNotNull(Operation.byScopeAndName(Scope.Namespace, "register"));
        assertNotNull(Operation.byScopeAndName(Scope.Namespace, "unregister"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeConfig, "create"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeConfig, "get"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeConfig, "update"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeConfig, "delete"));
    }

}