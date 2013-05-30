package org.rosuda.linux.socket;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.Socket;
import java.net.SocketException;

import javax.activation.UnsupportedDataTypeException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.newsclub.net.unix.AFUNIXSocketAddress;
import org.rosuda.util.java.file.FileFinderUtil;
import org.rosuda.util.process.OS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpTunnelTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpTunnelTest.class);

    private static final long TIMEOUT = 10000;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private byte[] payload;
    private File file;
    private Process rserveProcess;
    private TcpTunnelServer tunnel;
    private String host;
    private int port;
    private Socket socket;

    @Before
    public void setUp() throws Exception {
        if (OS.isWindows()) {
            return;
        }
        file = tempFolder.newFile("socketFile");
        file.delete();
        rserveProcess = createRServeSocketProcess(file);
        synchronized (this) {
            // TODO improvement : wait for process stream io with timeout
            this.wait(TIMEOUT);
        }
        new NativeSocketLibUtil().enableDomainSockets();
        AFUNIXSocketAddress afunixSocketAddress = new AFUNIXSocketAddress(file);
        host = getFromEnviromentWithDefault("JORIS_host", "localhost");
        port = getFromEnviromentWithDefault("JORIS_port", 9999);
        payload = "1*2*3*5*8*13".getBytes("UTF8");
        tunnel = new TcpTunnelServer(host, port, afunixSocketAddress);
    }

    @After
    public void tearDown() {
        if (OS.isWindows()) {
            return;
        }
        try {
            tunnel.shutdown();
            rserveProcess.destroy();
        } finally {
            killAllRProcesses();
        }
    }

    @Test
    public void closingTheTunnelFreesThePort() throws Exception {
        if (OS.isWindows()) {
            return;
        }
        LOGGER.info("connecting to socket(" + host + "," + port + ")");
        socket = new Socket(host, port);
        socket.getOutputStream().write(payload);
        tunnel.shutdown();
        try {
            new Socket(host, port);
            fail("socket cannot connect since tunnel has been closed");
        } catch (final SocketException se) {

        }
    }

    // -- helper

    private Process createRServeSocketProcess(File socketFile) throws Exception {
        // find R exec
        final FileFinderUtil fileFinderUtil = new FileFinderUtil();
        String rExecutable = fileFinderUtil.findFileByName("R").get(0).getAbsolutePath(); // "/usr/lib/R/bin/R";
        String rServeBinary = fileFinderUtil.findFileByRegularExpression("Rserve(.*).so").get(0).getAbsolutePath(); // "/home/ralf/R/x86_64-pc-linux-gnu-library/2.14/Rserve/libs/Rserve-bin.so";
        String tunnelFile = socketFile.getAbsolutePath();
        final String startCmd = String.format("%s CMD %s --RS-socket %s --no-save --slave", rExecutable, rServeBinary, tunnelFile);
        LOGGER.info("starting rserve>" + startCmd);
        return Runtime.getRuntime().exec(startCmd);
    }

    @SuppressWarnings("unchecked")
    private <TYPE> TYPE getFromEnviromentWithDefault(String environmentLookupKey, TYPE defaultValue) throws Exception {
        final String fromEnvironment = System.getenv(environmentLookupKey);
        if (fromEnvironment == null) {
            return defaultValue;
        } else {
            if (defaultValue instanceof String) {
                return (TYPE) fromEnvironment;
            } else if (defaultValue instanceof Number) {
                final Class<TYPE> defaultType = (Class<TYPE>) defaultValue.getClass();
                final Constructor<TYPE> stringConstructor = defaultType.getConstructor(String.class);
                return stringConstructor.newInstance(fromEnvironment);
            } else {
                throw new UnsupportedDataTypeException();
            }
        }
    }

    private void killAllRProcesses() {
        try {
            Runtime.getRuntime().exec("pkill Rserve");
        } catch (IOException e) {
        }
    }

}
