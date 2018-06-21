package com.mirobarsa.statuschecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author mbarsocchi
 */
public class UrlStatus {

    static final Logger logger = Logger.getLogger(UrlStatus.class.getName());

    private boolean downStatus;
    private String url;

    public UrlStatus(String url) {
        this.url = url;
    }

    public boolean isDown() {
        return downStatus;
    }

    public String getUrl() {
        return url;
    }

    private HttpURLConnection connect(String url) throws MalformedURLException, IOException {
        URL callUrl = null;
        callUrl = new URL(this.url);

        HttpURLConnection connection = null;
        connection = (HttpURLConnection) callUrl.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(4000);
        connection.connect();
        return connection;
    }

    public boolean isStreamDown(String streamUrl) {
        JSONObject obj = null;
        boolean result = true;
        String response = null;
        try {
            HttpURLConnection connection = this.connect(this.url);
            int status = connection.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    response = sb.toString();
                    obj = new JSONObject(response);
            }
            if (obj != null) {
                String streamUrlReceived = ((JSONObject) ((JSONObject) obj.get("icestats")).get("source")).getString("listenurl");
                if (streamUrlReceived != null && streamUrl.equals(streamUrlReceived)) {
                    result = false;
                }
            } else {
                logger.debug("Json response object nullo. La risposta è [" + response + "]");
            }
        } catch (JSONException | IOException ex) {
        }
        return result;
    }

    public boolean isUrlstateOk() {
        HttpURLConnection connection;
        int responseCode = -1;
        Boolean result = false;

        try {
            connection = this.connect(this.url);
            responseCode = connection.getResponseCode();
            result = responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException ex) {
        }

        return result;
    }

    public String traceRoute(Executer exe) {
        StringBuilder stringBuilder = new StringBuilder();
        Process p;
        String command = "";
        try {
            URL u = new URL(this.url);
            if (exe.isWindows()) {
                command = "tracert " + u.getHost();
            } else {
                command = "traceroute " + u.getHost();
//                command += u.getPort() == -1 ? "" : " -p " + u.getPort();
            }

            p = exe.execute(command);
            logger.info(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine).append("\n");
            }
            in.close();
        } catch (IOException ex) {
            logger.error("[" + this.url + "] " + ex);
        }
        return stringBuilder.toString();
    }

}
