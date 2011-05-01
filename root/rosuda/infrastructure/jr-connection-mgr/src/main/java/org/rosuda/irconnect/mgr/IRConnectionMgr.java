package org.rosuda.irconnect.mgr;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.irconnect.cfg.IRConnectionConfig;

public interface IRConnectionMgr {

	public IRConnection getIRConnection(final IRConnectionConfig config);
}
