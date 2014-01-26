package org.rosuda.util.r.impl;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.RunStateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WindowsRStarter extends AbstractRStarter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WindowsRStarter.class);
    private static final String FILE_R_EXE = "file.r.exe";
    private static final String FILE_RSERVE_EXE = "file.rserve.exe";

    public static final String rserve = "Rserve";
    public static final String rserveX64 = "Rserve_x64";

    private static final FileFilter r_exe = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isFile() && file.getName().toLowerCase().equals("r.exe");
        }
    };

    private static final Matcher<String> r_exe_tokenMatcher = new Matcher<String>() {
        @Override
        public boolean matches(String typeInstance) {
            return typeInstance.endsWith(System.getProperty("file.separator") + "r.exe");
        }

        @Override
        public String toString() {
            return "any string matching r.exe ignoring case";
        }
    };

    private final class RServeExeFileFilter implements FileFilter {

        private final boolean is_64_bit;

        private RServeExeFileFilter(final boolean is_64_bit) {
            this.is_64_bit = is_64_bit;
        }

        @Override
        public boolean accept(File file) {
            final String filename = file.getName().toLowerCase();
            final String path = file.getParentFile().getAbsolutePath().toLowerCase();
            boolean valid = is_64_bit
                    && (file.isFile() && (filename.contains("64") || path.contains("64")) && filename.contains("rserve") && filename
                            .endsWith(".exe")) || !is_64_bit && (file.isFile() && filename.contains("rserve") && filename.endsWith(".exe"));
            return valid;
        }

        @Override
        public String toString() {
            if (is_64_bit)
                return "any string containing '64' and 'rserve' ending with '.exe' ignoring case";
            return "any string containing 'rserve' ending with '.exe' ignoring case";
        }
    };

    private static final Matcher<String> r_serve_token_matcher = new Matcher<String>() {
        @Override
        public boolean matches(String typeInstance) {
            return typeInstance.contains(rserve);
        }

    };
    private static final Preferences winRStarterPrefs = initPreferences();

    private String usedRserveExecutable;

    WindowsRStarter(final RunStateHolder<IRConnection> runStateHolder, final RStartContext setup) {
        super(runStateHolder, setup);
    }

    private static Preferences initPreferences() {
        final Preferences userRoot = Preferences.userRoot();
        // check if a newer version of R might be in the classpath
        if (newOrInvalidRExecReferencedFromPreference(userRoot.get(FILE_R_EXE, ""))) {
            userRoot.put(FILE_R_EXE, "");
            userRoot.put(FILE_RSERVE_EXE, "");
            LOGGER.info("resetting R preferences as a new version of R might be available");
            try {
                userRoot.sync();
            } catch (BackingStoreException e) {
                LOGGER.error("could not reset preferences", e);
            }
        }
        return userRoot;
    }

    private static boolean newOrInvalidRExecReferencedFromPreference(final String currentRPath) {
        if (currentRPath == "")
            return false;
        final List<File> matches = getRPathFromPathEnvironment(r_exe_tokenMatcher);
        if (!matches.isEmpty()) {
            Collections.sort(matches, new FileDateComparator());
            final File currentRFile = new File(currentRPath);
            if (currentRFile == null || !currentRFile.exists() || !currentRFile.canExecute()) {
                return true;
            }
            return matches.get(0).lastModified() > currentRFile.lastModified();
        }
        return false;
    }

    private abstract class FileProvider {

        protected final String getLocation(final List<File> fileList, final String preferenceName) {
            final String preferenceValue = winRStarterPrefs.get(preferenceName, "");
            if (!"".equals(preferenceValue)) {
                final File preferredFile = new File(preferenceValue);
                if (preferredFile.exists()) {
                    fileList.add(preferredFile);
                    LOGGER.info("using \"" + preferenceValue + "\" as . configured in the user preferences");
                    return preferenceValue;
                } else {
                    LOGGER.warn("preferred file \"" + preferenceValue + "\" is invalid!");
                }
            }
            final String winRExeFilePath = handleFind(fileList);
            if (winRExeFilePath != null) {
                winRStarterPrefs.put(preferenceName, winRExeFilePath);
                try {
                    winRStarterPrefs.sync();
                } catch (final BackingStoreException e) {
                    LOGGER.error("could not store preference path for r.exe", e);
                }
                return winRExeFilePath;
            } else {
                return null;
            }
        }

        protected abstract String handleFind(final List<File> fileList);
    }

    private final class WinRExeFileProvider extends FileProvider {

        @Override
        protected String handleFind(List<File> fileList) {
            LOGGER.info("searching for r.exe");
            fillFileListWhereRulesMatchWhileNoEntryHasBeenFound(fileList, r_exe, r_exe_tokenMatcher);
            if (fileList.isEmpty()) {
                LOGGER.error("no r.exe found in PATH or USER environment. Please install R and have your path environment point to your R installation!");
                return null;
            } else {
                final String winRExeFilePath = fileList.get(0).getAbsolutePath();
                LOGGER.info("use r.exe : \"" + winRExeFilePath + "\"");
                return winRExeFilePath;
            }
        }
    }

    private final class WinRserveFileProvider extends FileProvider {

        @Override
        protected String handleFind(List<File> fileList) {
            LOGGER.info("searching rserve");
            String winRExeFilePath = fileList.get(0).getAbsolutePath();
            // first from fileList
            final boolean is_64_bit = (winRExeFilePath.contains("x64"));
            final ArrayList<File> rserveList = new ArrayList<File>();
            fillFileListWhereRulesMatchWhileNoEntryHasBeenFound(rserveList, new RServeExeFileFilter(is_64_bit), r_serve_token_matcher);
            if (!rserveList.isEmpty()) {
                usedRserveExecutable = rserveList.get(0).getAbsolutePath();
                LOGGER.info("chose r serve executable \"" + usedRserveExecutable + "\"");
                return usedRserveExecutable;
            } else {
                LOGGER.error("no rserve.exe/rserve_64.exe found in PATH or USER environment. Please install R and RServe, and have your path environment point to your RServe installation!");
                return null;
            }
        }

    }

    @Override
    void initRFileLocations(final List<File> list) {
        list.clear();
        final String rExeFile = new WinRExeFileProvider().getLocation(list, FILE_R_EXE);
        if (rExeFile != null) {
            this.usedRserveExecutable = new WinRserveFileProvider().getLocation(list, FILE_RSERVE_EXE);
        }
    }

    @Override
    String[] getRuntimeArgs(final String executableRFile) {
        if (usedRserveExecutable == null)
            throw new NullPointerException("Rserve.exe was not found.");
        return new String[] { executableRFile, "CMD", usedRserveExecutable, R_SERVE_ARGS };
    }

    @Override
    protected boolean isBlocking() {
        return true;
    }

    // -- helper
    private void fillFileListWhereRulesMatchWhileNoEntryHasBeenFound(final List<File> list, final FileFilter fileMatcher,
            final Matcher<String> pathTokenMatcher) {
        searchInPathList(getRPathFromPathEnvironment(pathTokenMatcher), list, fileMatcher);
        searchInPathList(Collections.singletonList(new File(System.getProperty("user.home"))), list, fileMatcher);
        searchInPathList(Collections.singletonList(new File(System.getenv("ProgramFiles"))), list, fileMatcher);
        searchInPathList(Collections.singletonList(new File(System.getenv("ProgramFiles") + " (x86)")), list, fileMatcher);
        Iterable<File> rootFolders = getRootFolders();
        for (final File file : rootFolders) {
            searchInPathList(Collections.singletonList(file), list, fileMatcher);
        }
        Collections.sort(list, new FileDateComparator());
    }

    private static final List<File> getRPathFromPathEnvironment(final Matcher<String> pathTokenMatcher) {
        final List<File> searchPaths = new ArrayList<File>();
        final StringTokenizer rPathTokenizer = new StringTokenizer(System.getProperty("java.library.path"),
                System.getProperty("path.separator"));
        while (rPathTokenizer.hasMoreTokens()) {
            final String pathToken = rPathTokenizer.nextToken();
            if (pathTokenMatcher.matches(pathToken)) {
                searchPaths.add(new File(pathToken));
            }
        }
        return searchPaths;
    }

    private void searchInPathList(final Iterable<File> paths, final List<File> list, final FileFilter fileMatcher) {
        if (!list.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("skipping " + paths);
            }
            return;
        } else if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("scanning recursively " + paths);
        }
        for (final File searchPath : paths) {
            scanForRServeInstallations(searchPath, list, fileMatcher);
        }
    }

    private void scanForRServeInstallations(File folder, List<File> matchingRServeFiles, final FileFilter fileMatcher) {
        if (folder == null || !folder.isDirectory()) {
            return;
        }
        if (!folder.canRead()) {
            LOGGER.warn("cannot read folder " + folder.getAbsolutePath());
            return;
        }
        final File[] children = folder.listFiles();
        if (children == null) {
            return;
        }
        for (final File file : children) {
            if (fileMatcher.accept(file)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("found " + fileMatcher.toString() + " : " + file.getAbsolutePath());
                }
                matchingRServeFiles.add(file);
            } else if (file.isDirectory()) {
                scanForRServeInstallations(file, matchingRServeFiles, fileMatcher);
            }
        }
        return;
    }

    private Iterable<File> getRootFolders() {
        return Arrays.asList(File.listRoots());
    }
}
