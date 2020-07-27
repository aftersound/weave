package io.aftersound.weave.resource;

import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleResourceManagerTest {

    @Test
    public void testSimpleResourceManager() {
        ResourceManager resourceManager = SimpleResourceManager.withDependingResourceTypes(
                new ResourceType(
                        Object.class.getName(),
                        Object.class
                )
        );

        ResourceDeclaration resourceDeclaration = resourceManager.getDeclaration();
        assertNotNull(resourceDeclaration);
        assertEquals(1, resourceDeclaration.getDependingResourceTypes().length);
        assertEquals("java.lang.Object", resourceDeclaration.getDependingResourceTypes()[0].name());
        assertFalse(resourceManager.accept(null));
    }
}