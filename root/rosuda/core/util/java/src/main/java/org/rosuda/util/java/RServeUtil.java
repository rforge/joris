package org.rosuda.util.java;

import java.io.IOException;

public class RServeUtil {

    public static void killAllWindowsRProcesses() {
        try {
            Runtime.getRuntime().exec("taskkill /F /im Rserve.exe");

        } catch (IOException e) {
        }
    }

    public static void killAllUXRProcesses() {
        try {
            Runtime.getRuntime().exec("pkill Rserve");

        } catch (IOException e) {
        }
    }

}
