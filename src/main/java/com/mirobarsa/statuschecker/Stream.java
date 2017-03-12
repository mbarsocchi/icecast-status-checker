/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.statuschecker;

import com.mirobarsa.statuschecker.InternetState.IntnetState;
import static com.mirobarsa.statuschecker.UrlStatus.logger;

/**
 *
 * @author mbarsocchi
 */
public class Stream {

    private final UrlStatus stream;
    private final UrlStatus server;
    private final InternetState inetSt;
    private final StreamState streamState;
    boolean firstTrace = true;

    Stream(String streamListenUrl, String serverUrl, String stEndppoint, String internetUrl) {
        this.streamState = new StreamState();
        this.stream = new UrlStatus(serverUrl + "/" + stEndppoint);
        this.server = new UrlStatus(serverUrl);
        this.inetSt = new InternetState(new UrlStatus(internetUrl));
    }

    boolean isStreamDown(String streamUrl) {
        boolean res = stream.isStreamDown(streamUrl);
        if (res) {
            streamState.setStreamState(StreamState.StrmState.STREAM_DOWN);
        } else {
            streamState.setStreamState(StreamState.StrmState.STREAM_UP);
            inetSt.setNetState(InternetState.IntnetState.INTNET_OK);
        }
        return res;
    }

    boolean isServerUrlOk() {
        boolean res = server.isUrlstateOk();
        if (res) {
            streamState.setSrvState(StreamState.SrvState.SERVER_UP);
            this.firstTrace = true;
        } else {
            streamState.setSrvState(StreamState.SrvState.SERVER_DOWN);
            inetSt.check();
        }
        return res;
    }

    void checkTraceRoute(Executer exe) {
        if (inetSt.getNetState().equals(IntnetState.INTNET_OK) && firstTrace) {
            logger.error(this.server.traceRoute(exe));
            this.firstTrace = false;
        }
    }

    StreamState.StrmState getStreamState() {
        return streamState.getStreamState();
    }

    void setStreamState(StreamState.StrmState strmState) {
        streamState.setStreamState(strmState);
    }

}
