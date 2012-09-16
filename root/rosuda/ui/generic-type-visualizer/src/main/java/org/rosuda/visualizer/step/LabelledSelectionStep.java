package org.rosuda.visualizer.step;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;

import org.rosuda.mvc.swing.DocumentValueAdapter;
import org.rosuda.mvc.swing.JButtonHasClickable;
import org.rosuda.mvc.swing.JLabelHasValue;
import org.rosuda.mvc.swing.DocumentHasValue;
import org.rosuda.mvc.swing.MVPContainerView;
import org.rosuda.type.TreeUtil;
import org.rosuda.ui.core.mvc.HasClickable;
import org.rosuda.ui.core.mvc.HasClickable.ClickEvent;
import org.rosuda.ui.core.mvc.HasClickable.ClickListener;
import org.rosuda.ui.core.mvc.HasValue;
import org.rosuda.ui.core.mvc.MVP;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.ui.core.mvc.MessageBus.EventListener;
import org.rosuda.ui.core.mvc.impl.HasValueImpl;
import org.rosuda.visualizer.Localized;
import org.rosuda.visualizer.VisualizerFrame;
import org.rosuda.visualizer.VisualizerFrame.NodeEvent;
import org.rosuda.visualizer.VisualizerFrame.Step;

public class LabelledSelectionStep implements MVP<JPanel, LabelledSelectionStep.Model, LabelledSelectionStep.View> {
	
	public static class JPanelImpl {
		
		private final Presenter presenter;
		private final Model model;
		private final View view;
		
		public JPanelImpl(final Step identifier, final MessageBus mb) {
			this.presenter = new Presenter(identifier);
			this.model = new Model(identifier);
			this.view = new View(identifier);
			presenter.bind(model, view, mb);
		}

		public LabelledView getView() {
			return view;
		}
		
		
	}
	
	public static class Model implements MVP.Model {
		
		private final Step identifier;
		
		private Model(final Step identifier) {
			this.identifier = identifier;
		}
		
		private String nodePath;

		public String getNodePath() {
			return nodePath;
		}

		public void setNodePath(final String nodePath) {
			System.out.println("set nodePath to "+nodePath+" for "+identifier);
			this.nodePath = nodePath;
		}
		
	}
	
	public static class View extends MVPContainerView<JPanel> implements LabelledView {

		private final HasValue<String> labelledField;
		private final HasValue<String> description;
		private final HasValue<String> stepDescription;	
		private final HasValue<String> label;
		private final HasClickable choseButton;
		final Localized localized;
		
		private final Step step;
		
		private View (final Step step) {
			super(new JPanel());
			this.step = step;
			final String prefix = step.toString();
			final ResourceBundle localization = ResourceBundle.getBundle(View.class.getName());
			this.localized = new Localized.ResourceBundleImpl(localization);
			//normalize java class Name into resource:
			final String htmlResourceLocation = "/" + View.class.getName().substring(0, View.class.getName().lastIndexOf(".")+1).replace('.', '/') + localized.get(prefix+".HTMLDescription");
			final InputStream htmlResource = View.class.getResourceAsStream(htmlResourceLocation);
			final StringBuilder descriptionBuilder = new StringBuilder();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(htmlResource));
			String line = null;
			final String newLine = System.getProperty("line.separator");
			try {
				while ( (line = reader.readLine()) != null) {
					descriptionBuilder.append(line);
					descriptionBuilder.append(newLine);
				}
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
			this.stepDescription = new HasValueImpl<String>(localized.get(prefix+".Step"));
			this.description = new HasValueImpl<String>(descriptionBuilder.toString());
			final JLabel jlabel = new JLabel(localized.get(prefix+".Label"));
			final JButton jchoseButton = new JButton(localized.get("Button.Select"));
			final JTextField nodePathField = new JTextField(30);
			nodePathField.setEditable(false);
			this.label = new JLabelHasValue(jlabel);
			this.choseButton = new JButtonHasClickable(jchoseButton);
			final Document document = nodePathField.getDocument();
			this.labelledField = new DocumentHasValue<String>(document, new DocumentValueAdapter.String(document));
			
			getViewContainer().add(jlabel, BorderLayout.WEST);
			getViewContainer().add(nodePathField, BorderLayout.CENTER);
			getViewContainer().add(jchoseButton, BorderLayout.EAST);
		}
		
		public HasValue<String> getLabel() {
			return label;
		}
		
		public HasValue<String> getNodePath() {
			return labelledField;
		}
		
		public HasValue<String> getDescription() {
			return description;
		}
		public HasValue<String> getStepDescription() {
			return stepDescription;
		}
		
		public HasClickable getButton() {
			return choseButton;
		}
				
		public Step getStep() {
			return step;
		}
	}
	
	public static class Presenter implements MVP.Presenter<LabelledSelectionStep.Model, LabelledSelectionStep.View> {

		private final Step identifier;
		
		private Presenter(final Step identifier) {
			this.identifier = identifier;
		}
		
		public class LabelledSelectionEvent implements MessageBus.Event {
			private final String value;
			
			public LabelledSelectionEvent(final String value) {
				this.value = value;
			}
			
			public String getValue() {
				return this.value;
			}			
			
			public Step getStepId() {
				return Presenter.this.identifier;
			}
		}
				
		private EventListener<VisualizerFrame.NodeEvent> nodeEventListener;
		public void bind(final LabelledSelectionStep.Model model, final LabelledSelectionStep.View view, final MessageBus messageBus) {
			nodeEventListener = new EventListener<VisualizerFrame.NodeEvent>() {
				@Override
				public void onEvent(final NodeEvent event) {
					if (!Presenter.this.identifier.equals(event.getStep()))
						return;
					final String value = TreeUtil.getId(event.getNode());
					view.getNodePath().setValue(value);
					model.setNodePath(value);
				}
			};	
			final ClickListener selectionListener = new ClickListener() {				
				public void onClick(final ClickEvent event) {
					if (model.getNodePath() != null && model.getNodePath().length() > 1) {
						view.disable();
						messageBus.fireEvent(new LabelledSelectionEvent(model.getNodePath()));
						messageBus.registerListener(nodeEventListener);
					}
				}
			};
			view.getNodePath().setValue(model.getNodePath());
			view.getButton().addClickListener(selectionListener);
			messageBus.registerListener(nodeEventListener);
		}

		public void unbind(final LabelledSelectionStep.Model model, final LabelledSelectionStep.View view, final MessageBus messageBus) {
			messageBus.removeListener(nodeEventListener);
		}
		
	}
	
}
