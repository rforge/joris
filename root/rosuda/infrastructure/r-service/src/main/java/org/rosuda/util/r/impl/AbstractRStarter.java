package org.rosuda.util.r.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.ProcessStarter;
import org.rosuda.util.process.RUNSTATE;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.RunstateAware;

abstract class AbstractRStarter extends RunstateAware<IRConnection> implements ProcessStarter<IRConnection> {

	public static long MAX_TIMEOUT = 1000;
	protected final Log log = LogFactory.getLog(getClass());
	protected final String R_ARGS = "--vanilla --slave";
	protected final String R_SERVE_ARGS = "--no-save --slave";
	protected final List<File> fileLocations = Collections.unmodifiableList(getRFileLocations());
	private Process process;
	
	public AbstractRStarter(final RunStateHolder<IRConnection> runStateHolder) {
		super(runStateHolder);
		runStateHolder.setRunState(RUNSTATE.STARTING);
	}
	
	private final List<File> getRFileLocations() {
		final List<File> list = new ArrayList<File>();
		//first choice:
		list.add(new File("R"));
		initRFileLocations(list);
		return Collections.unmodifiableList(list);
	}
	
	abstract void initRFileLocations(final List<File> list);
	
	abstract String[] getRuntimeArgs(final String executableRFile);
	
	@Override
	public final void start() {
		for (final File startFile : fileLocations) {
			log.info("starting R process for '"+startFile.getAbsolutePath()+"'");
			try {
				final String[] runtimeArgs = getRuntimeArgs(startFile.getAbsolutePath());
				final StringBuilder sb = new StringBuilder().append("> ");
				for (final String arg: runtimeArgs) {
					sb.append(arg);
					sb.append(" ");
				}
				log.info(sb.toString());
				
				process = Runtime.getRuntime().exec(runtimeArgs);
				//append loggers
				final StreamLogger infoStream = new StreamLogger("RServe>", Mode.INFO, process.getInputStream());
				new Thread(new StreamLogger("RServe>", Mode.ERROR, process.getErrorStream())).start();
				new Thread(infoStream).start();
				new Thread(new ProcessMonitor()).start();
				//TODO: wait until started ...
				long totalTimeOut = 0;
				while (totalTimeOut < MAX_TIMEOUT && infoStream.length < 20) {
					synchronized (this) {
						try {
							this.wait(50);
							totalTimeOut += 50;
						} catch (InterruptedException e) {
							log.debug("woke up");
						}
					}
				}
				if (totalTimeOut < MAX_TIMEOUT)
					runStateHolder.setRunState(RUNSTATE.RUNNING);
				else 
					log.warn("no answer from R - please make sure R is installed an Rserve running.");
			}catch (final IOException x) {
				log.fatal(x);
			} 
		}
	}
	
	class ProcessMonitor implements Runnable {
		
		ProcessMonitor() {
		}
		
		@Override
		public void run() {
			try {
				final int exitCode = process.waitFor();
				log.info("R process exited : "+exitCode);
			} catch (InterruptedException e) {
				log.error("process was forcefully killed.");
			}
			runStateHolder.setRunState(RUNSTATE.TERMINATED);
		}
		
	}
	
	enum Mode {
		ERROR, INFO
	}
	class StreamLogger implements Runnable{

		private final String prefix;
		private final Mode mode;
		private final BufferedReader reader;
		private long length;
		
		StreamLogger(final String prefix, final Mode mode, final InputStream in) {
			this.prefix = prefix;
			this.mode = mode;
			if (in == null)
				throw new NullPointerException("missing io stream for r process");
			this.reader = new BufferedReader(new InputStreamReader(in));
		}

		@Override
		public void run() {
			String line = null;
			try {
				while ( (line = reader.readLine()) != null ) {
					length += line.length();
					switch (mode) {
						case ERROR: log.error(prefix +" "+ line); break;
						case INFO:	log.info(prefix +" "+ line); break;
					}
				}
			} catch (final IOException e) {
				log.fatal(e);
			}
		}
		
		public long getLogLength() {
			return length;
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
