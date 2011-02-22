package org.rosuda.mapper.irexp.test;

import java.util.Properties;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.ITwoWayConnection;
import org.rosuda.mapper.filter.NodeFilter;
import org.rosuda.mapper.filter.NodeFilter.Event;
import org.rosuda.mapper.filter.NodeValueFilter;
import org.rosuda.mapper.filter.ObjectTransformationManager;
import org.rosuda.mapper.irexp.IREXPMapper;
import org.rosuda.rengine.REngineConnectionFactory;
import org.rosuda.type.Node;
import org.rosuda.type.Value;
import org.rosuda.type.impl.NodeBuilderFactory;

public class RFilterTest extends TestCase {

	int countAccepted = 0;
	int countRejected = 0;
	private static final boolean debug = false;
	private static final Logger logger = Logger.getLogger(RFilterTest.class.getCanonicalName());
	private ITwoWayConnection connection;
	private ObjectTransformationManager<Object> filterMgr;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.filterMgr = new ObjectTransformationManager<Object>(new NodeBuilderFactory<Object>(), 
				new IREXPMapper<Object>().createInstance());
		final Properties properties = new Properties();
		try {
			connection = REngineConnectionFactory.getInstance().createTwoWayConnection(properties);
		} catch (final Exception x) {
			x.printStackTrace();
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		connection.close();
	}
	
	public void testUnfilteredModel() {
		if (connection == null) {
			logger.warning("no connection available, test cannot be run");
			return;
		}
		final IREXP lmREXP = connection.eval("lm(speed~dist,data=cars)");
		assertEquals(IREXP.XT_MAP, lmREXP.getType());
		final Node<?> lmNode = filterMgr.transform(lmREXP);
		assertNotNull(lmNode);
		if (debug) System.out.println(lmNode);
		assertEquals(12, lmNode.getChildCount()); //keyvalues	
	}
	
	public void testFilteredModel() {
		if (connection == null) {
			logger.warning("no connection available, test cannot be run");
			return;
		}
		final NodeFilter.EventListener<Object> listener = new NodeFilter.EventListener<Object>() {
			public void triggered(Event<Object> event) {
				if (event.wasAccepted())
					countAccepted ++;
				else 
					countRejected ++;
			}
		};
		final NodeFilter<Object> filterResiduals = new NodeFilter<Object>() {
			@Override
			protected boolean mayCreateChild(Node.Builder<Object> parent, String... newChildName) {
				final String name = newChildName != null && newChildName.length > 0 
					? newChildName[0]
					: null;
				if ("residuals".equals(name)) {
					return false;
				}
				return true;
			}
			
		};
		filterResiduals.addListener(listener);
		filterMgr.addFilter(filterResiduals);
		final IREXP lmREXP = connection.eval("lm(speed~dist,data=cars)");
		assertEquals(IREXP.XT_MAP, lmREXP.getType());
		final Node<?> lmNode = filterMgr.transform(lmREXP);
		if (debug) System.out.println(lmNode);
		assertNotNull(lmNode);
		assertEquals(11,lmNode.getChildCount());
		assertEquals(1, countRejected);
		assertTrue(countAccepted > 11);
	}
	
	public void testFilteredModelMapKeyValues() {
		if (connection == null) {
			logger.warning("no connection available, test cannot be run");
			return;
		}
		final NodeFilter.EventListener<Object> listener = new NodeFilter.EventListener<Object>() {
			public void triggered(Event<Object> event) {
				if (event.wasAccepted())
					countAccepted ++;
				else 
					countRejected ++;
			}
		};
		final NodeFilter<Object> filterResiduals = new NodeFilter<Object>() {
			@Override
			protected boolean mayCreateChild(Node.Builder<Object> parent, String... newChildName) {
				final String name = newChildName != null && newChildName.length > 0 
					? newChildName[0]
					: null;
//					> names(lm(speed~dist,data=cars))
//					 [1] "coefficients"  "residuals"     "effects"       "rank"         
//					 [5] "fitted.values" "assign"        "qr"            "df.residual"  
//					 [9] "xlevels"       "call"          "terms"         "model"    
				if (//"coefficients".equals(name) ||
					"residuals".equals(name) ||
					"effects".equals(name) ||
					"rank".equals(name) ||
					"fitted.values".equals(name) ||
					"assign".equals(name) ||
					"qr".equals(name) ||
					"df.residual".equals(name) ||
					"xlevels".equals(name) ||
					"call".equals(name) ||
					"terms".equals(name) ||
					"model".equals(name)
				) {
					return false;
				}
				return true;
			}
			
		};
		filterResiduals.addListener(listener);
		filterMgr.addFilter(filterResiduals);
		final IREXP lmREXP = connection.eval("lm(speed~dist,data=cars)");
		assertEquals(IREXP.XT_MAP, lmREXP.getType());
		final Node<?> lmNode = filterMgr.transform(lmREXP);
		if (debug) System.out.println(lmNode);
		assertNotNull(lmNode);
		assertEquals(11, countRejected);
		//coefficients + childs:
		//> length(lm(speed~dist,data=cars)$coefficients)
		//[1] 2
		assertEquals(3, countAccepted);
		assertEquals(1, lmNode.getChildCount()); //rmap
	}

	public void testFilteredResiduals() {
		if (connection == null) {
			logger.warning("no connection available, test cannot be run");
			return;
		}
		final NodeValueFilter.EventListener<Object> listener = new NodeValueFilter.EventListener<Object>() {
			public void triggered(NodeValueFilter.Event<Object> event) {
				if (event.wasAccepted())
					countAccepted ++;
				else 
					countRejected ++;
			}
		};
		@SuppressWarnings("unused")
		final NodeFilter.EventListener<Node.Builder<Object>> listener2 = new NodeFilter.EventListener<Node.Builder<Object>>() {
			public void triggered(NodeFilter.Event<Node.Builder<Object>> event) {
				if (event.wasAccepted())
					countAccepted ++;
				else 
					countRejected ++;
			}
		};
		
		final NodeValueFilter<Object> filterQrIntercepts = new NodeValueFilter<Object>() {
			@Override
			protected boolean maySetValue(final Node.Builder<Object> parent, final Value newValue) {
				if ("(Intercept)".equals(parent.getName())
					&& parent.getParent() != null 
					&& parent.getParent().getParent() != null
					&& "matrix".equals(parent.getParent().getParent().getName())
				) {
					if (newValue.getType()==Value.Type.NUMBER) {
						//manipulate parent node ?
						//dont accept any children and remove this node!
						//parent->getParent():keyValue (container)
						//parent->getParnet()->getParent:map (supercontainer)
						parent.getParent().delete(parent);
						return false;
					}
				}
				return true;
			}
			
		};
		filterQrIntercepts.addListener(listener);
		
		
		filterMgr.addFilter(filterQrIntercepts);
		final IREXP lmREXP = connection.eval("lm(speed~dist,data=cars)");
		assertEquals(IREXP.XT_MAP, lmREXP.getType());
		final Node<Object> lmNode = filterMgr.transform(lmREXP);
		if (debug) 
			System.out.println(lmNode);
		assertNotNull(lmNode);
		assertEquals(50, countRejected); // all qr intercept
		//check Matrix
		int distCount = 0;
		int otherCount = 0;
		for (final Node<Object> child : lmNode.getChildren()) {
			if ("qr".equals(child.getName())) {
				for (final Node<Object> qrChild : child.getChildren()) {
					if ("qr".equals(qrChild.getName())) {
						for (final Node<Object> qrqrChild : qrChild.getChildren()) {
							if ("matrix".equals(qrqrChild.getName())) {
								for (final Node<Object> matrixChild : qrqrChild.getChildren()) {
									//rowholders
									for (final Node<Object> rowChild : matrixChild.getChildren()) {
										if ("dist".equals(rowChild.getName())) {
											distCount ++;
										} else {
											otherCount = 0;
										}
									}
								}
							}
							
						}
					}
				}
				
			}
		}
		assertEquals(50, distCount);
		assertEquals(0, otherCount);
	}
	
	//"structural" tests are only found for
	//reject node
	//reject value
	//on node .. do something with the current tree
	//on value .. do something with the current tree
	
	@Deprecated
	public void XtestFilteredSummaryResiduals() {
		if (connection == null) {
			logger.warning("no connection available, test cannot be run");
			return;
		}
		final NodeValueFilter.EventListener<Object> listener = new NodeValueFilter.EventListener<Object>() {
			public void triggered(NodeValueFilter.Event<Object> event) {
				if (event.wasAccepted())
					countAccepted ++;
				else 
					countRejected ++;
			}
		};
		@SuppressWarnings("unused")
		final NodeFilter.EventListener<Object> listener2 = new NodeFilter.EventListener<Object>() {
			public void triggered(NodeFilter.Event<Object> event) {
				if (event.wasAccepted())
					countAccepted ++;
				else 
					countRejected ++;
			}
		};
		final NodeValueFilter<Object> filterResiduals = new NodeValueFilter<Object>() {

			@Override
			protected boolean maySetValue(final Node.Builder<Object> parent, final Value newValue) {
				System.out.println("parent.name="+parent.getName()+ " val"+newValue);
				if ("Double".equals(parent.getName())) {
					System.out.println("2."+parent.getParent().getName());
				}
				//> names(summary(lm(speed~dist,data=cars)))
				// [1] "call"          "terms"         "residuals"     "coefficients" 
				// [5] "aliased"       "sigma"         "df"            "r.squared"    
				// [9] "adj.r.squared" "fstatistic"    "cov.unscaled" 
				
				//> names(summary(lm(speed~dist,data=cars))$coefficients[1,])
				//[1] "Estimate"   "Std. Error" "t value"    "Pr(>|t|)"  

				if ("Double".equals(parent.getName())&&parent.getParent()!=null) {
					final Node.Builder<Object> grandParent = parent.getParent();
					if (debug) System.out.println("CREATE KEY "+newValue);
					if (newValue.getType()==Value.Type.STRING && 
						("residuals".equals(newValue.getString()) ||
						 //"qr".equals(newValue.getString()) ||
						 "aliased".equals(newValue.getString()) ||	
						 "sigma".equals(newValue.getString()) ||
						 "fstatistic".equals(newValue.getString())||
						 "t value".equals(newValue.getString())
						)
					) {
						//manipulate parent node ?
						//dont accept any children and remove this node!
						//parent->getParent():keyValue (container)
						//parent->getParnet()->getParent:map (supercontainer)
						parent.getParent().getParent().delete(parent.getParent());
						//add no more here:
						@SuppressWarnings("unused")
						final NodeFilter<Object> innerFilter = new NodeFilter<Object>() {
							@Override
							protected boolean mayCreateChild(final Node.Builder<Object> innerParent, final String... newChildName) {
								boolean isParent = innerParent.getParent().equals(parent.getParent());								
								return !isParent;
							}
						};
						//alt
						//filterMgr.addFilter(innerFilter);
						//innerFilter.addListener(listener2);
						return false;
					}
				}
				return true;
			}
			
		};
		filterResiduals.addListener(listener);
		
		
		filterMgr.addFilter(filterResiduals);
		final IREXP lmREXP = connection.eval("summary(lm(speed~dist,data=cars))");
		assertEquals(IREXP.XT_MAP, lmREXP.getType());
		final Node<Object> lmNode = filterMgr.transform(lmREXP);
		//if (debug) 
			System.out.println(lmNode);
		assertNotNull(lmNode);
		assertEquals(4, countRejected); // 6 named
		//assertEquals(1, countAccepted);
		assertEquals(1, lmNode.getChildCount()); //rmap
	}

}
