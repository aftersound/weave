package io.aftersound.weave.common;

import io.aftersound.weave.mvel2.CompiledTemplates;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EvaluatorTest {

    @Test
    public void evaluateTemplate() {
        TemplateEvaluator templateEvaluator = new TemplateEvaluator(CompiledTemplates.REGISTRY.get());
        assertNull(templateEvaluator.evaluate(null, new HashMap()));
        assertEquals("", templateEvaluator.evaluate("", new HashMap()));
        assertEquals(
                "DefaultAs(100 Degree Celsius)",
                templateEvaluator.evaluate(
                        "DefaultAs(@{degree})",
                        MapBuilder.hashMap().kv("degree", "100 Degree Celsius").build()
                )
        );
    }

    @Test
    public void evaluateMultiSelection() {
        MultiSelectionEvaluator evaluator = new MultiSelectionEvaluator(CompiledTemplates.REGISTRY.get());
        MultiSelection selection;
        Map<String, String> namedElements;

        // case 1
        selection = null;
        namedElements = evaluator.evaluate(selection, new HashMap<String, Object>());
        assertEquals(0, namedElements.size());

        // case 2
        selection = new MultiSelection();
        namedElements = evaluator.evaluate(selection, new HashMap<String, Object>());
        assertEquals(0, namedElements.size());

        // case 3
        selection.setSelector("NULL_CHOICES");
        namedElements = evaluator.evaluate(selection, new HashMap<String, Object>());
        assertEquals(0, namedElements.size());

        // case 4
        selection.setSelector("EMPTY_CHOICES");
        selection.setChoices(new HashMap<String, Map<String, String>>());
        namedElements = evaluator.evaluate(selection, new HashMap<String, Object>());
        assertEquals(0, namedElements.size());

        // case 5
        selection.setSelector("NO_MATCH_CHOICE");
        selection.setChoices(
                MapBuilder.hashMap()
                        .kv("CHOICE", new HashMap<>())
                        .build()
        );
        namedElements = evaluator.evaluate(selection, new HashMap<String, Object>());
        assertEquals(0, namedElements.size());

        // case 6
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

        namedElements = evaluator.evaluate(selection, variables);
        assertEquals("`type`='brewery'", namedElements.get("TYPE"));
        assertEquals("", namedElements.get("COUNTRY"));
        assertEquals("", namedElements.get("STATE"));
        assertEquals("", namedElements.get("ZIPCODE"));
        assertEquals("", namedElements.get("NAME"));
    }

    @Test
    public void evaluateSingleSelection() {
        SingleSelectionEvaluator evaluator = new SingleSelectionEvaluator(CompiledTemplates.REGISTRY.get());
        SingleSelection singleSelection;

        // case 1
        singleSelection = null;
        assertNull(evaluator.evaluate(singleSelection, new HashMap<String, Object>()));

        // case 2
        singleSelection = new SingleSelection();
        assertNull(evaluator.evaluate(singleSelection, new HashMap<String, Object>()));

        // case 3
        singleSelection.setSelector("NULL_CHOICES_AVAILABLE");
        assertNull(evaluator.evaluate(singleSelection, new HashMap<String, Object>()));

        // case 4
        singleSelection.setSelector("EMPTY_CHOICES");
        singleSelection.setChoices(new HashMap<String, String>());
        assertNull(evaluator.evaluate(singleSelection, new HashMap<String, Object>()));

        // case 5
        singleSelection.setSelector("NO_MATCH_CHOICE");
        singleSelection.setChoices(MapBuilder.hashMap().kv("A", "Not selected").build());
        assertNull(evaluator.evaluate(singleSelection, new HashMap<String, Object>()));

        // case 6
        singleSelection.setSelector("@if{type=='brewery'}ONE@else{}TWO@end{}");
        singleSelection.setChoices(
                MapBuilder.hashMap()
                        .kv("ONE", "Brewery is top notch!")
                        .kv("TWO", "Beer tastes great!")
                        .build()
        );
        assertEquals("Beer tastes great!", evaluator.evaluate(singleSelection, MapBuilder.hashMap().kv("type", "beer").build()));
        assertEquals("Brewery is top notch!", evaluator.evaluate(singleSelection, MapBuilder.hashMap().kv("type", "brewery").build()));

        Map<String, SingleSelection> namedSelections = new HashMap<>();

        SingleSelection playerAwardSelection = new SingleSelection();
        playerAwardSelection.setSelector("@if{ranking==1}ONE@elseif{ranking==2}TWO@else{}OTHER@end{}");
        playerAwardSelection.setChoices(
                MapBuilder.hashMap()
                        .kv("ONE", "Ballon d'or of @{year}")
                        .kv("TWO", "Runner up of @{year}")
                        .kv("OTHER", "Keep up the good work!")
                        .build()
        );
        namedSelections.put("PlayerAward", playerAwardSelection);

        SingleSelection teamAwardSelection = new SingleSelection();
        teamAwardSelection.setSelector("@if{ranking==1}ONE@elseif{ranking==2}TWO@else{}OTHER@end{}");
        teamAwardSelection.setChoices(
                MapBuilder.hashMap()
                        .kv("ONE", "Best Club of @{year}")
                        .kv("TWO", "Runner up Team of @{year}")
                        .kv("OTHER", "Come next year!")
                        .build()
        );
        namedSelections.put("TeamAward", teamAwardSelection);

        Map<String, String> namedChoices = evaluator.evaluate(
                namedSelections,
                MapBuilder.hashMap()
                        .kv("ranking", 1)
                        .kv("year", 2020)
                        .build()
        );
        assertEquals("Ballon d'or of 2020", namedChoices.get("PlayerAward"));
        assertEquals("Best Club of 2020", namedChoices.get("TeamAward"));

        namedChoices = evaluator.evaluate((Map<String, SingleSelection>) null, new HashMap<String, Object>());
        assertEquals(0, namedChoices.size());

        namedChoices = evaluator.evaluate(new HashMap<String, SingleSelection>(), new HashMap<String, Object>());
        assertEquals(0, namedChoices.size());
    }

    @Test
    public void evaluateCompoundTemplate() {
        CompoundTemplate compoundTemplate = new CompoundTemplate();
        compoundTemplate.setOptionalVariables(Arrays.asList("country", "state", "zipcode", "name"));
        compoundTemplate.setTemplate("SELECT `beer-sample`.* FROM `beer-sample` WHERE @{TYPE}@{COUNTRY}@{STATE}@{ZIPCODE}@{NAME} limit @{fetchCount} offset @{skipCount}");
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
        compoundTemplate.setElements(selection);

        CompoundTemplateEvaluator evaluator = new CompoundTemplateEvaluator(CompiledTemplates.REGISTRY.get());

        // case 1
        Map<String, Object> variables = new HashMap<>(
                MapBuilder.hashMap()
                .kv("type", "brewery")
                .kv("fetchCount", 100)
                .kv("skipCount", 100)
                .kv("name", null)
                .build()
        );

        String query = evaluator.evaluate(compoundTemplate, variables);
        assertEquals("SELECT `beer-sample`.* FROM `beer-sample` WHERE `type`='brewery' limit 100 offset 100", query);

        // case 2
        variables = new HashMap<>(
                MapBuilder.hashMap()
                        .kv("type", "brewery")
                        .kv("fetchCount", 100)
                        .kv("skipCount", 100)
                        .kv("country", "US")
                        .build()
        );

        query = evaluator.evaluate(compoundTemplate, variables);
        assertEquals("SELECT `beer-sample`.* FROM `beer-sample` WHERE `type`='brewery' AND `country` like '%US%' limit 100 offset 100", query);

    }


}