package org.rosuda.util.java.file.search;

import java.io.File;
import java.util.List;

import org.rosuda.util.java.file.FileSystem;
import org.rosuda.util.java.file.FilenameMatcher;

public class PathChildrenFileSearchStrategy extends AbstractFileSearchStrategy {

    public PathChildrenFileSearchStrategy(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    protected void handleSearchFor(FilenameMatcher matcher, List<File> matchingFiles) {
        for (final File path : fileSystem.pathelements()) {
            if (matchingFiles.isEmpty()) {
                scanFoldersUntilMatched(path.listFiles(), matcher, matchingFiles);
            } else {
                LOGGER.debug("skipping \"" + path.getAbsolutePath() + "\".");
            }
        }
    }

}
