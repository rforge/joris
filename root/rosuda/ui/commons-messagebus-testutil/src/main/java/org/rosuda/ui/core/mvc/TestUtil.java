package org.rosuda.ui.core.mvc;

import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Set;

import org.rosuda.ui.core.mvc.HasClickable.ClickEvent;

public class TestUtil {

    public static void simulateLeftClick(DefaultHasClickable clickable) {
	clickable.click(new ClickEvent() {

	    @Override
	    public Set<Modifier> getModifiers() {
		return Collections.emptySet();
	    }

	    @Override
	    public int getClickCount() {
		return 1;
	    }

	    @Override
	    public int getButton() {
		return MouseEvent.BUTTON1;
	    }
	});
    }
}
