package org.rosuda.util.db;

import java.io.IOException;
import java.sql.Connection;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;

public class DatabaseStarter implements ProcessStarter<Connection>{

	private final String processStartScript;
	private final Log log = LogFactory.getLog(DatabaseStarter.class);
	private Process process;
	final RunStateHolder<Connection> runStateHolder;
	
	public DatabaseStarter(final RunStateHolder<Connection> runStateHolder, final String processStartScript) {
		this.processStartScript = processStartScript;
		this.runStateHolder = runStateHolder;
	}
	
	@Override
	public void start() {
		final Runtime runtime = Runtime.getRuntime();
		try {
			final String classPath = System.getProperty("java.class.path");
			final String startScript = MessageFormat.format(processStartScript, classPath);
			log.info("starting database by command:\r\n>"+startScript);
			this.process = runtime.exec(startScript);
			//TODO wait for log;
			this.runStateHolder.setRunState(RUNSTATE.RUNNING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			process.destroy();
		} catch (final Throwable tx) {}
		super.finalize();
	}
}
