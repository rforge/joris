package org.rosuda.util.java.file.search;

import java.io.File;
import java.util.List;

import org.rosuda.util.java.file.FileMatcher;
import org.rosuda.util.java.file.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractFileSearchStrategy implements FileSearchStrategy {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractFileSearchStrategy.class);
    protected final FileSystem fileSystem;

    protected AbstractFileSearchStrategy(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public void searchFor(FileMatcher matcher, List<File> matchingFiles) {
        if (matchingFiles.isEmpty()) {
            LOGGER.info("searching file '" + matcher.describe() + "' by " + this.getClass().getSimpleName());
            handleSearchFor(matcher, matchingFiles);
        }
    }

    protected abstract void handleSearchFor(FileMatcher matcher, List<File> matchingFiles);

    protected void scanFoldersUntilMatched(File[] folders, FileMatcher matcher, List<File> fileList) {
        if (folders == null) {
            return;
        }
        for (final File child : folders) {
            LOGGER.debug("searching for '" + matcher.describe() + " in " + child.getAbsolutePath());
            if (!fileList.isEmpty()) {
                break;
            }
            if (child.isFile() && matcher.matches(child)) {
                fileList.add(child);
            } else if (child.isDirectory()) {
                scanFoldersUntilMatched(child.listFiles(), matcher, fileList);
            }
        }
    }
}
