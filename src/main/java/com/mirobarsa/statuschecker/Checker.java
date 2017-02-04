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

    public Checker(Properties prop) {
        this.streamUrl = prop.getProperty("STREAM");
        this.proc = prop.getProperty("BUTTPATH");
        this.stream = new UrlStatus(prop.getProperty("SERVERURL") + "/" + prop.getProperty("STATUS"));
        this.server = new UrlStatus(prop.getProperty("SERVERURL"));
        this.exe = new Executer();
    }

    public void check(StreamState st) {
        if (stream.isStreamDown(streamUrl)) {
            st.setStreamState(StreamState.StrmState.STREAM_DOWN);
            if (server.isServerUp()) {
                st.setSrvState(StreamState.SrvState.SERVER_UP);
                try {
                    if (Butt.getIstance().getButtProc() != null && Butt.getIstance().getButtProc().isAlive()) {
                        if (stream.isStreamDown(streamUrl)) {
                            logger.info("Uccido butt");
                            Butt.getIstance().getButtProc().destroyForcibly().waitFor();
                        }
                    }
                    logger.info("Lancio butt");
                    Butt.getIstance().setButtProc(exe.execute(proc));
                } catch (IOException | InterruptedException ex) {
                    logger.error(ex);
                }
            } else {
                st.setSrvState(StreamState.SrvState.SERVER_DOWN);
            }
        } else {
            st.setSrvState(StreamState.SrvState.SERVER_UP);
            st.setStreamState(StreamState.StrmState.STREAM_UP);
        }
    }

}
