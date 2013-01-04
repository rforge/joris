package org.rosuda.ui;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.rosuda.ui.core.mvc.HasKeyEvent;
import org.rosuda.ui.core.mvc.HasKeyEvent.KeyEvent;
import org.rosuda.ui.main.MainModel;
import org.rosuda.ui.main.MainPresenter;
import org.rosuda.ui.main.MainView;
import org.rosuda.ui.test.MVPTest;
import org.rosuda.ui.test.ModelInitializer;

public class MainFrameTest extends MVPTest<MainModel, MainView<Void>, MainPresenter<Void>, ModelInitializer<MainModel>> {

    private List<HasKeyEvent.KeyEvent> typedKeys;
    private static final String ERSTER_TEXT = "erster Text";
    private static final String ZWEITER_TEXT = "zweiter Text";

    @Before
    public void setUp() {
	typedKeys = new ArrayList<HasKeyEvent.KeyEvent>();
	view.getInputEvent().addKeyEventListener(new HasKeyEvent.KeyListener() {
	    @Override
	    public void onKeyEvent(KeyEvent event) {
		typedKeys.add(event);
	    }
	});
    }
    
    @Test
    public void pressingTheCrtKeySendsTheInputcontentToTheProtocol() {
	int initialLength = view.getProtocol().getValue().getLength();
	enterCommand("ein Text");
	assertThat(view.getProtocol().getValue().getLength(), greaterThan(initialLength));
    }

    @Test
    public void pressingUpKeyWithoutAnyCommandDoesNotChangeTheCurrentValue() {
	view.getInputEvent().sendEvent(HasKeyEvent.KeyEvent.Type.KEY_UP, 0, java.awt.event.KeyEvent.VK_UP);
	assertThat(view.getInputValue().getValue(), nullValue());
    }
    
    @Test
    public void pressingUpKeyDoesNotChangeTheCurrentValueWithoutFormerEnteredCommand() {
	setInputText(ERSTER_TEXT);
	view.getInputEvent().sendEvent(HasKeyEvent.KeyEvent.Type.KEY_UP, 0, java.awt.event.KeyEvent.VK_UP);
	assertThat(view.getInputValue().getValue(), equalTo(ERSTER_TEXT));
    }
    
    @Test
    public void previousCommandIsAutomaticallyRestoredWhenPressingUpKey() {
	enterCommand(ERSTER_TEXT);
	setInputText(ZWEITER_TEXT);
	view.getInputEvent().sendEvent(HasKeyEvent.KeyEvent.Type.KEY_UP, 0, java.awt.event.KeyEvent.VK_UP);
	assertThat(view.getInputValue().getValue(), equalTo(ERSTER_TEXT));
    }
    
    @Test
    public void pressingDownKeyWithoutAnyCommandDoesNotChangeTheCurrentValue() {
	view.getInputEvent().sendEvent(HasKeyEvent.KeyEvent.Type.KEY_UP, 0, java.awt.event.KeyEvent.VK_DOWN);
	assertThat(view.getInputValue().getValue(), nullValue());
    }
    
    @Test
    public void pressingDownKeyDoesNotChangeTheCurrentValueWithoutFormerEnteredCommand() {
	setInputText(ERSTER_TEXT);
	view.getInputEvent().sendEvent(HasKeyEvent.KeyEvent.Type.KEY_UP, 0, java.awt.event.KeyEvent.VK_DOWN);
	assertThat(view.getInputValue().getValue(), equalTo(ERSTER_TEXT));
    }

    @Test
    public void pressingDownRestoresLastText() {
	previousCommandIsAutomaticallyRestoredWhenPressingUpKey();
	view.getInputEvent().sendEvent(HasKeyEvent.KeyEvent.Type.KEY_UP, 0, java.awt.event.KeyEvent.VK_DOWN);
	assertThat(view.getInputValue().getValue(), equalTo(ZWEITER_TEXT));
    }
    // -- helper
    private void enterCommand(String inputText) {
	setInputText(inputText);
	view.getInputEvent().sendEvent(HasKeyEvent.KeyEvent.Type.KEY_UP, 0, java.awt.event.KeyEvent.VK_ENTER);
    }

    private void setInputText(String inputText) {
	view.getInputValue().setValue(inputText);
    }

}
