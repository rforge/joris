package rforge.joris;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class RConnectionDebugger {
    
    public static void main(String[] args) {
	final String host = args[0];
	final String port = args[1];
	System.out.println("starting RConnection(host=\""+host+"\",port="+port+")");
	try {
	    final RConnection con = new RConnection(host, Integer.parseInt(port));
	    System.out.println("sqrt(14) = "+con.eval("sqrt(14)").asDouble());
	} catch (Throwable e) {
	    e.printStackTrace();
	} 
    }

}
