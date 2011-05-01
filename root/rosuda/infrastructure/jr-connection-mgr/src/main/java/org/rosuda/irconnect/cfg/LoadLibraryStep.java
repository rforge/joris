package org.rosuda.irconnect.cfg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.RServerException;
import org.springframework.beans.factory.annotation.Required;

public class LoadLibraryStep implements IRConnectionConfigStep {

	Log LOG = LogFactory.getLog(LoadLibraryStep.class);

	private String library;

	@Required
	public void setLibrary(final String library) {
		this.library = library;
	}

	@Override
	public boolean doWithConnection(final IRConnection con)
			throws RServerException {
		final IREXP required = con.eval("as.logical(require(\"" + library
				+ "\"))");
		final boolean success = required.asBool().isTRUE();
		LOG.info("library \""+library+"\" has been auto-loaded");
		return success;
	}

}
