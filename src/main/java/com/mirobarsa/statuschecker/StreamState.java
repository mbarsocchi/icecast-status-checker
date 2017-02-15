package com.mirobarsa.statuschecker;

import org.apache.log4j.Logger;

/**
 *
 * @author mbarsocchi
 */
public class StreamState {

    static final Logger logger = Logger.getLogger(StreamState.class.getName());

    private StrmState streamState = StrmState.STARTING;
    private SrvState srvState = SrvState.STARTING;

    public enum StrmState {

        STARTING,
        STREAM_UP,
        STREAM_DOWN;
    }

    public enum SrvState {

        STARTING,
        SERVER_UP,
        SERVER_DOWN;
    }

    private void logStream(StrmState state, StrmState newState) {
        if ((state.equals(StrmState.STREAM_UP) || state.equals(StrmState.STARTING))
                && newState.equals(StrmState.STREAM_DOWN)) {
            logger.info("Stream is down");
        } else if ((state.equals(StrmState.STREAM_DOWN) || state.equals(StrmState.STARTING))
                && newState.equals(StrmState.STREAM_UP)) {
            logger.info("Stream is up");
        }
    }

    private void logServer(SrvState state, SrvState newSrvState) {
        if ((state.equals(SrvState.SERVER_UP) || state.equals(SrvState.STARTING)) && newSrvState.equals(SrvState.SERVER_DOWN)) {
            logger.info("Server is down");
        } else if ((state.equals(SrvState.SERVER_DOWN) || state.equals(SrvState.STARTING)) && newSrvState.equals(SrvState.SERVER_UP)) {
            logger.info("Server is up");
        }
    }

    public StrmState getStreamState() {
        return streamState;
    }

    public void setStreamState(StrmState newStreamState) {
        logStream(streamState, newStreamState);
        this.streamState = newStreamState;
        if (newStreamState.equals(StrmState.STREAM_UP)) {
            setSrvState(SrvState.SERVER_UP);
        }
    }

    public SrvState getSrvState() {
        return srvState;
    }

    public void setSrvState(SrvState newSrvState) {
        logServer(srvState, newSrvState);
        this.srvState = newSrvState;
    }

}
