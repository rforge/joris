package org.rosuda.util.java.file.search;

import java.io.File;
import java.util.List;

import org.rosuda.util.java.file.FileSystem;
import org.rosuda.util.java.file.FilenameMatcher;

public class HomeFolderFileSearchStrategy extends AbstractFileSearchStrategy {

    public HomeFolderFileSearchStrategy(FileSystem fileSystem) {
        super(fileSystem);
    }

    @Override
    protected void handleSearchFor(FilenameMatcher matcher, List<File> matchingFiles) {
        scanFoldersUntilMatched(fileSystem.listFilesFromHomeDirectory(), matcher, matchingFiles);
    }

}
