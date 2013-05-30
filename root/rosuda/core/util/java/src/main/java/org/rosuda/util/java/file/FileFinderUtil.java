package org.rosuda.util.java.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rosuda.util.java.file.search.FileSearchStrategy;
import org.rosuda.util.java.file.search.FlatPathElementCompletionStrategy;
import org.rosuda.util.java.file.search.FullRootRecursiveFileSearchStrategy;
import org.rosuda.util.java.file.search.HomeFolderFileSearchStrategy;
import org.rosuda.util.java.file.search.PathChildrenFileSearchStrategy;

public class FileFinderUtil {

    private FileSearchStrategy[] searchStrategies;

    public FileFinderUtil() {
        this(new FileSystemImpl());
    }

    public FileFinderUtil(final FileSystem fileSystem) {
        searchStrategies = new FileSearchStrategy[] { new FlatPathElementCompletionStrategy(fileSystem),
                new HomeFolderFileSearchStrategy(fileSystem), new PathChildrenFileSearchStrategy(fileSystem),
                new FullRootRecursiveFileSearchStrategy(fileSystem) };
    }

    public List<File> findFileByName(final String fileName) {
        return findFilesByStrategies(new EqualsFilenameMatcher(fileName));
    }

    public List<File> findFileByRegularExpression(final String fileName) {
        return findFilesByStrategies(new RegexpFilenameMatcher(fileName));
    }

    private List<File> findFilesByStrategies(final FilenameMatcher matcher) {
        final List<File> matchingFiles = new ArrayList<File>();
        for (final FileSearchStrategy searchStrategy : searchStrategies) {
            searchStrategy.searchFor(matcher, matchingFiles);
        }
        return Collections.unmodifiableList(matchingFiles);
    }

}
