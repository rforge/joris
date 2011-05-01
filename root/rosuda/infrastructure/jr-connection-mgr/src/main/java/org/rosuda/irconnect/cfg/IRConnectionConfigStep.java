package org.rosuda.irconnect.cfg;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.RServerException;

public interface IRConnectionConfigStep {

	/**
	 * executes a series of commands on an rconnection
	 * (use to execute install library command like {R-code:})
	 *  if (which(.packages(all.available=TRUE)=="TIMP")==0) {
	 *  	install.packages("TIMP",repos="http://cran.r-project.org") 
	 *  }
	 *  #return value
	 *  (which(.packages(all.available=TRUE)=="TIMP")>0)
	 * 
	 * @param con
	 * @return success or failure
	 * @throws RServerException
	 */
	public boolean doWithConnection(final IRConnection con) throws RServerException;
	
}
