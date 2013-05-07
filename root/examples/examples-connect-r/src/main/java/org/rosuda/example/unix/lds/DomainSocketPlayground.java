package org.rosuda.example.unix.lds;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.rosuda.irconnect.TcpDomainServerManager;

public class DomainSocketPlayground {

    public static void main(String[] args) {
        System.out.println("Ensure that Rserve is available in DomainSocket mode>");
        /**
         * R CMD ~/R/x86_64-pc-linux-gnu-library/2.14/Rserve/Rserve --RS-socket /home/ralf/data_dir/socket/rserve --no-save --slave
         * R CMD ~/R/x86_64-pc-linux-gnu-library/2.14/Rserve/Rserve --RS-socket /home/ralf/data_dir/socket/rserve --no-save --slave
         * /home/ralf/R/x86_64-pc-linux-gnu-library/2.14/Rserve/libs//Rserve-bin.so --RS-socket /home/ralf/data_dir/socket/rserve --no-save --slave
         * /bin/sh -c echo 'library(Rserve);Rserve(args="--RS-socket /home/ralf/data_dir/socket/rserve --no-save --slave")' | /usr/bin/R --vanilla --slave
         */
        System.out.println("/bin/sh -c echo 'library(Rserve);Rserve(args=\"--RS-socket /home/ralf/data_dir/socket/rserve --no-save --slave\")' | /usr/bin/R --vanilla --slave");
        
        DomainSocketPlayground playground = new DomainSocketPlayground();
        final String socketfile ="/home/ralf/data_dir/socket/rserve";
        final String host ="localhost";
        final int port = 9999;
        playground.startSocketServer(host,port,socketfile);
        try {
            playground.createRConnectionsAndRetrieveResults(host, port);
        } catch (RserveException e) {
            throw new RuntimeException(e);
        } catch (REXPMismatchException e) {
            throw new RuntimeException(e);
        }
    }

    private void createRConnectionsAndRetrieveResults(String host, int port) throws RserveException, REXPMismatchException {
        RConnection rConnection1 = new RConnection(host, port);
        rConnection1.voidEval("x<-1");
        
        RConnection rConnection2 = new RConnection(host, port);
        //blocks
        rConnection2.voidEval("x<-2");
        
        int int1 = rConnection1.eval("x").asInteger();
        int int2 = rConnection2.eval("x").asInteger();
        
        System.out.println("con1.x<-"+int1);
        System.out.println("con2.x<-"+int2);
    }

    private void startSocketServer(String host, int port, String socket) {
        TcpDomainServerManager.getInstance().prepareEnvironmentForTcpConnection(host, port, socket);
    }
}
