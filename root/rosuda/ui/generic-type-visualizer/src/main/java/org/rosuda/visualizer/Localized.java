package org.rosuda.visualizer;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public interface Localized {
	String get(final String localizedProperty);
	
	public static class ResourceBundleImpl implements Localized {
		private final ResourceBundle resources;
		
		public ResourceBundleImpl(final ResourceBundle resources) {
			this.resources = resources;
		}

		public String get(final String localizedProperty) {
			if (resources == null)
				return localizedProperty;
			try {
				return resources.getString(localizedProperty);
			} catch (final MissingResourceException mre) {
				return new StringBuilder().append("-").append(localizedProperty).append("-").toString();
			}
		}
	}
}
