package org.rosuda.util.java.file.search;

import java.io.File;
import java.util.List;

import org.rosuda.util.java.file.FileSystem;
import org.rosuda.util.java.file.FileMatcher;

public class FlatPathElementCompletionStrategy extends AbstractFileSearchStrategy {

    public FlatPathElementCompletionStrategy(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    protected void handleSearchFor(FileMatcher matcher, List<File> matchingFiles) {
        for (final File path : fileSystem.pathelements()) {
            final String perfectMatch = matcher.perfectMatch();
            if (perfectMatch != null) {
                final File match = fileSystem.getChild(path, perfectMatch);
                LOGGER.debug("searching file '" + matcher.describe() + "' in " + path);
                if (match != null && match.exists()) {
                    LOGGER.info("found file matching fileName '" + matcher.describe() + "' : " + match.getAbsolutePath());
                    matchingFiles.add(match);
                }
            }
        }

    }

}
