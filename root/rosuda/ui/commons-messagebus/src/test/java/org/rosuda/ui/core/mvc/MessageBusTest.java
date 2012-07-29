package org.rosuda.ui.core.mvc;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MessageBusTest{

	private MessageBus bus;
	int eventCount = 0;
	
	@Before
	public void setUp() {
	    MessageBus.Impl impl = new MessageBus.Impl();
	    impl.setAsynchMode(false);
	    bus = impl;   
	}
	
	class TestEvent implements MessageBus.Event {	
	}
	
	@Test
	public void testSendMessage() {
		bus.registerListener(new MessageBus.EventListener<MessageBusTest.TestEvent>() {
			public void onEvent(TestEvent event) {
				eventCount ++;
			}
		});
		bus.fireEvent(new TestEvent());
		assertEquals("no event received", 1, eventCount);
	}
	
}
