package org.rosuda.ui.core.mvc;

public interface MVP<M extends MVP.Model, V extends MVP.View> {

	/**
	 * the data model
	 * @author ralfseger
	 *
	 */
	public interface Model {
	}
	
	public interface View<C>{

		void disable();
		
		void enable();
		
		void show();
		
		void hide();
		
		/**
		 * get the container for the target platform, either rich client like "swing", "awt" etc or web based.
		 * @return
		 */
		C getContainer();
		
	}
	
	interface Presenter<M extends MVP.Model, V extends MVP.View> {
		/**
		 * binds model and view
		 */
		void bind(final M model, final V view, final MessageBus mb);
		/**
		 * unbinds the components
		 */
		void unbind(final M model, final V view, final MessageBus mb);
	}
	
	
}
