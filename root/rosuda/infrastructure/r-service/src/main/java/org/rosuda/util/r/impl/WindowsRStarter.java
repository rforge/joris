package org.rosuda.util.r.impl;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.RunStateHolder;


class WindowsRStarter extends AbstractRStarter {
	
	private static final Log LOG = LogFactory.getLog(WindowsRStarter.class);
	
	public static final String rserve = "Rserve";
	public static final String rserveX64 = "Rserve_x64";
	private String usedRserveExecutable;
		
	WindowsRStarter(final RunStateHolder<IRConnection> runStateHolder, final RStartContext setup) {
		super(runStateHolder, setup);
	}

	@Override
	void initRFileLocations(final List<File> list) {
		list.clear();
		LOG.info("searching for r.exe");
		final FileFilter r_exe = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isFile() && file.getName().toLowerCase().equals("r.exe");
			}
		};
		final Matcher<String> r_exe_tokenMatcher = new Matcher<String>() {
			@Override
			public boolean matches(String typeInstance) {
				return typeInstance.endsWith(System.getProperty("file.separator")+"r.exe");
			}
			@Override
			public String toString() {
				return "any string matching r.exe ignoring case";
			}
		};
		fillFileListWhereRulesMatchWhileNoEntryHasBeenFound(list, r_exe, r_exe_tokenMatcher);
		if (list.isEmpty()) {
			LOG.error("no r.exe found in PATH or USER environment. Please install R and have your path environment point to your R installation!");
		} else {
			LOG.info("use r.exe : \""+list.get(0).getAbsolutePath()+"\"");
		}
		LOG.info("searching rserve");
		final boolean is_64_bit = (list.get(0).getAbsolutePath().contains("x64"));
		final FileFilter r_serve_exe = new FileFilter() {
			@Override
			public boolean accept(File file) {
				final String filename = file.getName().toLowerCase();
				final String path = file.getParentFile().getAbsolutePath().toLowerCase();
				if (is_64_bit)
					return file.isFile() && (filename.contains("64") || path.contains("64")) && filename.contains("rserve") && filename.endsWith(".exe");
				return file.isFile() && filename.contains("rserve") && filename.endsWith(".exe");
			}
			@Override
			public String toString() {
				if (is_64_bit)
					return "any string containing '64' and 'rserve' ending with '.exe' ignoring case";
				return "any string containing 'rserve' ending with '.exe' ignoring case";
			}
		};
		final Matcher<String> rTokenMatcher = new Matcher<String>() {
			@Override
			public boolean matches(String typeInstance) {
				return typeInstance.contains(rserve);
			}
			
		};
		final ArrayList<File> rserveList = new ArrayList<File>();
		fillFileListWhereRulesMatchWhileNoEntryHasBeenFound(rserveList, r_serve_exe, rTokenMatcher);
		if (!rserveList.isEmpty()) {
			usedRserveExecutable = rserveList.get(0).getAbsolutePath();
			LOG.info("chose r serve executable \""+usedRserveExecutable+"\"");
		} else {
			LOG.error("no rserve.exe/rserve_64.exe found in PATH or USER environment. Please install R and RServe, and have your path environment point to your RServe installation!");
		}
	}


	@Override
	String[] getRuntimeArgs(final String executableRFile) {
		if (usedRserveExecutable == null)
			throw new NullPointerException("Rserve.exe was not found.");
		return new String[]{
			executableRFile,
			"CMD",
			usedRserveExecutable, 
			R_SERVE_ARGS
		};
	}

	// -- helper
	private void fillFileListWhereRulesMatchWhileNoEntryHasBeenFound(final List<File> list, final FileFilter fileMatcher, final Matcher<String> pathTokenMatcher) {
		searchInPathList(getRPathFromPathEnvironment(pathTokenMatcher), list, fileMatcher);
		searchInPathList(Collections.singletonList(new File(System.getProperty("user.home"))), list, fileMatcher);
		searchInPathList(Collections.singletonList(new File(System.getenv("ProgramFiles"))), list, fileMatcher);
		searchInPathList(Collections.singletonList(new File(System.getenv("ProgramFiles") +" (x86)")), list, fileMatcher);
		searchInPathList(getRootFolders(), list, fileMatcher);
		Collections.sort(list, new FileDateComparator());
	}
	
	
	private List<File> getRPathFromPathEnvironment(final Matcher<String> pathTokenMatcher) {
		final List<File> searchPaths = new ArrayList<File>();
		final StringTokenizer rPathTokenizer = new StringTokenizer(System.getProperty("java.library.path"),System.getProperty("path.separator"));
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
			if (log.isDebugEnabled()) {
				log.debug("skipping "+paths);
			}
			return;
		} else if (log.isDebugEnabled()) {
			log.debug("scanning recursively "+paths);
		}
		for (final File searchPath : paths) {
			scanForRServeInstallations(searchPath, list, fileMatcher);
		}
	}
	private void scanForRServeInstallations(File folder,
			List<File> matchingRServeFiles, final FileFilter fileMatcher) {
		if (folder == null || !folder.isDirectory()) {
			return;
		}
		if (!folder.canRead()) {
			LOG.warn("cannot read folder "+folder.getAbsolutePath());
			return;
		}
		final File[] children = folder.listFiles();
		if (children == null) {
			return;
		}
		for (final File file : children) {
			if (fileMatcher.accept(file)) {
				if (log.isDebugEnabled()) {
					log.debug("found " +fileMatcher.toString()+ " : "+file.getAbsolutePath());
				}
				matchingRServeFiles.add(file);
			}
			else if (file.isDirectory()) {
				scanForRServeInstallations(file, matchingRServeFiles, fileMatcher);
			}
		}
		return;
	}

	
	private Iterable<File> getRootFolders() {
		return Arrays.asList(File.listRoots());
	}
}
