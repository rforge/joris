package org.rosuda.ui.work;

import org.rosuda.irconnect.IREXP;
import org.rosuda.mapper.filter.ObjectTransformationManager;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.type.Node;
import org.rosuda.type.impl.NodeBuilderFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Function;

@Component
public class WrapIREXPAsNode implements Function<IREXP, Node<IREXP>>{

	private final ObjectTransformationManager<IREXP> filterMgr = new ObjectTransformationManager<IREXP>(
			new NodeBuilderFactory<IREXP>(),
			new IREXPMapper<IREXP>().createInstance());

	@Override
	public Node<IREXP> apply(IREXP input) {
		//TODO inject filter ?
		return filterMgr.transform(input);
	}

}
