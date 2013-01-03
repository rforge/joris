package org.rosuda.ui;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.rosuda.ui.core.mvc.HasKeyEvent;
import org.rosuda.ui.main.MainModel;
import org.rosuda.ui.main.MainPresenter;
import org.rosuda.ui.main.MainView;
import org.rosuda.ui.test.MVPTest;

public class MainFrameTest extends MVPTest<MainModel, MainView<Object>, MainPresenter<Object>, MainFrameTestModelData> {

    @Override
    protected MainView<Object> createTestViewInstance() {
	return new MainFrameTestObjectView();
    }

    @Test
    public void pressingTheCrtKeySendsTheInputcontentToTheProtocol() {
	int initialLength = view.getProtocol().getValue().getLength();
	enterCommand("ein Text");
	assertThat(view.getProtocol().getValue().getLength(), greaterThan(initialLength));
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
