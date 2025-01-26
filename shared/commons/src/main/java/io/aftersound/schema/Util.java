package io.aftersound.schema;

import io.aftersound.dict.Dictionary;
import io.aftersound.dict.SimpleDictionary;
import io.aftersound.func.FuncFactory;

import java.util.List;

class Util {

    public static Dictionary<Directive> initDirectivesAndCreateDictionary(
            List<Directive> directives, FuncFactory funcFactory) {

        if (directives != null) {
            for (Directive directive : directives) {
                directive.init(funcFactory);
            }
        }

        return SimpleDictionary.<Directive>builder()
                .withEntries(directives)
                .withEntryNameFunc(Directive::getLabel)
                .build();
    }

    public static Dictionary<Field> createFieldictionary(String name, List<Field> fields) {
        return SimpleDictionary.<Field>builder(name)
                .withEntries(fields)
                .withEntryNameFunc(Field::getName)
                .withAttributeAccessor(FieldAttributeAccessor.INSTANCE)
                .build();
    }

}
