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

            config.URL = prop.getProperty("url");
            config.LOGIN = prop.getProperty("login");
            config.PASSWORD = prop.getProperty("password");
            config.UNIVERSE = prop.getProperty("universe");

            config.UNIVERSE_SPEED = Integer.parseInt(prop.getProperty("universe.speed", "1"));
            config.UNIVERSE_FLEET_SPEED = Integer.parseInt(prop.getProperty("universe.fleet.speed", "1"));
            config.EXECUTION_HOURS = Integer.parseInt(prop.getProperty("empire.execution.hours", "0"));
            config.EXECUTION_MINUTES = Integer.parseInt(prop.getProperty("empire.execution.minutes", "0"));

            config.COLONY_MIN_SIZE = Integer.parseInt(prop.getProperty("empire.colony.min.size", "180"));

            config.HEADLESS = Boolean.parseBoolean(prop.getProperty("chrome.headless", "false"));

            config.BUILD_RESOURCES = Boolean.parseBoolean(prop.getProperty("empire.build.resources", "true"));
            config.BUILD_FACTORIES = Boolean.parseBoolean(prop.getProperty("empire.build.factories", "true"));
            config.BUILD_DEFENCE = Boolean.parseBoolean(prop.getProperty("empire.build.defence", "true"));
            config.BUILD_FLEET = Boolean.parseBoolean(prop.getProperty("empire.build.fleet", "true"));
            config.BUILD_COLONIES = Boolean.parseBoolean(prop.getProperty("empire.build.colonies", "true"));
            config.DO_RESEARCHES = Boolean.parseBoolean(prop.getProperty("empire.do.researches", "true"));
            config.SEND_EXPEDITIONS = Boolean.parseBoolean(prop.getProperty("empire.send.expeditions", "true"));


            config.SLEEP_TIMEOUT = Integer.parseInt(prop.getProperty("sleep.timeout", "10000"));
            //In seconds
            config.ATTACK_CHECK_TIMEOUT = Integer.parseInt(prop.getProperty("empire.check.attack.timeout", "120"));
            config.UPDATE_RESOURCES_TIMEOUT = Integer.parseInt(prop.getProperty("update.resources.timeout", "900"));
            config.FLEET_SAVE_TIMEOUT = Integer.parseInt(prop.getProperty("fleet.save.timeout", "180"));

            config.DO_CHECK_ATTACK = Boolean.parseBoolean(prop.getProperty("empire.check.attack", "true"));

            config.LOG_STATE = Boolean.parseBoolean(prop.getProperty("empire.log.state", "false"));

            //Setup required local property for chrome driver
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
