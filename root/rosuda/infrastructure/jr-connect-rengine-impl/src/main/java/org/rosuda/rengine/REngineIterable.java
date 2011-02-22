package org.rosuda.rengine;

import java.util.Iterator;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.RList;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRIterable;

class REngineIterable implements IRIterable{

	private final RList delegate;
	
	REngineIterable(final RList list) {
		this.delegate = list;
	}

	public Iterator<IREXP> iterator() {
		return new Iterator<IREXP>() {
			int i = 0;
			public boolean hasNext() {
				return i<delegate.size();
			}

			public IREXP next() {
				final REXP rexp = delegate.at(i++);
				if (rexp == null)
					return null;
				return new REngineREXP(rexp);
			}
			public void remove() {
				i++;
			}
		};
	}

}
