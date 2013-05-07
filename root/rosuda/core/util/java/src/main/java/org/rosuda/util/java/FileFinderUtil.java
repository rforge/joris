package org.rosuda.util.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileFinderUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(FileFinderUtil.class);
    
    public static List<File> findFileByName(final String fileName) {
        final FileFinderUtil util = new FileFinderUtil();
        final List<File> list = new ArrayList<File>();
        util.scanPathAndAddToFileList(fileName, list);
        if (list.isEmpty()) {
            util.scanFileSystemForFile(fileName, list);
        }
        return Collections.unmodifiableList(list);
    }
    
    void scanPathAndAddToFileList(final String fileName, final List<File> list) {
        final String environmentPath = System.getenv("PATH");
        if (environmentPath != null) {
            final StringTokenizer pathTokenizer = new StringTokenizer(environmentPath, File.pathSeparator, false);
            while (pathTokenizer.hasMoreTokens()) {
                final String path = pathTokenizer.nextToken();
                final File match = new File(path, fileName);
                LOGGER.debug("searching file '"+fileName+"' in "+path);
                if (match.exists()) {
                    LOGGER.info("found file matching fileName '"+fileName+"' : "+match.getAbsolutePath());                    
                    list.add(match);
                }
            }
        }
    }

    void scanFileSystemForFile(final String fileName, final List<File> file) {
        //TODO .. all mount points/directories
        File[] folders = new File("/").listFiles();
        scanFoldersUntilMatched(folders, fileName, file);
    }

    private void scanFoldersUntilMatched(File[] folders, String fileName, List<File> fileList) {
        for (final File child: folders) {
            LOGGER.debug("searching for '"+fileName+" in "+child.getAbsolutePath());
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
