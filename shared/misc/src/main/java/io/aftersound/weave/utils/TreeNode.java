package io.aftersound.weave.utils;

import java.util.LinkedList;
import java.util.List;

public class TreeNode {

    private String data;
    private TreeNode parent;
    private List<TreeNode> children;

    private String attributes;

    private TreeNode(String data) {
        this.data = data;
    }

    public static TreeNode treeNodeExpectingChild(String data) {
        TreeNode treeNode = new TreeNode(data);
        treeNode.children = new LinkedList<TreeNode>();
        return treeNode;
    }

    public static TreeNode treeNodeExpectingNoChild(String data) {
        TreeNode treeNode = new TreeNode(data);
        return treeNode;
    }

    public String getData() {
        return data;
    }

    public void addChild(TreeNode child) {
        if (this.children == null) {
            this.children = new LinkedList<TreeNode>();
        }

        this.children.add(child);
        child.parent = this;
    }

    public TreeNode getParent() {
        return parent;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public int getNodeLevel() {
        if (parent == null) {
            return 0;
        } else {
            return parent.getNodeLevel() + 1;
        }
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getAttributes() {
        return attributes;
    }

    /**
     * Convert to textural expression
     * @return
     *          a textual expression
     */
    public String toExpr() {
        String attrStr = "";
        if (attributes != null && attributes.length() > 0) {
            attrStr = new StringBuilder().append('[').append(attributes).append(']').toString();
        }

        if (children == null) {
            return new StringBuilder().append(data).append(attrStr).toString();
        } else if (children.isEmpty()) {
            return new StringBuilder(data).append("()").append(attrStr).toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(data);
            sb.append("(");

            boolean isFirstChild = true;
            for (TreeNode child : children) {
                if (isFirstChild) {
                    isFirstChild = false;
                } else {
                    sb.append(",");
                }
                sb.append(child.toExpr());
            }
            sb.append(")");
            sb.append(attrStr);
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        return toExpr();
    }
}
