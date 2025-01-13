package io.aftersound.expr;

import io.aftersound.dict.Dictionary;
import io.aftersound.schema.Field;
import io.aftersound.util.TreeNode;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TranslatorTest {

    @Test
    public void test() {
        Dictionary<Field> dictionary = new Fields(
                Field.stringFieldBuilder("firstName").build(),
                Field.stringFieldBuilder("lastName").build(),
                Field.integerFieldBuilder("age").build()
        );
        Function<TreeNode, Expr> translator = TranslatorFactory.getTranslator(dictionary);
    }

}