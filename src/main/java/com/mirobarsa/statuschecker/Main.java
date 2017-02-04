package com.mirobarsa.statuschecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mbarsocchi
 */
public class Main {

    static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Checker.class.getName());

    public static void main(String[] args) throws URISyntaxException {

        Properties prop = new Properties();
        InputStream input = Main.class.getResourceAsStream("/config.properties");
        File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        String path = jarFile.getParentFile().getPath() + File.separator + "config.properties";

        try {
            if (new File(path).exists()) {
                prop.load(new FileInputStream(path));
            } else if (input != null) {
                prop.load(input);
            }
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
        Checker check = new Checker(prop);
        StreamState st = new StreamState();
        logger.info("Starting monitor");
        while (true) {
            check.check(st);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
