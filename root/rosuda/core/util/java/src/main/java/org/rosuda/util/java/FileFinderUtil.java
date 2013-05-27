package org.rosuda.util.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileFinderUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(FileFinderUtil.class);

    public static List<File> findFileByName(final String fileName) {
        final FileFinderUtil util = new FileFinderUtil();
        final List<File> list = new ArrayList<File>();
        LOGGER.info("searching file '" + fileName + "' in PATH");
        util.scanPathAndAddToFileList(fileName, list);
        if (list.isEmpty()) {
            LOGGER.info("searching file '" + fileName + "' in HOME");
            util.scanHomeFolderForFile(fileName, list);
        }
        if (list.isEmpty()) {
            LOGGER.info("searching file '" + fileName + "' in PATH CHILDREN");
            util.scanPathChildrenForFile(fileName, list);
        }
        if (list.isEmpty()) {
            LOGGER.info("searching file '" + fileName + "' in FILE SYSTEM");
            util.scanFileSystemForFile(fileName, list);
        }
        return Collections.unmodifiableList(list);
    }

    private void scanPathAndAddToFileList(final String fileName, final List<File> list) {
        for (final String path : pathelements()) {
            final File match = new File(path, fileName);
            LOGGER.debug("searching file '" + fileName + "' in " + path);
            if (match.exists()) {
                LOGGER.info("found file matching fileName '" + fileName + "' : " + match.getAbsolutePath());
                list.add(match);
            }
        }
    }

    void scanFileSystemForFile(final String fileName, final List<File> fileList) {
        LOGGER.debug("\"" + fileName + "\" not found in home or paths, scanning from root.");
        scanFoldersUntilMatched(File.listRoots(), fileName, fileList);
    }

    private void scanHomeFolderForFile(String fileName, List<File> fileList) {
        final File fileRoot = new File(System.getProperty("user.home"));
        scanFoldersUntilMatched(fileRoot.listFiles(), fileName, fileList);
    }

    private void scanPathChildrenForFile(String fileName, List<File> fileList) {
        for (final String path : pathelements()) {
            final File fileRoot = new File(path);
            if (fileList.isEmpty()) {
                scanFoldersUntilMatched(fileRoot.listFiles(), fileName, fileList);
            } else {
                LOGGER.debug("skipping \"" + fileRoot.getAbsolutePath() + "\".");
            }
        }
    }

    private Iterable<String> pathelements() {
        final String environmentPath = System.getenv("PATH");
        if (environmentPath != null) {
            final StringTokenizer pathTokenizer = new StringTokenizer(environmentPath, File.pathSeparator, false);
            return new Iterable<String>() {

                @Override
                public Iterator<String> iterator() {
                    return new Iterator<String>() {

                        @Override
                        public boolean hasNext() {
                            return pathTokenizer.hasMoreTokens();
                        }

                        @Override
                        public String next() {
                            return pathTokenizer.nextToken();
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };

        }
        return new ArrayList<String>();
    }

    private void scanFoldersUntilMatched(File[] folders, String fileName, List<File> fileList) {
        if (folders == null) {
            return;
        }
        for (final File child : folders) {
            LOGGER.debug("searching for '" + fileName + " in " + child.getAbsolutePath());
            if (!fileList.isEmpty()) {
                break;
            }
            if (child.isFile() && fileName.equals(child.getName())) {
                fileList.add(child);
            } else if (child.isDirectory()) {
                scanFoldersUntilMatched(child.listFiles(), fileName, fileList);
            }
        }

    }
}
