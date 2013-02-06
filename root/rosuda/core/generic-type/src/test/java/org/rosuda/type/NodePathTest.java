package org.rosuda.type;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import javax.swing.tree.TreePath;

import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class NodePathTest {

    @Test
    public void nodePathCanParseRootNode() {
	final NodePath path = NodePath.Impl.parse("/root");
	assertThat(path, notNullValue());
    }

    @Test
    public void nodePathCanParsePathFromTreePath() {
	final TreePath treepath = new TreePath(new Object[] { "root", "tmp" });
	final NodePath path = NodePath.Impl.parse(treepath);
	assertNodePathOfRootNext(path);
    }
    
    @Test
    public void nodePathFromTreeCannotContainEmptyString() {
	final TreePath treepath = new TreePath(new Object[] { "", "root", "tmp" });
	final NodePath path = NodePath.Impl.parse(treepath);
	assertNodePathOfRootNext(path);
    }
    
    @Test
    public void nodePathFromTreeCannotContainNull() {
	try {
	    final TreePath treepath = new TreePath(new Object[] { null, "root", "tmp" });
	    final NodePath path = NodePath.Impl.parse(treepath);
	    assertNodePathOfRootNext(path);
	} catch (final Exception x) {
	    LogFactory.getLog(NodePathTest.class).warn("current JDK does not support NULL in treepath", x);
	}
    }

    @Test
    public void nodePathCanParsePathFromString() {
	final NodePath path = NodePath.Impl.parse("/root/tmp");
	assertNodePathOfRootNext(path);
    }

    @Test
    public void nodePathCanBeFormattedToTheSameStringThatWasInitiallyParsed() {
	assertThat(NodePath.Impl.parse("/root").toString(), equalTo("/root"));
	assertThat(NodePath.Impl.parse("/root/tmp").toString(), equalTo("/root/tmp"));
	assertThat(NodePath.Impl.parse("/root/tmp[2]/x[3]/c[0]").toString(), equalTo("/root/tmp[2]/x[3]/c"));
    }
    
    // -- helper
    
    private void assertNodePathOfRootNext(final NodePath path) {
	assertThat("path is a nullValue : "+path, path, notNullValue());
	assertThat(path.hasNext(), equalTo(true));
	assertThat(path.getId().getName(), equalTo("root"));
	final NodePath next = path.next();
	assertThat(next.hasNext(), equalTo(false));
	assertThat(next.getId().getName(), equalTo("tmp"));
    }
}
