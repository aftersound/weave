package io.aftersound.weave.service.message;

import org.junit.Test;

import static org.junit.Assert.*;
public class  MessageRegistryTest {

    @Test
    public void internalServerError() {
        assertNotNull(MessageRegistry.INTERNAL_SERVICE_ERROR.error(new Exception("Something is wrong").getMessage()));
        assertNotNull(MessageRegistry.INTERNAL_SERVICE_ERROR.error("user lacks privilege or object not found: $.JOBRUNNER.TYPE"));
    }
  
}