package ru.tki;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;


public class BotConfigMainReader {

    public BotConfigMain getPropValues() throws IOException {

        InputStream inputStream = null;
        BotConfigMain config = new BotConfigMain();

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            Date time = new Date(System.currentTimeMillis());

            config.setUrl(prop.getProperty("url"));
            config.setLogin(prop.getProperty("login"));
            config.setPassword(prop.getProperty("password"));
            config.setUniverse(prop.getProperty("universe"));

            //Setup reauired local property for chrome driver
            System.setProperty("webdriver.chrome.driver", prop.getProperty("webdriver.chrome.driver"));
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return config;
    }
}
