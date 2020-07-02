package io.aftersound.weave.common;

import io.aftersound.weave.mvel2.CompiledTemplates;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MultiSelectionEvaluatorTest {

    @Test
    public void evaluate() {
        QueryTemplate queryTemplate = new QueryTemplate();
        queryTemplate.setTemplate("SELECT `beer-sample`.* FROM `beer-sample` WHERE @{TYPE}@{COUNTRY}@{STATE}@{ZIPCODE}@{NAME} limit @{fetchCount} offset @{skipCount}");
        MultiSelection selection = new MultiSelection();
        selection.setSelector("@if{type=='brewery'}brewery@else{}beer@end{}");
        selection.setChoices(
                MapBuilder.hashMap()
                        .kv(
                                "brewery",
                                MapBuilder.hashMap()
                                        .kv("TYPE", "`type`='brewery'")
                                        .kv("COUNTRY", "@if{country != null && country != ''} AND `country` like '%@{country}%'@end{}")
                                        .kv("STATE", "@if{state != null && state != ''} AND `state` like '%@{state}%'@end{}")
                                        .kv("ZIPCODE", "@if{zipcode != null && zipcode != ''} AND `code` like '%@{zipcode}%'@end{}")
                                        .kv("NAME", "@if{name != null && name != ''} AND `name` like '%@{name}%'@end{}")
                                        .build()
                        )
                        .kv(
                                "beer",
                                MapBuilder.hashMap()
                                        .kv("TYPE", "`type`='beer'")
                                        .kv("COUNTRY", "@if{country != null && country != ''} AND `country` like '%@{country}%'@end{}")
                                        .kv("NAME", "@if{name != null && name != ''} AND `name` like '%@{name}%'@end{}")
                                        .build()
                        )
                        .build()
        );
        queryTemplate.setElementSelection(selection);

        TemplateEvaluator templateEvaluator = new TemplateEvaluator(CompiledTemplates.REGISTRY.get());
        MultiSelectionEvaluator selectionEvaluator = new MultiSelectionEvaluator(templateEvaluator);

        // case 1
        Map<String, Object> variables = new HashMap<>(
                MapBuilder.hashMap()
                .kv("type", "brewery")
                .kv("fetchCount", 100)
                .kv("skipCount", 100)
                .kv("country", null)
                .kv("state", null)
                .kv("zipcode", null)
                .kv("name", null)
                .build()
        );

        Map<String, String> elementByName = selectionEvaluator.evaluateSelection(queryTemplate.getElementSelection(), variables);
        variables.putAll(elementByName);
        String query = templateEvaluator.evaluate(queryTemplate.getTemplate(), variables);
        assertEquals("SELECT `beer-sample`.* FROM `beer-sample` WHERE `type`='brewery' limit 100 offset 100", query);

        // case 2
        variables = new HashMap<>(
                MapBuilder.hashMap()
                        .kv("type", "brewery")
                        .kv("fetchCount", 100)
                        .kv("skipCount", 100)
                        .kv("country", "US")
                        .kv("state", null)
                        .kv("zipcode", null)
                        .kv("name", null)
                        .build()
        );

        elementByName = selectionEvaluator.evaluateSelection(queryTemplate.getElementSelection(), variables);
        variables.putAll(elementByName);
        query = templateEvaluator.evaluate(queryTemplate.getTemplate(), variables);
        assertEquals("SELECT `beer-sample`.* FROM `beer-sample` WHERE `type`='brewery' AND `country` like '%US%' limit 100 offset 100", query);
    }
}