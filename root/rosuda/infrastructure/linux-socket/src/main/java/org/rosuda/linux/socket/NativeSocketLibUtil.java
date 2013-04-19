package org.rosuda.linux.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.management.RuntimeErrorException;

import org.newsclub.net.unix.AFUNIXServerSocket;
import org.rosuda.util.process.OS;
import org.rosuda.util.process.ShellContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeSocketLibUtil {

    public static final String NATIVE_LIB_PATH = "NATIVE_LIB_PATH";
    public static final String PROP_LIBRARY_LOADED = "org.newsclub.net.unix.library.loaded";
    public static final String ENV_NATIVE_LIBRARY_PATH = "org.newsclub.net.unix.library.path";
    private Set<String> tempFolders = new HashSet<String>();
    private static final String resourcePath = "org/rosuda/linux/socket";
    private static final Logger LOGGER = LoggerFactory.getLogger(NativeSocketLibUtil.class);
    private static final int BUFFER_SIZE = 1024;
    private ShellContext context = new ShellContext();
    private String suffix;
    private String arch;

    public void setShellContext(ShellContext context) {
        this.context = context;
    }

    public void enableDomainSockets() {
        if (OS.isWindows()) {
            LOGGER.warn("unsupported operation system!");
        }
        if (context.getSystemProperty(PROP_LIBRARY_LOADED) != null) {
            LOGGER.info("Native socket library has already been loaded, environmt variable set.\n"
                    + context.getSystemProperty(PROP_LIBRARY_LOADED) + "\nmagic path=" + context.getSystemProperty(ENV_NATIVE_LIBRARY_PATH));
            LOGGER.info(">>>library is supported ?"+AFUNIXServerSocket.isSupported());
            return;
        }
        initOSProperties();
        try {
            File targetLocation = createTempFolder();
            String native_lib_path = context.getEnvironmentVariable(NATIVE_LIB_PATH);
            if (native_lib_path != null) {
                targetLocation.delete();
                LOGGER.info("using environment configuration for NATIVE_LIBPATH=" + native_lib_path);
                targetLocation = new File(native_lib_path);
            }
            context.setSystemProperty(ENV_NATIVE_LIBRARY_PATH, targetLocation.getAbsolutePath());
            final Enumeration<URL> resources = NativeSocketLibUtil.class.getClassLoader().getResources(resourcePath);
            byte[] buffer = new byte[BUFFER_SIZE];
            while (resources.hasMoreElements()) {
                final URL resource = resources.nextElement();
                LOGGER.debug("resource[protocol='" + resource.getProtocol() + "'] : " + resource);
                if (isFile(resource)) {
                    final File asFileResource = new File(resource.getFile());
                    for (final File child : asFileResource.listFiles()) {
                        String fileName = child.getName();
                        if (isNativeLibraryFile(fileName)) {
                            copyFileWithBuffer(targetLocation, child.getName(), new FileInputStream(child), buffer);
                        }
                    }
                } else if (isJar(resource)) {
                    String archiveFileName = resource.getPath();
                    LOGGER.debug(">>RAW archiveFileName:" + archiveFileName);
                    int startIdx = "jar:".length() + 1;
                    int endIdx = archiveFileName.indexOf("!", startIdx);
                    archiveFileName = archiveFileName.substring(startIdx, endIdx);
                    LOGGER.debug(">>SUBST archiveFileName:" + archiveFileName);
                    final JarFile archive = new JarFile(URLDecoder.decode(archiveFileName, "UTF-8"));
                    Enumeration<? extends ZipEntry> entries = archive.entries();
                    while (entries.hasMoreElements()) {
                        final ZipEntry entry = entries.nextElement();
                        String fileName = entry.getName();
                        LOGGER.debug(">>>> zip entry : " + fileName + ", isFromResourcePath:" + fileName.startsWith(resourcePath)
                                + ", isNativeLibFile:" + isNativeLibraryFile(fileName));
                        if (fileName.startsWith(resourcePath) && isNativeLibraryFile(fileName)) {
                            fileName = fileName.substring(resourcePath.length() + 1);
                            copyFileWithBuffer(targetLocation, fileName, archive.getInputStream(entry), buffer);
                        }
                    }
                } else {
                    throw new RuntimeException("unknown protocol in url " + resource);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("failure retrieving native .so libraries", e);
        }
    }

    private void initOSProperties() {
        suffix = ".so";
        String os = context.getSystemProperty("os.name").replaceAll("[^A-Za-z0-9]", "").toLowerCase();
        if ("macosx".equals(os)) {
            suffix = ".dylib";
        }
        arch = context.getSystemProperty("os.arch");
    }

    private boolean isFile(final URL resource) {
        return resource.getProtocol().equals("file");
    }

    private boolean isJar(final URL resource) {
        return resource.getProtocol().equals("jar");
    }

    private boolean isNativeLibraryFile(String fileName) {
        return fileName.endsWith(suffix) && fileName.contains(arch);
    }

    public File createTempFolder() throws IOException {
        File tempFile = File.createTempFile("native", "");
        String absolutePath = tempFile.getAbsolutePath().replace('.', File.separatorChar);
        if (!tempFile.delete()) {
            tempFile.deleteOnExit();
        }
        final File tmpPath = new File(absolutePath);
        tempFolders.add(tmpPath.getAbsolutePath());
        final File libPath = new File(tmpPath, "linuxdomainsocket");
        tempFolders.add(libPath.getAbsolutePath());
        return libPath;
    }

    private void copyFileWithBuffer(File targetLocation, final String fileName, final InputStream in, byte[] buffer) throws IOException,
            FileNotFoundException {
        final File targetFile = new File(targetLocation, fileName);
        if (targetFile.exists() && targetFile.length() > 0) {
            LOGGER.info("targetfile \"" + targetFile.getAbsolutePath() + "\" exists with a length of " + targetFile.length() + " bytes.");
//            initJavaWithNativeLib(targetFile);
            return;
        }
        File targetFolder = targetFile.getParentFile();
        if (!targetFolder.exists() && !targetFolder.mkdirs()) {
            targetFile.mkdir();
        }
        final BufferedInputStream source = new BufferedInputStream(in, BUFFER_SIZE);
        final BufferedOutputStream target = new BufferedOutputStream(new FileOutputStream(targetFile), BUFFER_SIZE);
        int read = -1;
        int total = 0;
        while ((read = source.read(buffer)) > -1) {
            target.write(buffer, 0, read);
            total += read;
        }
        LOGGER.info("copied " + total + " bytes for file " + targetFile.getAbsolutePath());
        target.flush();
        target.close();
        source.close();
        tempFolders.add(targetFile.getAbsolutePath());
        //initJavaWithNativeLib(targetFile);
    }

    private void initJavaWithNativeLib(final File targetFile) {
        if (targetFile != null) {
            throw new RuntimeException("targetFile = "+targetFile);
        }
        String libName;
        try {
            libName = targetFile.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException("could not determine canonical path of " + targetFile, e);
        }
        if (!NativeLibUtil.isLibraryAlreadyLoaded(libName)) {
            LOGGER.info("loading native lib \"" + libName + "\"");
            // classloaded access ?
            try {
                System.load(targetFile.getAbsolutePath());
            } catch (final UnsatisfiedLinkError ule) {
                LOGGER.error("failed to load native library '" + targetFile.getAbsolutePath() + "'", ule);
            }
        } else {
            LOGGER.info("native library \"" + libName + "\" has already been loaded. current Environment="
                    + context.getSystemProperty(PROP_LIBRARY_LOADED));
            if (context.getSystemProperty(PROP_LIBRARY_LOADED) == null) {
                LOGGER.warn("library has been loaded but environment is not set. Setting '"+PROP_LIBRARY_LOADED+" to "+libName);
                context.setSystemProperty(PROP_LIBRARY_LOADED, libName);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        LOGGER.info("resetting properties");
        cleanUpTemporaryEnvironmentFiles();
        super.finalize();
    }

    public void resetCache() {
        LOGGER.info("*** resetting properties");//check who (wich test) is not resetting cache!
        cleanUpTemporaryEnvironmentFiles();
        context.setSystemProperty(PROP_LIBRARY_LOADED, null);
        context.setSystemProperty(ENV_NATIVE_LIBRARY_PATH, null);
    }

    public void releaseSocketFile(String file) {
        if (file == null) {
            return;
        }
        final File socketFile = new File(file);
        if (!socketFile.exists()) {
            return;
        }
        if (!socketFile.delete()) {
            socketFile.deleteOnExit();
        }
    }

    private void cleanUpTemporaryEnvironmentFiles() {
        releaseSocketFile(context.getSystemProperty(PROP_LIBRARY_LOADED));
        releaseSocketFile(context.getSystemProperty(ENV_NATIVE_LIBRARY_PATH));
        for (String absPath : tempFolders) {
            releaseSocketFile(absPath);
        }
    }
}
