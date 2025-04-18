package io.aftersound.service.management;

import io.aftersound.service.management.Operation;
import io.aftersound.service.management.Scope;
import org.junit.Test;

import static org.junit.Assert.*;

public class OperationTest {

    @Test
    public void test() {
        assertNotNull(Operation.byScopeAndName(Scope.Instance, "register"));
        assertNotNull(Operation.byScopeAndName(Scope.Instance, "unregister"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeSpec, "create"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeSpec, "get"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeSpec, "update"));
        assertNotNull(Operation.byScopeAndName(Scope.RuntimeSpec, "delete"));
    }

}