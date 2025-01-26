package io.aftersound.expr;

import io.aftersound.dict.Dictionary;
import io.aftersound.dict.SimpleDictionary;
import io.aftersound.schema.Field;
import io.aftersound.schema.FieldAttributeAccessor;
import io.aftersound.util.TreeNode;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TranslatorTest {

    @Test
    public void test() throws Exception {
        Dictionary<Field> dictionary = SimpleDictionary.<Field>builder("Person")
                .withEntries(
                        List.of(
                                Field.stringFieldBuilder("firstName").build(),
                                Field.stringFieldBuilder("lastName").build(),
                                Field.integerFieldBuilder("age").build()
                        )
                )
                .withEntryNameFunc(Field::getName)
                .withAttributeAccessor(FieldAttributeAccessor.INSTANCE)
                .build();
        Function<TreeNode, Expr> translator = TranslatorFactory.getTranslator(dictionary);
        assertNotNull(translator);

        Expr filter = translator.apply(TreeNode.from("NOT(OR(LE(age,16),GE(age,70)))"));
        assertNotNull(filter);
    }

}