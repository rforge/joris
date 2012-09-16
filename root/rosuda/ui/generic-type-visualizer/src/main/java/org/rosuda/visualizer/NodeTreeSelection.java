package org.rosuda.visualizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rosuda.type.NodePath;

public interface NodeTreeSelection {

    List<NodePath> getSelectedPaths();

    public static class Impl implements NodeTreeSelection {

	private final List<NodePath> paths;

	public Impl(final List<NodePath> paths) {
	    this.paths = paths == null ? new ArrayList<NodePath>() : Collections.unmodifiableList(paths);
	}

	@Override
	public List<NodePath> getSelectedPaths() {
	    return paths;
	}

    }
}
