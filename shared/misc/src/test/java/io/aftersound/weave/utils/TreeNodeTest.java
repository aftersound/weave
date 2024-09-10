package io.aftersound.weave.utils;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.*;

public class TreeNodeTest {

    @Test
    public void treeNodeExpectingChild() {
        assertNotNull(TreeNode.treeNodeExpectingChild("root"));
        assertNotNull(TreeNode.treeNodeExpectingChild("root").getChildren());
        assertTrue(TreeNode.treeNodeExpectingChild("root").getChildren().isEmpty());
    }

    @Test
    public void treeNodeExpectingNoChild() {
        assertNotNull(TreeNode.treeNodeExpectingNoChild("root"));
        assertNull(TreeNode.treeNodeExpectingNoChild("root").getChildren());
    }

    @Test
    public void from() throws Exception {
        TreeNode tn = TreeNode.from("root(c1(),c2())");
        assertNotNull(tn);
        assertNotNull(tn.getChildAt(0));
        assertNotNull(tn.getChildAt(1));
        assertNull(tn.getChildAt(2));

        assertThrows(
                ExprTreeParsingException.class,
                () -> TreeNode.from("root(")
        );
    }

    @Test
    public void getData() {
        TreeNode tn = TreeNode.treeNodeExpectingChild("root");
        assertEquals("root", tn.getData());

        tn = TreeNode.treeNodeExpectingNoChild("root");
        assertEquals("root", tn.getData());
    }

    @Test
    public void addChild() {
        TreeNode tn = TreeNode.treeNodeExpectingChild("root");
        tn.addChild(TreeNode.treeNodeExpectingNoChild("c1"));
        tn.addChild(TreeNode.treeNodeExpectingNoChild("c2"));
        assertNotNull(tn.getChildAt(0));
        assertNotNull(tn.getChildAt(1));
        assertNull(tn.getChildAt(2));

        tn = TreeNode.treeNodeExpectingNoChild("root");
        tn.addChild(TreeNode.treeNodeExpectingNoChild("c1"));
        assertNotNull(tn.getChildAt(0));
    }

    @Test
    public void getParent() {
        TreeNode tn = TreeNode.treeNodeExpectingChild("root");
        tn.addChild(TreeNode.treeNodeExpectingNoChild("c1"));
        tn.addChild(TreeNode.treeNodeExpectingNoChild("c2"));
        assertNotNull(tn.getChildAt(0));
        assertNotNull(tn.getChildAt(1));
        assertNotNull(tn.getChildAt(0).getParent());
        assertSame(tn.getChildAt(0).getParent(), tn.getChildAt(1).getParent());
    }

    @Test
    public void getChildren() {
        TreeNode tn = TreeNode.treeNodeExpectingChild("root");
        tn.addChild(TreeNode.treeNodeExpectingNoChild("c1"));
        tn.addChild(TreeNode.treeNodeExpectingNoChild("c2"));
        assertNotNull(tn.getChildren());
    }

    @Test
    public void getNodeLevel() {
        TreeNode tn = TreeNode.treeNodeExpectingChild("root");
        tn.addChild(TreeNode.treeNodeExpectingNoChild("c1"));
        tn.addChild(TreeNode.treeNodeExpectingNoChild("c2"));
        assertEquals(0, tn.getNodeLevel());
        assertEquals(1, tn.getChildAt(0).getNodeLevel());
        assertEquals(1, tn.getChildAt(1).getNodeLevel());
    }

    @Test
    public void setAttributes() {
        TreeNode tn = TreeNode.treeNodeExpectingChild("MAP:GET");
        tn.addChild(TreeNode.treeNodeExpectingNoChild("firstName"));
        tn.setAttributes(MapBuilder.hashMap().put("ON", "TARGET").build());
    }

    @Test
    public void getAttributes() {
        TreeNode tn = TreeNode.treeNodeExpectingChild("MAP:GET");
        tn.addChild(TreeNode.treeNodeExpectingNoChild("firstName"));
        tn.setAttributes(MapBuilder.hashMap().put("ON", "TARGET").build());
        assertNotNull(tn.getAttributes());
        assertEquals("TARGET", tn.getAttributes().get("ON"));
    }

    @Test
    public void getChildAt() throws Exception {
        TreeNode tn = TreeNode.from("root(c1(),c2())");
        assertNotNull(tn.getChildAt(0));
        assertNotNull(tn.getChildAt(1));
        assertNull(tn.getChildAt(2));

        tn = TreeNode.treeNodeExpectingNoChild("root");
        assertNull(tn.getChildAt(-1));
        assertNull(tn.getChildAt(0));
        assertNull(tn.getChildAt(1));
    }

    @Test
    public void getChildrenCount() throws Exception {
        TreeNode tn = TreeNode.from("root(c1(),c2())");
        assertEquals(2, tn.getChildrenCount());

        tn = TreeNode.treeNodeExpectingNoChild("root");
        assertEquals(0, tn.getChildrenCount());
    }

    @Test
    public void testGetChildren() throws Exception {
        TreeNode tn = TreeNode.from("root(c1(),c2(),c3())");

        // getChildren()
        assertNotNull(tn.getChildren());

        // getChildren(int fromIndex);
        for (int i = -50; i < 50; i++) {
            assertNotNull(tn.getChildren(i));
        }
        assertEquals(3, tn.getChildren(-1).size());
        assertEquals(3, tn.getChildren(0).size());
        assertEquals(2, tn.getChildren(1).size());
        assertEquals(1, tn.getChildren(2).size());
        for (int i = 3; i < 100; i++) {
            assertEquals(0, tn.getChildren(i).size());
        }

        // getChildren(int fromIndex, int toIndex)
        tn = TreeNode.treeNodeExpectingNoChild("root");
        assertEquals(0, tn.getChildren(0,0).size());
        tn = TreeNode.treeNodeExpectingChild("root");
        assertEquals(0, tn.getChildren(0,0).size());
        tn = TreeNode.from("root(c1(),c2(),c3())");
        assertEquals(0, tn.getChildren(2,0).size());
        assertEquals(3, tn.getChildren(0, 2).size());
        assertEquals(2, tn.getChildren(1, 2).size());
        assertEquals(1, tn.getChildren(2, 2).size());
    }

    @Test
    public void getChildrenWithDataEntries() throws Exception {
        TreeNode tn = TreeNode.from("root(c1(),c2(),c3())");

        Map<String, TreeNode> m = tn.getChildrenWithDataEntries();
        assertNotNull(m);
        assertNull(m.get("c1"));
        assertNull(m.get("c2"));
        assertNull(m.get("c3"));

        m = tn.getChildrenWithDataEntries("c2", "c3");
        assertNotNull(m);
        assertNull(m.get("c1"));
        assertNotNull(m.get("c2"));
        assertNotNull(m.get("c3"));
        assertNull(m.get("c4"));
    }

    @Test
    public void getChildWithData() throws Exception {
        TreeNode tn = TreeNode.from("root(c1(),c2(),c3())");
        assertNotNull(tn.getChildWithData("c1"));
        assertNotNull(tn.getChildWithData("c2"));
        assertNotNull(tn.getChildWithData("c3"));
        assertNull(tn.getChildWithData("c4"));

        tn = TreeNode.treeNodeExpectingNoChild("root");
        assertNull(tn.getChildWithData("c1"));
        assertNull(tn.getChildWithData("c2"));
        assertNull(tn.getChildWithData("c3"));

        tn = TreeNode.treeNodeExpectingChild("root");
        assertNull(tn.getChildWithData("c1"));
        assertNull(tn.getChildWithData("c2"));
        assertNull(tn.getChildWithData("c3"));
    }

    @Test
    public void getDataOfChildren() throws Exception {
        TreeNode tn = TreeNode.treeNodeExpectingNoChild("root");
        List<String> dl = tn.getDataOfChildren();
        assertNull(dl);

        tn = TreeNode.treeNodeExpectingChild("root");
        dl = tn.getDataOfChildren();
        assertNotNull(dl);
        assertEquals(0, dl.size());

        tn = TreeNode.from("root(c1(),c2(),c3())");
        dl = tn.getDataOfChildren();
        assertNotNull(dl);
        assertEquals("c1", dl.get(0));
        assertEquals("c2", dl.get(1));
        assertEquals("c3", dl.get(2));
    }

    @Test
    public void testGetDataOfChildren() throws Exception {
        TreeNode tn = TreeNode.treeNodeExpectingNoChild("root");
        List<String> dl = tn.getDataOfChildren(0);
        assertNotNull(dl);
        assertEquals(0, dl.size());

        tn = TreeNode.treeNodeExpectingChild("root");
        dl = tn.getDataOfChildren(0);
        assertNotNull(dl);
        assertEquals(0, dl.size());

        tn = TreeNode.from("root(c1(),c2(),c3())");
        dl = tn.getDataOfChildren(1);
        assertNotNull(dl);
        assertEquals(2, dl.size());
        assertEquals("c2", dl.get(0));
        assertEquals("c3", dl.get(1));

        dl = tn.getDataOfChildren(-1);
        assertNotNull(dl);
        assertEquals(3, dl.size());
        assertEquals("c1", dl.get(0));
        assertEquals("c2", dl.get(1));
        assertEquals("c3", dl.get(2));

        dl = tn.getDataOfChildren(3);
        assertEquals(0, dl.size());


    }

    @Test
    public void getDataOfChildAt() throws Exception {
        TreeNode tn = TreeNode.from("root(c1(),c2(),c3())");
        assertEquals("c1", tn.getDataOfChildAt(0));
        assertEquals("c2", tn.getDataOfChildAt(1));
        assertEquals("c3", tn.getDataOfChildAt(2));
        assertNull(tn.getDataOfChildAt(3));
        assertNull(tn.getDataOfChildAt(4));

        tn = TreeNode.treeNodeExpectingNoChild("root");
        assertNull(tn.getDataOfChildAt(-1));
        assertNull(tn.getDataOfChildAt(0));
        assertNull(tn.getDataOfChildAt(1));

        tn = TreeNode.treeNodeExpectingChild("root");
        assertNull(tn.getDataOfChildAt(-1));
        assertNull(tn.getDataOfChildAt(0));
        assertNull(tn.getDataOfChildAt(1));
    }

    @Test
    public void toExpr() throws Exception {
        TreeNode tn = TreeNode.from("root(c1(),c2(),c3())[a:b,c:d]");
        assertEquals("root(c1(),c2(),c3())[a:b,c:d]", tn.toExpr());

        tn = TreeNode.treeNodeExpectingNoChild("root");
        assertEquals("root", tn.toExpr());

        tn = TreeNode.from("root()");
        assertEquals("root()", tn.toExpr());
    }

    @Test
    public void testToString() throws Exception {
        TreeNode tn = TreeNode.from("root(c1(),c2(),c3())[a:b,c:d]");
        assertEquals("root(c1(),c2(),c3())[a:b,c:d]", tn.toString());
    }

    @Test
    public void translate() throws Exception {
        TreeNode tn = TreeNode.from("EQ(Color,Blue)");
        String expr = tn.translate(treeNode -> tn.toExpr());
        assertEquals("EQ(Color,Blue)", expr);
    }
}