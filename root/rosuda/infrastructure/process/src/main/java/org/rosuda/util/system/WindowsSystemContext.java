package org.rosuda.util.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WindowsSystemContext implements SystemContext {

    @Override
    public Collection<String> runningProcesses(String taskname) {
        final List<String> processes = new ArrayList<String>();
        try {
            final Process tasklistprocess = Runtime.getRuntime().exec(
                    new String[] { "cmd.exe", "/c", "tasklist.exe /v /fo csv /nh |find \"" + taskname + "\"" });
            final BufferedReader readFromProcess = new BufferedReader(new InputStreamReader(tasklistprocess.getInputStream()));
            String line;
            while ((line = readFromProcess.readLine()) != null) {
                processes.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
        return processes;
    }
}
