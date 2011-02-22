package org.rosuda.util.r.impl;

import java.io.File;
import java.util.List;

import org.rosuda.irconnect.IRConnection;
import org.rosuda.util.process.RunStateHolder;


class WindowsRStarter extends AbstractRStarter {
	
	final static String[] checkedLocations = new String[]{
		"C:\\Program Files\\R\\R-2.12.1\\bin\\x64\\R",
		"R",
	};
	
	public static final String rserve = "Rserve";
	public static final String rserveX64 = "Rserve_x64";
	
	private String usedRserveExecutable;
	
	WindowsRStarter(RunStateHolder<IRConnection> runStateHolder) {
		super(runStateHolder);
	}

	@Override
	void initRFileLocations(final List<File> list) {
		//find pattern
		final File rRootDir = new File("C:\\Program Files\\R\\");
		if (rRootDir.exists()&&rRootDir.isDirectory()) {
			//find newest R version
			File newestVersion = null;
			int version = 0;
			
			for (File child: rRootDir.listFiles()) {
				if (!child.isDirectory())
					continue;
				//parse name
				if (!child.getName().startsWith("R-"))
					continue;
				//parse version
				
				int currentVersion = 0;
				final String[] versionString = child.getName().split("-")[1].split("\\.");
				for (int i = 0; i < versionString.length; i++) {
					try {
						int v = Integer.parseInt(versionString[i]);
						currentVersion += (int) (Math.pow(100, versionString.length - i +1) * (double) v);
					} catch (NumberFormatException numf) {}
				}
				if (currentVersion > version) {
					version = currentVersion;
					newestVersion = child;
				}
			}
			if (newestVersion != null) {
				log.info("current R version "+newestVersion.getName());
				//check if /bin/X64 exists
				File rExecutable = new File(newestVersion, "bin\\x64\\");
				if (rExecutable.exists()) {
					//search for Rserve here
					File rserveExec = new File(rExecutable,rserveX64+".exe");
					if (rserveExec.exists()) {
						usedRserveExecutable = rserveX64;
					} else {
						rserveExec = new File(rExecutable, rserve+".exe");
						if (rserveExec.exists())
							usedRserveExecutable = rserve;
					}
					rExecutable = new File(rExecutable, "R.exe");
					if (usedRserveExecutable!=null&&(rExecutable==null||!rExecutable.exists())) {
						throw new RuntimeException("64bit windows R.exe not found in "+rExecutable.getAbsolutePath());
					}
				} else {
					rExecutable = new File (newestVersion, "bin\\R.exe");
					if (!rExecutable.exists()) {
						throw new RuntimeException("32bit windows R.exe not found in "+rExecutable.getAbsolutePath());
					}
					File rserveExec = new File(newestVersion,"bin\\"+rserve+".exe");
					if (rserveExec == null || !rserveExec.exists()) {
						throw new RuntimeException("Rserve.exe not found in "+rserveExec.getAbsolutePath());
					}
					usedRserveExecutable = rserve;
				}
				list.clear();
				list.add(rExecutable);
			}
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

}
