package org.rosuda.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.ITwoWayConnection;
import org.rosuda.mapper.ObjectTransformationHandler;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.type.Node;
import org.rosuda.type.impl.NodeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateNodeModels {

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateNodeModels.class);
	
	private final ITwoWayConnection connection;
	private final ObjectTransformationHandler<Object> handler;

	public CreateNodeModels() {
		this.handler = new IREXPMapper<Object>().createInstance();
		final Properties properties = new Properties();
		connection = REngineConnectionFactory.getInstance().createTwoWayConnection(properties);
	}
	
	public void executeRCommand(final String command) {
		connection.voidEval(command);
	}
	/**
	 * 
	 * @param command a linear model command like lm(x~y,...)
	 * @param target
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 */
	public void writeSummaryAndAICForCommand(final String command, final File target) throws ParserConfigurationException, TransformerException, IOException {
		LOGGER.info(command);
		connection.voidEval("mModel <- "+command+"");
		connection.voidEval("mSummary <- summary(mModel)");
		connection.voidEval("mAIC <- AIC(mModel)");
		
		final IREXP lmREXP = connection.eval("c(mSummary,AIC=mAIC)");
		final Node.Builder<Object> lmNode = new NodeBuilderFactory<Object>().createRoot();
		handler.transform(lmREXP, lmNode);
		final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(target));
		oos.writeObject(lmNode.build());
		oos.close();
		LOGGER.info("created file "+target.getAbsolutePath());
	}

}
