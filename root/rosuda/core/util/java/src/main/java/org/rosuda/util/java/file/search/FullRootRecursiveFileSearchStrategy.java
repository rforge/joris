package org.rosuda.util.java.file.search;

import java.io.File;
import java.util.List;

import org.rosuda.util.java.file.FileSystem;
import org.rosuda.util.java.file.FilenameMatcher;

public class FullRootRecursiveFileSearchStrategy extends AbstractFileSearchStrategy {

    public FullRootRecursiveFileSearchStrategy(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    protected void handleSearchFor(FilenameMatcher matcher, List<File> matchingFiles) {
        LOGGER.debug("\"" + matcher.describe() + "\" not found in home or paths, scanning from root.");
        scanFoldersUntilMatched(fileSystem.listRoots(), matcher, matchingFiles);
    }

}
