package org.rosuda.linux.socket.connection;

import java.io.IOException;
import java.net.Socket;

import org.newsclub.net.unix.AFUNIXSocket;
import org.rosuda.linux.socket.TcpTunnelServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpDomainTunnelConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpDomainTunnelConnection.class);
    private final Socket socket;
    private final IOStream domainToSocketStream;
    private final IOStream socketToDomainStream;
    private AFUNIXSocket domainsocket;

    public TcpDomainTunnelConnection(final TcpTunnelServer server, final AFUNIXSocket domainsocket, final Socket socket) throws IOException {
        this.domainsocket = domainsocket;
        this.socket = socket;
        this.domainToSocketStream = new IOStream(domainsocket.getInputStream(), socket.getOutputStream());
        Thread domainToSocketThread = new Thread(domainToSocketStream);
        domainToSocketThread.start();
        domainToSocketThread.setName("lds->tcpsocket");
        this.socketToDomainStream = new IOStream(socket.getInputStream(), domainsocket.getOutputStream());
        Thread socketToDomainThread = new Thread(socketToDomainStream);
        socketToDomainThread.start();
        socketToDomainThread.setName("tcpsocket->lds");
    }
    
    public void close() {
        domainToSocketStream.close();
        socketToDomainStream.close();
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.warn("could not close socket", e);
        }
        try {
            domainsocket.close();
        } catch (IOException e) {
            LOGGER.warn("could not close domainsocket", e);;
        }
    }

    public boolean isOpen() {
        return domainToSocketStream.isActive() && socketToDomainStream.isActive();
    }

}
