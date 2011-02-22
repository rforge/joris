package org.rosuda.ui.core.mvc;

import junit.framework.TestCase;

public class TestMessageBus extends TestCase{

	private MessageBus bus = MessageBus.INSTANCE;
	int eventCount = 0;
	
	class TestEvent implements MessageBus.Event {
		
	}
	
	public void testSendMessage() {
		bus.registerListener(new MessageBus.EventListener<TestMessageBus.TestEvent>() {
			public void onEvent(TestEvent event) {
				eventCount ++;
			}
		});
		bus.fireEvent(new TestEvent());
		assertEquals("no event received", 1, eventCount);
	}
	
}
