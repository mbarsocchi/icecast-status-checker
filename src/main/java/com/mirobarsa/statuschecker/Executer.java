package com.mirobarsa.statuschecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author mbarsocchi
 */
public class Executer {

    private final Runtime rt;

    public Executer() {
        this.rt = Runtime.getRuntime();
    }

    public Process execute(String command) throws IOException {
        Process proc = this.rt.exec(command);
        return proc;
    }

    public boolean isRunning(String processName) throws IOException {
        boolean result = false;
        Process p = null;
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            p = this.execute("tasklist /fi \"ImageName eq " + processName + ".exe\"");
        } else {
            p = this.execute("ps -efa | grep " + processName + "|grep -v grep");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(processName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void kill(String processName) throws IOException {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            this.execute("taskkill /F /IM " + processName + ".exe ");
        } else {
            this.execute("kill -9 | pidof " + processName);
        }
    }

}
