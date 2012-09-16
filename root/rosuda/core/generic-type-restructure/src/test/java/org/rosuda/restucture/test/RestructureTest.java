package org.rosuda.restucture.test;

import junit.framework.TestCase;

import org.rosuda.restructure.StructureHandler;
import org.rosuda.restructure.StructureHandlerImpl;
import org.rosuda.type.Node;
import org.rosuda.type.NodePath;
import org.rosuda.type.impl.NodeBuilderFactory;

public class RestructureTest extends TestCase {

    Node.Builder<Object> root;
    Node.Builder<Object> lvl1;
    Node.Builder<Object> lvl2;

    StructureHandler<Object> handler;

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	root = new NodeBuilderFactory<Object>().createRoot();
	lvl1 = root.createChild("level1");
	root.add(lvl1);
	root.add(root.createChild("level1_0"));
	root.add(root.createChild("level1"));

	lvl2 = root.createChild("level2");
	lvl1.add(lvl2);

	handler = new StructureHandlerImpl<Object>();
    }

    public void testIdentify() {
	assertNotNull(root);
	final NodePath path = NodePath.Impl.parse("/" + Node.ROOTNAME);
	assertNotNull(path);
	final Node.Builder<Object> foundRoot = handler.findNode(root, path);
	assertNotNull(foundRoot);
	assertEquals(root, foundRoot);
	assertNotNull("did not find /root/level1/", handler.findNode(root, "/root/level1"));
	assertNotNull("did not find /root/level1_0/", handler.findNode(root, "/root/level1_0"));
	assertNotNull("did not find /root/level1[0]", handler.findNode(root, "/root/level1[0]"));
	assertNotNull("did not find /root/level1/level2", handler.findNode(root, "/root/level1/level2"));
	assertNotNull("did not find /root/level1[0]/level2", handler.findNode(root, "/root/level1[0]//level2"));
	assertNotNull("did not find /root/level1[1]", handler.findNode(root, "/root/level1[1]"));
	assertNull("did find /root/level1[2]", handler.findNode(root, "/root/level1[2]"));
	assertNotNull("did not find /root[0]", handler.findNode(root, "/root[0]"));
	assertNull("did find /root[1]", handler.findNode(root, "/root[1]"));
	assertNotNull("relative find failed for level2", handler.findNode(lvl1, "/level2"));
    }

    public void testRemove() {
	assertNotNull("did not find /root/level1/level2", handler.findNode(root, "/root/level1/level2"));
	final Node.Builder<Object> removedLvl2 = handler.removeNode(root, "/root/level1/level2");
	assertNotNull("remove failed for '/root/level1/level2'", removedLvl2);
	assertNull("parent still present for '/root/level1/level2'", removedLvl2.getParent());
	assertNull("node still present '/root/level1/level2'", handler.findNode(root, "/root/level1/level2"));
	assertNull("child not rmoved 'level2'", handler.findNode(lvl1, "/level2"));
    }

    public void testRemoveInBetween() {
	assertNotNull("did not find /root/level1/level2", handler.findNode(root, "/root/level1/level2"));
	final Node.Builder<Object> removedLvl1 = handler.removeNode(root, "/root/level1");
	assertNotNull("remove failed for '/root/level1'", removedLvl1);
	assertNull("parent still present for '/root/level1'", removedLvl1.getParent());
	assertNotNull("missing second '/root/level1'", handler.findNode(root, "/root/level1"));
	assertNotNull("child structure persists 'level2'", handler.findNode(lvl1, "/level2"));
	assertNull("path still found '/root/level1/level2'", handler.findNode(root, "/root/level1/level2"));
	assertNull("second node found '/root/level1[1]'", handler.findNode(root, "/root/level1[1]"));
    }

    public void testMoveNode() {
	final Node.Builder<Object> movedNode = handler.moveNode(root, "root/level1/level2", "root/level1[1]");
	assertNotNull(movedNode);
	assertNotSame(lvl1, movedNode.getParent());
	assertEquals(lvl2, movedNode);
	assertNull("path still found '/root/level1/level2'", handler.findNode(root, "/root/level1/level2"));
	assertNotNull("path still found '/root/level1[1]'", handler.findNode(root, "/root/level1[1]"));
	final Node.Builder<Object> lvl1_1 = handler.findNode(root, "/root/level1[1]");
	assertEquals("one child expected", 1, lvl1_1.getChildCount());
	final Node.Builder<Object> lvl1_1_0 = lvl1_1.getChildren().iterator().next();
	assertEquals("level2", lvl1_1_0.getName());
	assertNotNull("child is not as expected", handler.findNode(lvl1_1, "/level2"));
	assertNotNull("missing '/root/level1[1]/level2'", handler.findNode(root, "/root/level1[1]/level2"));
    }
    
   
}
