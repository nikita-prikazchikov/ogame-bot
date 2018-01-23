package ru.tki;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

            config.setProperties(prop);

            config.setUrl(prop.getProperty("url"));
            config.setLogin(prop.getProperty("login"));
            config.setPassword(prop.getProperty("password"));
            config.setUniverse(prop.getProperty("universe"));
            config.setHeadless(Boolean.parseBoolean(prop.getProperty("chrome.headless", "false")));

            config.setBuildResources(Boolean.parseBoolean(prop.getProperty("empire.build.resources", "true")));
            config.setBuildFactories(Boolean.parseBoolean(prop.getProperty("empire.build.factories", "true")));
            config.setBuildDefence(Boolean.parseBoolean(prop.getProperty("empire.build.defence", "true")));
            config.setBuildFleet(Boolean.parseBoolean(prop.getProperty("empire.build.fleet", "true")));
            config.setBuildColonies(Boolean.parseBoolean(prop.getProperty("empire.build.colonies", "true")));
            config.setDoResearches(Boolean.parseBoolean(prop.getProperty("empire.do.researches", "true")));

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
