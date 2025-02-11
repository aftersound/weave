package io.aftersound.func;

import io.aftersound.util.TreeNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FuncFactoryTest {

    @Test
    public void getDescriptors() {
        List<Descriptor> descriptors =
                new FuncFactory() {

                    @Override
                    public <IN, OUT> Func<IN, OUT> create(TreeNode directive) {
                        return null;
                    }

                }
                .getFuncDescriptors();
        assertTrue(descriptors.isEmpty());
    }

}