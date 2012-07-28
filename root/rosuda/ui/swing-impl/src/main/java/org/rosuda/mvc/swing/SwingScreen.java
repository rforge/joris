package org.rosuda.mvc.swing;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.rosuda.ui.core.mvc.Screen;

public class SwingScreen implements Screen {

	private int[] getMinimumDimension() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		int minWidth = 50000;
		int minHeight = 50000;
		// Get size of each screen
		for (int i=0; i<gs.length; i++) {
		    DisplayMode dm = gs[i].getDisplayMode();
		    minWidth = Math.min(minWidth, dm.getWidth());
		    minHeight = Math.min(minHeight, dm.getHeight());
		}
		return new int[]{minWidth, minHeight};
	}
	@Override
	public int getWidth() {
		return getMinimumDimension()[0];
	}

	@Override
	public int getHeight() {
		return getMinimumDimension()[1];
	}

}
