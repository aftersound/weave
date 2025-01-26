package io.aftersound.schema;

import io.aftersound.dict.Dictionary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    public void createFieldDictionary() {
        Dictionary<Field> dict = Util.createFieldictionary(
                "Person",
                List.of(
                        Field.stringFieldBuilder("firstName").build(),
                        Field.stringFieldBuilder("lastName").build()
                )
        );
        assertNotNull(dict);
        assertEquals(2, dict.all().size());
    }

}