package org.rosuda.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.tree.TreePath;

import org.apache.commons.logging.LogFactory;

public interface NodePath {

    public interface Identifier {
	String getName();

	int getIndex();

	public static class Impl implements Identifier {

	    private final String name;
	    private final int idx;

	    public Impl(final String name) {
		this(name, 0);
	    }

	    public Impl(final String name, final int idx) {
		this.name = name;
		this.idx = idx;
	    }

	    public String getName() {
		return name;
	    }

	    public int getIndex() {
		return idx;
	    }

	}
    }

    Identifier getId();

    NodePath next();

    boolean hasNext();

    public static class Impl implements NodePath {

	private final Identifier id;
	private final NodePath next;

	public Impl(final Identifier id) {
	    this(id, null);
	}

	public Impl(final Identifier id, final NodePath next) {
	    this.id = id;
	    this.next = next;
	}

	public NodePath next() {
	    return next;
	}

	public boolean hasNext() {
	    return next != null;
	}

	public Identifier getId() {
	    return id;
	}

	public static NodePath parse(final String string) {
	    final StringTokenizer idTokenizer = new StringTokenizer(string, TreeUtil.NODE_SEPARATOR, false);
	    final List<String> names = new ArrayList<String>();
	    final List<Integer> numbers = new ArrayList<Integer>();
	    while (idTokenizer.hasMoreTokens()) {
		final StringTokenizer numberTokenizer = new StringTokenizer(idTokenizer.nextToken(), "[]", false);
		if (!numberTokenizer.hasMoreTokens())
		    throw new IllegalArgumentException("cannot parse \"" + string + "\"");
		final String name = numberTokenizer.nextToken();
		names.add(name);
		int num = 0;
		if (numberTokenizer.hasMoreTokens()) {
		    num = Integer.parseInt(numberTokenizer.nextToken());
		}
		numbers.add(num);
	    }
	    return generateStack(names, numbers);
	}

	@Override
	public String toString() {
	    final StringBuilder builder = new StringBuilder();
	    NodePath path = this;
	    do {
		appendToPathAsString(builder, path);
		path = path.next();
	    } while (path != null);
	    return builder.toString();
	}

	private void appendToPathAsString(final StringBuilder builder, NodePath path) {
	    final Identifier identifier = path.getId();
	    builder.append("/").append(identifier.getName());
	    if (identifier.getIndex() != 0) {
		builder.append("[").append(identifier.getIndex()).append("]");
	    }
	}

	public static NodePath parse(final TreePath treePath) {
	    LogFactory.getLog(NodePath.Impl.class).warn("parsing TreePath (length="+treePath.getPathCount()+")" + treePath+" @"+Thread.currentThread().getStackTrace()[2]);
	    final List<String> names = new ArrayList<String>();
	    final List<Integer> numbers = new ArrayList<Integer>();

	    final List<Object> parts = Arrays.asList(treePath.getPath());
	    for (int i = 0; i < parts.size(); i++) {
		final Object ithPart = parts.get(i);
		if (isValid(ithPart)) {
		    final String name = ithPart.toString();
		    names.add(name);
		    numbers.add(0);
		}
	    }
	    return generateStack(names, numbers);
	}

	private static boolean isValid(Object ithPart) {
	    return ithPart != null && ithPart.toString().length() > 0;
	}

	// -- helper
	private static NodePath generateStack(final List<String> names, final List<Integer> numbers) {
	    LogFactory.getLog(NodePath.Impl.class).warn("generateStack(" + names + "," + numbers + ") .. startCount = "+ (names.size() - 1));
	    NodePath path = null;
	    for (int i = names.size() - 1; i >= 0; i--) {
		LogFactory.getLog(NodePath.Impl.class).warn("generateStack pushing(" + names.get(i) + "," + numbers.get(i) + "," + path + ") @"+i);
		final NodePath nextPath = new NodePath.Impl(new Identifier.Impl(names.get(i), numbers.get(i)), path);
		path = nextPath;
		LogFactory.getLog(NodePath.Impl.class).warn("generateStack path = "+nextPath);
	    }
	    return path;
	}
    }
}
