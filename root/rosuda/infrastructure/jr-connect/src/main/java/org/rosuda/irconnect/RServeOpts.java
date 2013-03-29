package org.rosuda.irconnect;

public enum RServeOpts {
	PORT, SOCKET, WORKDIR, ENCODING, CONF, SETTINGS;

	public String getEnvironmentName() {
		return "RSERVE_" + name();
	}

	public String asRServeOption() {
		return "--RS-" + name().toLowerCase();
	}

}
