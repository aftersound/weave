package io.aftersound.weave.utils;

/**
 * A parser that parses textual expression in following format into a {@link TreeNode}
 *  List(ASCII,false)
 *  Tuple(VARCHAR,TEXT,Set(BOOLEAN,true)[a:b,c:d],Tuple(BLOB,VARCHAR,INT64,INT32,FLOAT))
 *  AND(MATCH(keyword,TITLE),OR(GT(Size,6)))
 */
public class TextualExprTreeParser {

    private static final char LEFT_PARENTHESIS = '(';
    private static final char RIGHT_PARENTHESIS = ')';
    private static final char COMMA = ',';
    private static final char TAB = '\t';
    private static final char NEW_LINE = '\n';
    private static final char LEFT_BRACKET = '[';
    private static final char RIGHT_BRACKET = ']';

    /**
     * Parse textual expression into {@link TreeNode}
     * @param textualExpression
     *          textual expression which conforms to acceptable format and structure
     * @return
     *          a {@link TreeNode} which retains the same logical tree structure
     * @throws ExprTreeParsingException
     *          exception on any syntax error
     */
    public static TreeNode parse(String textualExpression) throws ExprTreeParsingException {

        if (textualExpression == null || textualExpression.isEmpty()) {
            throw new ExprTreeParsingException("Given textual expression is null or empty");
        }

        TreeNode rootNode = null;
        TreeNode cursorNode = null;

        ParsingTracker parsingTracker = new ParsingTracker();

        StringBuilder treeNodeDataBuffer = new StringBuilder();
        StringBuilder treeNodeAttrDataBuffer = new StringBuilder();

        for (int index = 0; index < textualExpression.length(); index++) {
            char c = textualExpression.charAt(index);

            switch (c) {

                case LEFT_PARENTHESIS: {
                    if (parsingTracker.getBracketPairCount() > 0) {
                        throw new ExprTreeParsingException("( is not allowed to be nested in [ ]");
                    }

                    if (treeNodeDataBuffer.length() == 0) {
                        throw new ExprTreeParsingException("Data is missing before (");
                    }

                    if (parsingTracker.expectAny(COMMA, RIGHT_PARENTHESIS)) {
                        throw new ExprTreeParsingException(", or ) is missing");
                    }

                    parsingTracker.increaseParenthesisPairCount();

                    if (cursorNode == null) {
                        cursorNode = TreeNode.treeNodeExpectingChild(treeNodeDataBuffer.toString());
                        rootNode = cursorNode;
                    } else {
                        TreeNode child = TreeNode.treeNodeExpectingChild(treeNodeDataBuffer.toString());
                        cursorNode.addChild(child);
                        cursorNode = child;
                    }

                    treeNodeDataBuffer = new StringBuilder();
                }
                break;

                case COMMA: {
                    if (parsingTracker.getBracketPairCount() > 0) {
                        treeNodeAttrDataBuffer.append(c);
                    } else {

                        if (cursorNode == null) {
                            throw new ExprTreeParsingException("Current TreeNode is null when , is encountered");
                        }

                        if (treeNodeDataBuffer.length() == 0 && (cursorNode.getChildren() == null || cursorNode.getChildren().isEmpty())) {
                            throw new ExprTreeParsingException("Data is missing before ,");
                        }

                        if (parsingTracker.expectAny(COMMA, RIGHT_PARENTHESIS)) {
                            parsingTracker.clearExpected();
                        }

                        if (treeNodeDataBuffer.length() > 0) {
                            TreeNode child = TreeNode.treeNodeExpectingNoChild(treeNodeDataBuffer.toString());
                            cursorNode.addChild(child);

                            treeNodeDataBuffer = new StringBuilder();
                        }

                        if (treeNodeAttrDataBuffer.length() > 0) {
                            TreeNode treeNodeWithAttribute = cursorNode.getChildren().get(cursorNode.getChildren().size() - 1);
                            treeNodeWithAttribute.setAttributes(treeNodeAttrDataBuffer.toString());
                            treeNodeAttrDataBuffer = new StringBuilder();
                        }
                    }
                }
                break;

                case RIGHT_PARENTHESIS: {
                    if (parsingTracker.getBracketPairCount() > 0) {
                        throw new ExprTreeParsingException(") is not allowed to be nested in [ ]");
                    }

                    if (cursorNode == null) {
                        throw new ExprTreeParsingException("Current TreeNode is null when ) is encountered");
                    }

                    if (parsingTracker.decreaseParenthesisPairCount() < 0) {
                        throw new ExprTreeParsingException("Parenthesises are not paired properly");
                    }

                    if (parsingTracker.expectAny(COMMA, RIGHT_PARENTHESIS)) {
                        parsingTracker.clearExpected();
                    }

                    if (treeNodeDataBuffer.length() > 0) {
                        TreeNode child = TreeNode.treeNodeExpectingNoChild(treeNodeDataBuffer.toString());
                        cursorNode.addChild(child);
                    }

                    if (treeNodeAttrDataBuffer.length() > 0) {
                        TreeNode treeNodeWithAttribute = cursorNode.getChildren().get(cursorNode.getChildren().size() - 1);
                        treeNodeWithAttribute.setAttributes(treeNodeAttrDataBuffer.toString());
                        treeNodeAttrDataBuffer = new StringBuilder();
                    }

                    cursorNode = cursorNode.getParent();

                    treeNodeDataBuffer = new StringBuilder();

                    parsingTracker.setExpectedChars(COMMA, RIGHT_PARENTHESIS);
                }
                break;

                case TAB:
                case NEW_LINE:
                    // skip intentionally
                    break;

                case LEFT_BRACKET: {
                    if (parsingTracker.increaseBracketPairCount() > 1) {
                        throw new ExprTreeParsingException("[ is allowed to be nested in [ ]");
                    }
                }
                break;

                case RIGHT_BRACKET: {
                    if (parsingTracker.decreaseBracketPairCount() < 0) {
                        throw new ExprTreeParsingException("Brackets are not paired properly");
                    }
                }
                break;

                default: {
                    if (parsingTracker.getBracketPairCount() > 0) {
                        treeNodeAttrDataBuffer.append(c);
                    } else {
                        if (parsingTracker.expectAny(COMMA, RIGHT_PARENTHESIS)) {
                            throw new ExprTreeParsingException(", or ) is missing");
                        }

                        treeNodeDataBuffer.append(c);
                    }
                }

            }
        }

        if (parsingTracker.getParenthesisPairCount() != 0) {
            throw new ExprTreeParsingException("Parenthesises are not paired properly");
        }

        if (parsingTracker.getBracketPairCount() != 0) {
            throw new ExprTreeParsingException("Bracket are not paired properly");
        }

        if (rootNode == null && treeNodeDataBuffer.length() > 0) {
            rootNode = TreeNode.treeNodeExpectingNoChild(treeNodeDataBuffer.toString());
        }

        if (rootNode != null && treeNodeAttrDataBuffer.length() > 0) {
            rootNode.setAttributes(treeNodeAttrDataBuffer.toString());
        }

        return rootNode;
    }

    static class ParsingTracker {

        private char[] expectedChars = new char[0];
        private int parentheisPairCount = 0;
        private int bracketPairCount = 0;

        boolean expectAny(char... chars) {
            if (expectedChars.length == 0) {
                return false;
            }

            for (char c : chars) {
                for (char expected : expectedChars) {
                    if (c == expected) {
                        return true;
                    }
                }
            }

            return false;
        }

        public void setExpectedChars(char... expectedChars) {
            this.expectedChars = expectedChars;
        }

        public void clearExpected() {
            expectedChars = new char[0];
        }

        public int increaseParenthesisPairCount() {
            parentheisPairCount++;
            return parentheisPairCount;
        }

        public int decreaseParenthesisPairCount() {
            parentheisPairCount--;
            return parentheisPairCount;
        }

        public int getParenthesisPairCount() {
            return parentheisPairCount;
        }

        public int increaseBracketPairCount() {
            bracketPairCount++;
            return bracketPairCount;
        }

        public int decreaseBracketPairCount() {
            bracketPairCount--;
            return bracketPairCount;
        }

        public int getBracketPairCount() {
            return bracketPairCount;
        }

    }

}
