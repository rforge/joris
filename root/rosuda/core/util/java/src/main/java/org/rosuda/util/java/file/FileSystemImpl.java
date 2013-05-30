package org.rosuda.util.java.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class FileSystemImpl implements FileSystem {

    @Override
    public File[] listRoots() {
        return File.listRoots();
    }

    @Override
    public File getChild(File parent, String fileName) {
        return new File(parent, fileName);
    }

    @Override
    public File[] listFilesFromHomeDirectory() {
        final File fileRoot = new File(System.getProperty("user.home"));
        return fileRoot.listFiles();

    }

    @Override
    public Iterable<File> pathelements() {
        final String environmentPath = System.getenv("PATH");
        if (environmentPath != null) {
            final StringTokenizer pathTokenizer = new StringTokenizer(environmentPath, File.pathSeparator, false);
            return new Iterable<File>() {

                @Override
                public Iterator<File> iterator() {
                    return new Iterator<File>() {

                        @Override
                        public boolean hasNext() {
                            return pathTokenizer.hasMoreTokens();
                        }

                        @Override
                        public File next() {
                            return new File(pathTokenizer.nextToken());
                        }

                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };

        }
        return new ArrayList<File>();
    }
}
