package org.rosuda.util.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.util.logging.LogMode;
import org.rosuda.util.logging.StreamLogger;

/**
 * 
 * 
 * @author RSE
 *
 *
 * this class tries to create a process subject to a max timeout
 * @param <T>
 */
public abstract class AbstractMaxTimeoutProcessProvider<T> {

	private static final Log log = LogFactory.getLog(AbstractMaxTimeoutProcessProvider.class);

	private long maxtimeout = 1000;
	private long polltime = 100;

	public void setMaxtimeout(long maxtimeout) {
		this.maxtimeout = maxtimeout;
	}
	
	public void setPolltime(long polltime) {
		this.polltime = polltime;
	}
	
	public T create(final Process process, final RunStateHolder<T> runstateHolder) {
		new Thread(new StreamLogger(this.getClass(), "RServe>", LogMode.ERROR, process.getErrorStream())).start();
		new Thread(new StreamLogger(this.getClass(), "RServe>", LogMode.INFO, process.getInputStream())).start();
		new Thread(new ProcessMonitor(runstateHolder, process)).start();
		//TODO: wait until started ...
		long totalTimeOut = 0;
		T result = null;
		while (result == null && totalTimeOut < maxtimeout) {
			synchronized (this) {
				try {
					this.wait(polltime);
					try {
						result = createResultInstance();
						//rcon = 
					} catch (final Exception x) {}
					totalTimeOut += polltime;
				} catch (InterruptedException e) {
					log.debug("woke up");
				}
			}
		}
		return result;
	}
	
	protected abstract T createResultInstance();

	private class ProcessMonitor implements Runnable {
		
		private final RunStateHolder<T> runStateHolder;
		private final Process process;
		
		ProcessMonitor(final RunStateHolder<T> runStateHolder, final Process process) {
			this.runStateHolder = runStateHolder;
			this.process = process;
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
}
