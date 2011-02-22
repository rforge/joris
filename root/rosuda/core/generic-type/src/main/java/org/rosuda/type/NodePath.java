package org.rosuda.type;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


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
			return next !=null;
		}

		public Identifier getId() {
			return id;
		}

		public static NodePath parse(final String string) {
			final StringTokenizer idTokenizer = new StringTokenizer(string, TreeUtil.NODE_SEPARATOR,false);
			final List<String> names = new ArrayList<String>();
			final List<Integer> numbers = new ArrayList<Integer>();
			while (idTokenizer.hasMoreTokens()) {
				final StringTokenizer numberTokenizer = new StringTokenizer(idTokenizer.nextToken(), "[]",false);
				if (!numberTokenizer.hasMoreTokens())
					throw new IllegalArgumentException("cannot parse \""+string+"\"");
				final String name = numberTokenizer.nextToken();
				names.add(name);
				int num = 0;
				if (numberTokenizer.hasMoreTokens()) {
					num = Integer.parseInt(numberTokenizer.nextToken());
				}
				numbers.add(num);
			}
			//generate stack:
			NodePath path = null;
			for (int i=names.size()-1; i>=0 ; i--) {
				final NodePath nextPath = new NodePath.Impl(new Identifier.Impl(names.get(i), numbers.get(i)), path);
				path = nextPath;
			}
			return path;
		}
		
	}
}
