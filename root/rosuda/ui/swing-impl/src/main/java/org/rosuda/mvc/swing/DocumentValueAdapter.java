package org.rosuda.mvc.swing;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

public interface DocumentValueAdapter<T> {

	T getValue();
	
	void setValue(T value);
	
	public static class String implements DocumentValueAdapter<java.lang.String> {

		private final javax.swing.text.Document document;
		
		public String(final javax.swing.text.Document document) {
			this.document = document;
		}
		
		@Override
		public java.lang.String getValue() {
			try {
				return document.getText(0, document.getLength());
			} catch (final BadLocationException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void setValue(java.lang.String value) {
			try {
				document.remove(0, document.getLength());
				document.insertString(0, value, null);
			} catch (final BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static class Document implements DocumentValueAdapter<javax.swing.text.Document> {

		private final JTextComponent owner;
		
		public Document(final JTextComponent owner) {
			this.owner = owner;
		}
		@Override
		public javax.swing.text.Document getValue() {
			return owner.getDocument();
		}

		@Override
		public void setValue(javax.swing.text.Document value) {
			owner.setDocument(value);
		}
		
	}
	
	public static class HTMLDocument implements DocumentValueAdapter<javax.swing.text.html.HTMLDocument> {

		private final javax.swing.text.html.HTMLDocument document;
		
		public HTMLDocument(final javax.swing.text.html.HTMLDocument document) {
			this.document = document;
		}
		@Override
		public javax.swing.text.html.HTMLDocument getValue() {
			return document;
		}

		@Override
		public void setValue(javax.swing.text.html.HTMLDocument value) {
			throw new UnsupportedOperationException();
		}
		
	}

}
