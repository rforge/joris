package rforge.joris;

import org.rosuda.REngine.Rserve.RConnection;

public class RConnectionDebugger {
    
    public static void main(String[] args) {
	RConnection con = null;
	if (args.length>1) {
	    final String host = args[0];
	    final String port = args[1];
	    con = connectToHostAndPort(host, port); 
	} else {
	    final String socket = args[0];
	    con = connectToSocket(socket);
	}
	try {
	    System.out.println("sqrt(14) = "+con.eval("sqrt(14)").asDouble());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private static RConnection connectToSocket(String socket) {
	System.out.println("starting RConnection(socket=\""+socket+"\")");
	try {
	    return new RConnection(socket);
	} catch (Throwable e) {
	    e.printStackTrace();
	    throw new RuntimeException("failed to connect", e);
	}
    }

    private static RConnection connectToHostAndPort(final String host, final String port) {
	System.out.println("starting RConnection(host=\""+host+"\",port="+port+")");
	try {
	    return new RConnection(host, Integer.parseInt(port));
	} catch (Throwable e) {
	    e.printStackTrace();
	    throw new RuntimeException("failed to connect", e);
	}
    }

}
