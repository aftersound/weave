package io.aftersound.weave.utils;

import java.util.*;

public class TreeNode {

    private String data;
    private TreeNode parent;
    private List<TreeNode> children;

    private Map<String, String> attributes;

    private TreeNode(String data) {
        this.data = data;
    }

    public static TreeNode treeNodeExpectingChild(String data) {
        TreeNode treeNode = new TreeNode(data);
        treeNode.children = new LinkedList<>();
        return treeNode;
    }

    public static TreeNode treeNodeExpectingNoChild(String data) {
        TreeNode treeNode = new TreeNode(data);
        return treeNode;
    }

    public static TreeNode from(String expr) throws ExprTreeParsingException {
        return TextualExprTreeParser.parse(expr);
    }

    public String getData() {
        return data;
    }

    public void addChild(TreeNode child) {
        if (this.children == null) {
            this.children = new LinkedList<>();
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

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public TreeNode getChildAt(int index) {
        if (children == null || children.isEmpty()) {
            return null;
        }
        if (index >= 0 && index < children.size()) {
            return children.get(index);
        }
        return null;
    }

    public int getChildrenCount() {
        return children != null ? children.size() : 0;
    }

    public List<TreeNode> getChildren(int fromIndex) {
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (children == null || children.isEmpty() || fromIndex >= children.size()) {
            return Collections.emptyList();
        }
        return children.subList(fromIndex, children.size());
    }

    public List<TreeNode> getChildren(int fromIndex, int toIndex) {
        if (children == null || children.isEmpty() || fromIndex >= children.size() || toIndex < fromIndex) {
            return Collections.emptyList();
        }

        int toIndexAdjusted = toIndex < children.size() ? (toIndex + 1) : children.size();
        return children.subList(fromIndex, toIndexAdjusted);
    }

    public Map<String, TreeNode> getChildrenWithDataEntries(String... dataEntries) {
        Map<String, TreeNode> m = new HashMap<>();

        if (children == null || children.isEmpty()) {
            return m;
        }

        Set<String> dataEntrySet = new HashSet<>();
        for (String dataEntry : dataEntries) {
            dataEntrySet.add(dataEntry);
        }

        for (TreeNode child : children) {
            if (dataEntrySet.contains(child.getData())) {
                m.put(child.getData(), child);
            }
        }

        return m;
    }

    public TreeNode getChildWithData(String data) {
        if (children == null || children.isEmpty()) {
            return null;
        }

        TreeNode target = null;
        for (TreeNode child : children) {
            if (data.equals(child.getData())) {
                target = child;
            }
        }

        return target;
    }

    public List<String> getDataOfChildren() {
        if (children == null) {
            return null;
        }
        if (children.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> data = new ArrayList<>(children.size());
        for (TreeNode child : children) {
            data.add(child.getData());
        }
        return data;
    }

    public List<String> getDataOfChildren(int fromIndex) {
        if (children == null || children.size() - 1 < fromIndex) {
            return Collections.emptyList();
        }

        List<String> data = new ArrayList<>(children.size() - fromIndex);
        for (int i = fromIndex; i < children.size(); i++) {
            data.add(children.get(i).getData());
        }
        return data;
    }

    public String getDataOfChildAt(int index) {
        if (children == null || children.isEmpty()) {
            return null;
        }
        if (index >= 0 && index < children.size()) {
            return children.get(index).getData();
        }
        return null;
    }

    /**
     * Convert to textural expression
     * @return
     *          a textual expression
     */
    public String toExpr() {
        String attrStr = "";
        if (attributes != null && attributes.size() > 0) {
            StringBuilder sb = new StringBuilder();
            boolean isFirst = true;
            for (Map.Entry<String, String> e : attributes.entrySet()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(',');
                }
                sb.append(e.getKey()).append(':').append(e.getValue());
            }
            attrStr = new StringBuilder().append('[').append(sb).append(']').toString();
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
