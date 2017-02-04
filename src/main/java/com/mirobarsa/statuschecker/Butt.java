package com.mirobarsa.statuschecker;

/**
 *
 * @author mbarsocchi
 */
public class Butt {

    private Process buttProc = null;
    private static Butt istance = null;

    private Butt() {
    }

    public static Butt getIstance() {
        if (istance == null) {
            istance = new Butt();
        }
        return istance;
    }

    public Process getButtProc() {
        return buttProc;
    }

    public void setButtProc(Process buttProc) throws InterruptedException {
        this.buttProc = buttProc;
    }
}
