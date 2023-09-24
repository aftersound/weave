package io.aftersound.weave.utils;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class TextualExprTreeParserTest {

    @Test
    public void parse() throws ExprTreeParsingException {
        TreeNode treeNode = TextualExprTreeParser.parse("DateFormat(Long,yyyy-MM-dd HH:mm:ss.SSS)[a:b,c:d,e]");
        assertNotNull(treeNode);

        assertEquals("DateFormat", treeNode.getData());
        assertEquals(2, treeNode.getChildren().size());
        assertEquals("Long", treeNode.getChildren().get(0).getData());
        assertEquals("yyyy-MM-dd HH:mm:ss.SSS", treeNode.getChildren().get(1).getData());
        assertNotNull(treeNode.getAttributes());
        assertEquals(2, treeNode.getAttributes().size());
        assertEquals("b", treeNode.getAttributes().get("a"));
        assertEquals("d", treeNode.getAttributes().get("c"));
    }

    @Test
    public void parseDataOnly() throws ExprTreeParsingException {
        TreeNode treeNode = TextualExprTreeParser.parse("String");
        assertEquals("String", treeNode.getData());
        assertNull(treeNode.getChildren());
    }

    @Test(expected = ExprTreeParsingException.class)
    public void parseNull() throws ExprTreeParsingException {
        TextualExprTreeParser.parse(null);
    }

    @Test(expected = ExprTreeParsingException.class)
    public void parseEmpty() throws ExprTreeParsingException {
        TextualExprTreeParser.parse("");
    }

    @Test(expected = ExprTreeParsingException.class)
    public void parseMissingBracket() throws ExprTreeParsingException {
        TextualExprTreeParser.parse("DateFormat(Long,yyyy-MM-dd HH:mm:ss.SSS)[a:b");
    }

    @Test(expected = ExprTreeParsingException.class)
    public void parseMissingParenthesis() throws ExprTreeParsingException {
        TextualExprTreeParser.parse("DateFormat(Long,yyyy-MM-dd HH:mm:ss.SSS");
    }

    @Test(expected = ExprTreeParsingException.class)
    public void parseExpectCommaOrLeftParenthesis() throws ExprTreeParsingException {
        TextualExprTreeParser.parse("DateFormat(Long,yyyy-MM-dd HH:mm:ss.SSS)a");
    }

    @Test(expected = ExprTreeParsingException.class)
    public void parseBracketNestedInBracket() throws ExprTreeParsingException {
        TextualExprTreeParser.parse("DateFormat(Long,yyyy-MM-dd HH:mm:ss.SSS)[[a:b]]");
    }

    @Test(expected = ExprTreeParsingException.class)
    public void parseLeftParenthesisNestedInBracket() throws ExprTreeParsingException {
        TextualExprTreeParser.parse("DateFormat(Long,yyyy-MM-dd HH:mm:ss.SSS)[()]");
    }

    @Test(expected = ExprTreeParsingException.class)
    public void parseLeftParenthesisRightAfterAnother() throws ExprTreeParsingException {
        TextualExprTreeParser.parse("DateFormat(()))");
    }
    
}