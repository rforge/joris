package org.rosuda.util.r.impl;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServeOpts;
import org.rosuda.util.process.RunStateHolder;
import org.rosuda.util.process.ShellContext;

public class UnixStarterTest {

	private UnixRStarter unixRStarter;
	private RStartContext context;
	private TestShellContext shellContext;
	
	
	private class TestShellContext extends ShellContext {
		private Map<String, String> argMap = new HashMap<String, String>();
		
		void setProperty(final String propertyName, final String propertyValue) {
			argMap.put(propertyName, propertyValue);
		}
		
		@Override
		public String getProperty(String propertyName) {
			return argMap.get(propertyName);
		}
	}
	
	@Before
	public void setUp() {
		RunStateHolder<IRConnection> runStateHolder = mock(RunStateHolder.class);
		context = new RStartContext();
		shellContext = new TestShellContext();
		context.setShellContext(shellContext);
		this.unixRStarter = new UnixRStarter(runStateHolder , context);
	}
	
	@Test
	public void withStandardShellContextNoSocketIsUsed() {
		String executableRFile = "Rserve";
		assertThat(unixRStarter.getRuntimeArgs(executableRFile), not(hasItemInArray(containsString(RServeOpts.SOCKET.asRServeOption()))));
	}
	
	@Test
	public void givenTheShellContextProvidesASocketArgumentThisSocketIsUsed() {
		String socketValue = "/tmp/rservesocket";
		shellContext.setProperty(RServeOpts.SOCKET.getEnvironmentName(), socketValue);
		
		String executableRFile = "Rserve";
		assertThat(unixRStarter.getRuntimeArgs(executableRFile), hasItemInArray(containsString(RServeOpts.SOCKET.asRServeOption()+" "+socketValue+" ")));
	}
}
