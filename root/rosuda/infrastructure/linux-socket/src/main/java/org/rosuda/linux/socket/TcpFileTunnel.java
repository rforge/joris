package org.rosuda.linux.socket;

import java.io.File;
import java.io.IOException;

import org.newsclub.net.unix.AFUNIXSocketAddress;

public class TcpFileTunnel {

    public static void main(final String[] args) {
        if (args.length < 3){
            System.out.println("usage TcpFileTunnel [file] [host] [port]");
            System.exit(-1);
        }
        final File socketFile = new File(args[0]);
        if (!socketFile.exists()) {
            throw new IllegalArgumentException("file \""+args[0]+"\" does not exist.");
        }
        final int port = Integer.parseInt(args[2]);
        try {
            AFUNIXSocketAddress socketAddress = new AFUNIXSocketAddress(socketFile);
            new TcpTunnelServer(args[1], port, socketAddress);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }
}
