package io.aftersound.mvel2;

import io.aftersound.util.MapBuilder;
import io.aftersound.util.Range;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class EvaluatorTest {

    @Test
    public void evaluateTemplate() {
        TemplateEvaluator templateEvaluator = new TemplateEvaluator(CompiledTemplates.REGISTRY.get());
        assertNull(templateEvaluator.evaluate(null, new HashMap<>()));
        assertEquals("", templateEvaluator.evaluate("", new HashMap<>()));
        assertEquals(
                "DefaultAs(100 Degree Celsius)",
                templateEvaluator.evaluate(
                        "DefaultAs(@{degree})",
                        MapBuilder.<String, Object>hashMap().put("degree", "100 Degree Celsius").build()
                )
        );
    }

    @Test
    public void evaluateTemplateOnVariableOfComplexType() {
        TemplateEvaluator templateEvaluator = new TemplateEvaluator(CompiledTemplates.REGISTRY.get());
        Range<String> range = new Range<>();
        Map<String, Object> variables = MapBuilder.<String, Object>hashMap().put("range", range).build();

        // case 1 lower inclusive
        range.setLowerInclusive(true);
        range.setLower("a");
        range.setUpper("z");
        assertEquals(
                "ts>='a'",
                templateEvaluator.evaluate(
                        "@if{range.getLower() != null}ts@if{range.isLowerInclusive()}>=@else{}>@end{}'@{range.getLower()}'@end{}",
                        variables
                )
        );

        // case 2 lower inclusive
        range.setLowerInclusive(false);
        range.setLower("a");
        range.setUpper("z");
        assertEquals(
                "ts>'a'",
                templateEvaluator.evaluate(
                        "@if{range.getLower() != null}ts@if{range.isLowerInclusive()}>=@else{}>@end{}'@{range.getLower()}'@end{}",
                        variables
                )
        );

        // case 3 lower missing
        range.setLowerInclusive(false);
        range.setLower(null);
        range.setUpper("z");
        assertEquals(
                "",
                templateEvaluator.evaluate(
                        "@if{range.getLower() != null}ts@if{range.isLowerInclusive()}>=@else{}>@end{}'@{range.getLower()}'@end{}",
                        variables
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
                MapBuilder.<String, Map<String, String>>hashMap()
                        .put("CHOICE", new HashMap<>())
                        .build()
        );
        namedElements = evaluator.evaluate(selection, new HashMap<String, Object>());
        assertEquals(0, namedElements.size());

        // case 6
        selection.setSelector("@if{type=='brewery'}brewery@else{}beer@end{}");
        selection.setChoices(
                MapBuilder.<String, Map<String, String>>hashMap()
                        .put(
                                "brewery",
                                MapBuilder.<String, String>hashMap()
                                        .put("TYPE", "`type`='brewery'")
                                        .put("COUNTRY", "@if{country != null && country != ''} AND `country` like '%@{country}%'@end{}")
                                        .put("STATE", "@if{state != null && state != ''} AND `state` like '%@{state}%'@end{}")
                                        .put("ZIPCODE", "@if{zipcode != null && zipcode != ''} AND `code` like '%@{zipcode}%'@end{}")
                                        .put("NAME", "@if{name != null && name != ''} AND `name` like '%@{name}%'@end{}")
                                        .build()
                        )
                        .put(
                                "beer",
                                MapBuilder.<String, String>hashMap()
                                        .put("TYPE", "`type`='beer'")
                                        .put("COUNTRY", "@if{country != null && country != ''} AND `country` like '%@{country}%'@end{}")
                                        .put("NAME", "@if{name != null && name != ''} AND `name` like '%@{name}%'@end{}")
                                        .build()
                        )
                        .build()
        );

        Map<String, Object> variables = new HashMap<>(
                MapBuilder.<String, Object>hashMap()
                        .put("type", "brewery")
                        .put("fetchCount", 100)
                        .put("skipCount", 100)
                        .put("country", null)
                        .put("state", null)
                        .put("zipcode", null)
                        .put("name", null)
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
        singleSelection.setChoices(MapBuilder.<String, String>hashMap().put("A", "Not selected").build());
        assertNull(evaluator.evaluate(singleSelection, new HashMap<String, Object>()));

        // case 6
        singleSelection.setSelector("@if{type=='brewery'}ONE@else{}TWO@end{}");
        singleSelection.setChoices(
                MapBuilder.<String, String>hashMap()
                        .put("ONE", "Brewery is top notch!")
                        .put("TWO", "Beer tastes great!")
                        .build()
        );
        assertEquals("Beer tastes great!", evaluator.evaluate(singleSelection, MapBuilder.<String, Object>hashMap().put("type", "beer").build()));
        assertEquals("Brewery is top notch!", evaluator.evaluate(singleSelection, MapBuilder.<String, Object>hashMap().put("type", "brewery").build()));

        Map<String, SingleSelection> namedSelections = new HashMap<>();

        SingleSelection playerAwardSelection = new SingleSelection();
        playerAwardSelection.setSelector("@if{ranking==1}ONE@elseif{ranking==2}TWO@else{}OTHER@end{}");
        playerAwardSelection.setChoices(
                MapBuilder.<String, String>hashMap()
                        .put("ONE", "Ballon d'or of @{year}")
                        .put("TWO", "Runner up of @{year}")
                        .put("OTHER", "Keep up the good work!")
                        .build()
        );
        namedSelections.put("PlayerAward", playerAwardSelection);

        SingleSelection teamAwardSelection = new SingleSelection();
        teamAwardSelection.setSelector("@if{ranking==1}ONE@elseif{ranking==2}TWO@else{}OTHER@end{}");
        teamAwardSelection.setChoices(
                MapBuilder.<String, String>hashMap()
                        .put("ONE", "Best Club of @{year}")
                        .put("TWO", "Runner up Team of @{year}")
                        .put("OTHER", "Come next year!")
                        .build()
        );
        namedSelections.put("TeamAward", teamAwardSelection);

        Map<String, String> namedChoices = evaluator.evaluate(
                namedSelections,
                MapBuilder.<String, Object>hashMap()
                        .put("ranking", 1)
                        .put("year", 2020)
                        .build()
        );
        assertEquals("Ballon d'or of 2020", namedChoices.get("PlayerAward"));
        assertEquals("Best Club of 2020", namedChoices.get("TeamAward"));

        namedChoices = evaluator.evaluate((Map<String, SingleSelection>) null, new HashMap<String, Object>());
        assertEquals(0, namedChoices.size());

        namedChoices = evaluator.evaluate(new HashMap<>(), new HashMap<>());
        assertEquals(0, namedChoices.size());
    }

    @Test
    public void evaluateMultiSelectionTemplate() {
        MultiSelectionStructuredTemplate multiSelectionStructuredTemplate = new MultiSelectionStructuredTemplate();
        multiSelectionStructuredTemplate.setOptionalVariables(Arrays.asList("country", "state", "zipcode", "name"));
        multiSelectionStructuredTemplate.setTemplate("SELECT `beer-sample`.* FROM `beer-sample` WHERE @{TYPE}@{COUNTRY}@{STATE}@{ZIPCODE}@{NAME} limit @{fetchCount} offset @{skipCount}");
        MultiSelection selection = new MultiSelection();
        selection.setSelector("@if{type=='brewery'}brewery@else{}beer@end{}");
        selection.setChoices(
                MapBuilder.<String, Map<String, String>>hashMap()
                        .put(
                                "brewery",
                                MapBuilder.<String, String>hashMap()
                                        .put("TYPE", "`type`='brewery'")
                                        .put("COUNTRY", "@if{country != null && country != ''} AND `country` like '%@{country}%'@end{}")
                                        .put("STATE", "@if{state != null && state != ''} AND `state` like '%@{state}%'@end{}")
                                        .put("ZIPCODE", "@if{zipcode != null && zipcode != ''} AND `code` like '%@{zipcode}%'@end{}")
                                        .put("NAME", "@if{name != null && name != ''} AND `name` like '%@{name}%'@end{}")
                                        .build()
                        )
                        .put(
                                "beer",
                                MapBuilder.<String, String>hashMap()
                                        .put("TYPE", "`type`='beer'")
                                        .put("COUNTRY", "@if{country != null && country != ''} AND `country` like '%@{country}%'@end{}")
                                        .put("NAME", "@if{name != null && name != ''} AND `name` like '%@{name}%'@end{}")
                                        .build()
                        )
                        .build()
        );
        multiSelectionStructuredTemplate.setElements(selection);

        StructuredTemplateEvaluator evaluator = new StructuredTemplateEvaluator(CompiledTemplates.REGISTRY.get());

        // case 1
        Map<String, Object> variables = MapBuilder.<String, Object>hashMap()
                .put("type", "brewery")
                .put("fetchCount", 100)
                .put("skipCount", 100)
                .put("name", null)
                .buildModifiable();

        String query = evaluator.evaluate(multiSelectionStructuredTemplate, variables);
        assertEquals("SELECT `beer-sample`.* FROM `beer-sample` WHERE `type`='brewery' limit 100 offset 100", query);

        // case 2
        variables = MapBuilder.<String, Object>hashMap()
                .put("type", "brewery")
                .put("fetchCount", 100)
                .put("skipCount", 100)
                .put("country", "US")
                .buildModifiable();

        query = evaluator.evaluate(multiSelectionStructuredTemplate, variables);
        assertEquals("SELECT `beer-sample`.* FROM `beer-sample` WHERE `type`='brewery' AND `country` like '%US%' limit 100 offset 100", query);

    }


}