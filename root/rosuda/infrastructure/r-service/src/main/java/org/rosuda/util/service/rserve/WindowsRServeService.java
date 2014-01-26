package org.rosuda.util.service.rserve;

import org.rosuda.util.java.file.FileFinderUtil;
import org.rosuda.util.process.ShellContext;

//Namenskonvention fuer OS Spezifische Services ?
public class WindowsRServeService implements RServeService {

    public WindowsRServeService(ShellContext shellContext) {
        // wegen port, etc .. => mag einen ShellContextAdapter!
    }

    @Override
    public String[] getRuntimeArgs() {
        final FileFinderUtil fileFinderUtil = new FileFinderUtil();
        String rExecutable = fileFinderUtil.findFileByName("R.exe").get(0).getAbsolutePath(); // "/usr/lib/R/bin/R";
        String rServePattern = "(.*)i386(.*)Rserve\\.exe";
        if (rExecutable.contains("x64")) {
            rServePattern = "x64(.*)Rserve\\.exe";
        }
        String rServeBinary = fileFinderUtil.findFileByRegularExpression(rServePattern).get(0).getAbsolutePath(); // "/home/ralf/R/x86_64-pc-linux-gnu-library/2.14/Rserve/libs/Rserve-bin.so";
        return new String[] { rExecutable, "CMD", rServeBinary, R_SERVE_ARGS };
    }

}
