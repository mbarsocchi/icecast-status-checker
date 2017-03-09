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
    private final String proc;
    private final UrlStatus stream;
    private final UrlStatus server;
    private final Executer exe;
    private int retryNumber;
    private static final int MAX_RETRY = 3;
    private final UrlStatus internet;
    private final String internetUrl;

    public Checker(Properties prop) {
        this.retryNumber = 1;
        this.streamUrl = prop.getProperty("STREAM");
        this.proc = prop.getProperty("BUTTPATH");
        this.internetUrl = prop.getProperty("INTERNETOK");
        this.stream = new UrlStatus(prop.getProperty("SERVERURL") + "/" + prop.getProperty("STATUS"));
        this.server = new UrlStatus(prop.getProperty("SERVERURL"));
        this.internet = new UrlStatus(internetUrl);
        this.exe = new Executer();
    }

    public void check(StreamState st) {
        if (stream.isStreamDown(streamUrl)) {
            st.setStreamState(StreamState.StrmState.STREAM_DOWN);
            if (server.isUrlstateOk()) {
                st.setSrvState(StreamState.SrvState.SERVER_UP);
                if (retryNumber > MAX_RETRY || st.getStreamState().equals(StreamState.StrmState.STARTING)) {
                    try {
                        if (Butt.getIstance().getButtProc() != null && Butt.getIstance().getButtProc().isAlive()) {
                            if (stream.isStreamDown(streamUrl)) {
                                logger.info("Restart butt");
                                Butt.getIstance().getButtProc().destroyForcibly().waitFor();
                                Butt.getIstance().setButtProc(exe.execute(proc));
                            } else {
                                retryNumber = 0;
                                st.setStreamState(StreamState.StrmState.STREAM_UP);
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
                st.setSrvState(StreamState.SrvState.SERVER_DOWN);
                if (!internet.isUrlstateOk()) {
                    logger.info("Errore di connessione internet");
                } else {
                    logger.info("Internet OK, url [" + internetUrl + "] response is 200");
                }
            }
        } else {
            retryNumber = 0;
            st.setStreamState(StreamState.StrmState.STREAM_UP);
        }
    }

}
