package org.rosuda.util.process;

import java.io.IOException;

public class ProcessContext {

	private Process process;

	private Runtime runtime = Runtime.getRuntime();
	
	public void setRuntime(final Runtime runtime) {
		this.runtime = runtime;
	}

	/**
	 * creates an external process
	 * @param runtimeArgs
	 * @return
	 * @throws IOException
	 */
	public Process createProcessForArgs(final String[] runtimeArgs) throws IOException {
		this.process = runtime.exec(runtimeArgs);
		return process;
	}

	/**
	 * creates an external Process
	 * @param startCommand
	 * @return
	 * @throws IOException
	 */
	public Process createProcessForArg(String startCommand) throws IOException {
		this.process = runtime.exec(startCommand);
		return process;
	}
	
	/**
	 * shutdown is required for maven tests!
	 */
	public boolean stop() {
		if (this.process != null)
			this.process.destroy();
		return this.process != null;
	}

}
