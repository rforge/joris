package org.rosuda.visualizer.step;

import java.awt.Component;

import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.visualizer.VisualizerFrame.Step;

public interface LabelledView {

	Component getContainer();
	HasValue<String> getDescription();
	HasValue<String> getStepDescription();
	Step getStep();

}
