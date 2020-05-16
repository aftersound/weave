package io.aftersound.weave.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResultTest {

    @Test
    public void isSuccess() {
        Result result = Result.success();
        assertTrue(result.isSuccess());
    }

    @Test
    public void isFailure() {
        Result result = Result.failure("reason");
        assertTrue(result.isFailure());
    }

}