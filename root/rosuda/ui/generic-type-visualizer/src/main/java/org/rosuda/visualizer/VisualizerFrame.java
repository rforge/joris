package org.rosuda.visualizer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;

import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.ITwoWayConnection;
import org.rosuda.mapper.filter.ObjectTransformationManager;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.type.Node;
import org.rosuda.type.impl.NodeBuilderFactory;
import org.rosuda.ui.core.mvc.MessageBus;
import org.rosuda.visualizer.NodeTreeModel.NodeToTreeNodeWrapper;
import org.rosuda.visualizer.NodeTreeModel.ValueToTreeNodeWrapper;
import org.rosuda.visualizer.step.LabelledMultiSelectionStep;
import org.rosuda.visualizer.step.LabelledSelectionStep;
import org.rosuda.visualizer.step.LabelledView;

public class VisualizerFrame extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1786188769301322816L;

	public enum Step {
		Trigger, Quality, Complexity, Filter //, Add, Move
	}

	public class NodeEvent implements MessageBus.Event {

		private final Node<?> node;
		private final Step step;
		
		public Node<?> getNode() {
			return node;
		}
		
		public Step getStep() {
			return step;
		}

		public NodeEvent(final Step step, final Node<?> node) {
			this.node = node;
			this.step = step;
		}
		
	}
	
	
		
	private final Configuration configuration = new Configuration();
	final Visualizer<?> visualizer;
	final Localized localized;
	private Step step;
	
	//layout:
	final JPanel contentPanel = new JPanel(new BorderLayout());
	//steps
	final JPanel westPanel = new JPanel(new GridLayout(5,1));
	final JPanel centerPanel = new JPanel(new GridLayout(5,1));
	
	final JLabel stepLabel = new JLabel();
	final JEditorPane description = new JEditorPane();			
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public VisualizerFrame(final Node<?> node) {
		this.visualizer = new Visualizer(node);
		final ResourceBundle localization = ResourceBundle.getBundle(Visualizer.class.getName());
		this.localized = new Localized.ResourceBundleImpl(localization);
		setTitle(localized.get("Frame.Title"));
		description.setEditable(false);
		description.setContentType("text/html");
		getContentPane().setLayout(new FlowLayout());
		final JPanel stepPanel = new JPanel(new BorderLayout());
		stepPanel.add(stepLabel, BorderLayout.NORTH);
		stepPanel.add(description, BorderLayout.CENTER);
		final JSplitPane layout = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		layout.setResizeWeight(0.3);
		getContentPane().add(layout);
		layout.add(stepPanel);
		final JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		visualizer.setPreferredSize(new Dimension(320, 480));
		content.setResizeWeight(0.5);
		content.add(new JScrollPane(visualizer, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
		content.add(contentPanel);
		contentPanel.add(westPanel, BorderLayout.WEST);
		contentPanel.add(centerPanel, BorderLayout.CENTER);		
		layout.add(content);
		
		//prepare step 1
		final MessageBus bus = MessageBus.INSTANCE;
		description.setPreferredSize(new Dimension(640,60));
		
		final List<LabelledView> stepList = new ArrayList<LabelledView>();
		stepList.add(new LabelledSelectionStep.JPanelImpl(Step.Trigger, bus).getView());
		stepList.add(new LabelledSelectionStep.JPanelImpl(Step.Quality, bus).getView());
		stepList.add(new LabelledSelectionStep.JPanelImpl(Step.Complexity, bus).getView());
		stepList.add(new LabelledMultiSelectionStep.JPanelImpl(Step.Filter, bus).getView());
		//add multiSelection
		final Iterator<LabelledView> steps = stepList.iterator();
		//test wiring ...
		updateFrameWithNextView(steps.next());		
		//listen and wait for next Steps:
		bus.registerListener(new MessageBus.EventListener<LabelledSelectionStep.Presenter.LabelledSelectionEvent>() {
			@Override
			public void onEvent(final LabelledSelectionStep.Presenter.LabelledSelectionEvent event) {
				updateStep();
				//workflow - while 
				switch (event.getStepId()) {
					case Trigger : configuration.setTriggerElement(event.getValue()); break;
					case Quality: configuration.setQualityElement(event.getValue()); break;
					case Complexity: configuration.setComplexityElement(event.getValue()); break;
					case Filter: 
						visualizer.removeAllTreeSelectionListeners();
						configuration.addFilterElement(event.getValue()); 
						visualizer.enableDragMode();
						break;
					//case Add:
					//case Move:
				}
			}
			
			private void updateStep() {
				if (steps.hasNext())
					updateFrameWithNextView(steps.next());
				else {
					//TODO store config
				}				
			}
		});
		
		//listen for nodes
		visualizer.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(final TreeSelectionEvent evt) {
				final TreeNode lastElement = (TreeNode) evt.getNewLeadSelectionPath().getLastPathComponent();
				if (lastElement == null)
					return;
				if (!lastElement.isLeaf())
					return;
				if (lastElement instanceof ValueToTreeNodeWrapper) {
					final ValueToTreeNodeWrapper<?> wrapper = (ValueToTreeNodeWrapper<?>) lastElement;
					//final Value value = wrapper.getValue();
					final TreeNode parent = wrapper.getParent();
					if (parent instanceof NodeToTreeNodeWrapper<?>) {
						final NodeToTreeNodeWrapper<?> parentWrapper = (NodeToTreeNodeWrapper<?>) parent;
						bus.fireEvent(new NodeEvent(step, parentWrapper.getNode()));
					} else {
						;
					}
				}
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(640, 480);
		pack();
		setVisible(true);
	}
	
	private void updateFrameWithNextView(final LabelledView view) {
		westPanel.add(view.getContainer());
		description.setText(view.getDescription().getValue());
		stepLabel.setText(view.getStepDescription().getValue());
		this.step = view.getStep();
	}
	
	public static final void main(final String[] args) {
		final ObjectTransformationManager<Object> filterMgr
			= new ObjectTransformationManager<Object>(new NodeBuilderFactory<Object>(), 
				new IREXPMapper<Object>().createInstance());
		final Properties properties = new Properties();
		long tick = System.currentTimeMillis();
		final ITwoWayConnection connection = REngineConnectionFactory.getInstance().createTwoWayConnection(properties);
		final long mark1 = System.currentTimeMillis() - tick;
		tick = System.currentTimeMillis();
		final IREXP lmREXP = connection.eval("summary(lm(speed~dist,data=cars))");
		final long mark2 = System.currentTimeMillis() - tick;
		tick = System.currentTimeMillis();
		final Node<Object> lmNode = filterMgr.transform(lmREXP);
		final long mark3 = System.currentTimeMillis() - tick;
		tick = System.currentTimeMillis();
		new VisualizerFrame(lmNode);
		final long mark4 = System.currentTimeMillis() - tick;
		System.out.println("Benchmarks :");
		System.out.printf("%5.3f s\t create connection \n", (double) mark1 / 1000.0);
		System.out.printf("%5.3f s\t evaluate REXP \n", (double) mark2 / 1000.0);
		System.out.printf("%5.3f s\t convert REXP \n", (double) mark3 / 1000.0);
		System.out.printf("%5.3f s\t build UI \n", (double) mark4 / 1000.0);
		
		//mini benchmark:
		/*
		final ObjectTransformationManager<NodeImpl> filterMgr2
		= new ObjectTransformationManager<NodeImpl>(NodeImplBuilderFactory.get(), 
			new IREXPMapper<NodeImpl>().createInstance());
		
		final List<Long> times = new ArrayList<Long>();
		for (int i=0; i< 100; i++) {
			final long before = System.currentTimeMillis();
			filterMgr.transform(lmREXP);
			times.add(System.currentTimeMillis()-before);
		}
		
		final List<Long> times2 = new ArrayList<Long>();
		for (int i=0; i< 100; i++) {
			final long before = System.currentTimeMillis();
			filterMgr2.transform(lmREXP);
			times2.add(System.currentTimeMillis()-before);
		}
		
		
		long min=1000000;
		long min2=1000000;
		long sum =0;
		long sum2 =0;
		long max=0;
		long max2=0;
		
		for (int i=0;i<100;i++) {
			final long current = times.get(i);
			final long current2= times2.get(i);
			if (current < min) {
				min = current;
			}
			if (current2 < min2) {
				min2 = current2;
			}
			if (current > max) {
				max = current;
			}
			if (current2 > max2) {
				max2 = current2;
			}
			sum += current;
			sum2 += current2;
		}
		System.out.println("    xml   impl");
		System.out.printf("min %5.3f", (double) min / 1000.0);
		System.out.printf(" %5.3f \n", (double) min2 / 1000.0);
		System.out.printf("max %5.3f", (double) max / 1000.0);
		System.out.printf(" %5.3f \n", (double) max2 / 1000.0);
		System.out.printf("avg %5.3f", (double) sum / 100000.0);
		System.out.printf(" %5.3f \n", (double) sum2 / 100000.0);
		*/
	    //    xml   impl
	    //min 0,122 0,000 
	    //max 0,333 0,011 
	    //avg 0,139 0,001 


	}
}
