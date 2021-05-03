package io.aftersound.weave.common.parser;

import io.aftersound.weave.utils.ExprTreeParsingException;
import io.aftersound.weave.utils.TextualExprTreeParser;
import io.aftersound.weave.utils.TreeNode;

public class TreeNodeParser extends FirstRawKeyValueParser<TreeNode> {

    @Override
    protected TreeNode _parse(String rawValue) {
        try {
            return TextualExprTreeParser.parse(rawValue);
        } catch (ExprTreeParsingException e) {
            return null;
        }
    }

}
