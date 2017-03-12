/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mirobarsa.statuschecker;

import org.apache.log4j.Logger;

/**
 *
 * @author mbarsocchi
 */
public class InternetState {

    static final Logger logger = Logger.getLogger(InternetState.class.getName());

    private IntnetState netState = IntnetState.STARTING;
    private final UrlStatus urlSt;

    public enum IntnetState {

        STARTING,
        INTNET_OK,
        INTNET_DOWN;
    }

    public InternetState(UrlStatus urlSt) {
        this.urlSt = urlSt;
    }

    public IntnetState getNetState() {
        return netState;
    }

    public void setNetState(IntnetState newNetState) {
        logServer(newNetState);
        this.netState = newNetState;
    }

    private void logServer(IntnetState newIntState) {
        if ((netState.equals(IntnetState.INTNET_OK) || netState.equals(IntnetState.STARTING)) && newIntState.equals(IntnetState.INTNET_DOWN)) {
            logger.info("Errore di connessione internet");
        } else if ((netState.equals(IntnetState.INTNET_DOWN) || netState.equals(IntnetState.STARTING)) && newIntState.equals(IntnetState.INTNET_OK)) {
            logger.info("Internet OK, url [" + urlSt.getUrl() + "] response is 200");
        }
    }

    void check() {
        if (!urlSt.isUrlstateOk()) {
            this.setNetState(IntnetState.INTNET_DOWN);
        } else {
            this.setNetState(IntnetState.INTNET_OK);
        }
    }
}
