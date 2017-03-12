package com.mirobarsa.statuschecker;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 *
 * @author mbarsocchi
 */
class Checker {

    static final Logger logger = Logger.getLogger(Checker.class.getName());

    private final String streamUrl;
    private final Stream stream;
    private final Executer exe;
    private final String proc;
    private int retryNumber;
    private final int maxRetry;

    public Checker(Properties prop) {
        this.retryNumber = 0;
        this.exe = new Executer();
        this.stream = new Stream(prop.getProperty("STREAM"),prop.getProperty("SERVERURL"), prop.getProperty("STATUS"),prop.getProperty("INTERNETOK"));
        this.streamUrl = prop.getProperty("STREAM");
        this.proc = prop.getProperty("BUTTPATH");
        if (prop.getProperty("MAX_RETRY") != null) {
            maxRetry = Integer.parseInt(prop.getProperty("MAX_RETRY"));
        } else {
            maxRetry = 3;
        }
    }

    public void check() {
        if (stream.isStreamDown(streamUrl)) {
            if (stream.isServerUrlOk()) {
                if (retryNumber >= maxRetry || stream.getStreamState().equals(StreamState.StrmState.STARTING)) {
                    try {
                        if (Butt.getIstance().getButtProc() != null && Butt.getIstance().getButtProc().isAlive()) {
                            if (stream.isStreamDown(streamUrl)) {
                                logger.info("Restart butt");
                                Butt.getIstance().getButtProc().destroyForcibly().waitFor();
                                Butt.getIstance().setButtProc(exe.execute(proc));
                            } else {
                                retryNumber = 0;
                                stream.setStreamState(StreamState.StrmState.STREAM_UP);
                            }
                        } else {
                            logger.info("Start butt");
                            Butt.getIstance().setButtProc(exe.execute(proc));
                        }
                    } catch (IOException | InterruptedException ex) {
                        logger.error(ex);
                    }
                } else {
                    logger.info("Stream Down. Tentativo numero " + retryNumber);
                    retryNumber++;
                }
            } else {
                stream.checkTraceRoute(exe);
            }
        } else {
            retryNumber = 0;
        }
    }

}
