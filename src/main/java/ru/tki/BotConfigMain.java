package ru.tki;

import java.util.Properties;

public class BotConfigMain {
    String url;
    String login;
    String password;
    String universe;
    Boolean headless = false;

    Boolean buildResources = true;
    Boolean buildFactories = true;
    Boolean buildDefence = true;
    Boolean buildFleet = true;
    Boolean buildColonies = true;
    Boolean doResearches = true;

    Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUniverse() {
        return universe;
    }

    public void setUniverse(String universe) {
        this.universe = universe;
    }

    public Boolean getHeadless() {
        return headless;
    }

    public void setHeadless(Boolean headless) {
        this.headless = headless;
    }

    public Boolean getBuildResources() {
        return buildResources;
    }

    public void setBuildResources(Boolean buildResources) {
        this.buildResources = buildResources;
    }

    public Boolean getBuildFactories() {
        return buildFactories;
    }

    public void setBuildFactories(Boolean buildFactories) {
        this.buildFactories = buildFactories;
    }

    public Boolean getBuildDefence() {
        return buildDefence;
    }

    public void setBuildDefence(Boolean buildDefence) {
        this.buildDefence = buildDefence;
    }

    public Boolean getBuildFleet() {
        return buildFleet;
    }

    public void setBuildFleet(Boolean buildFleet) {
        this.buildFleet = buildFleet;
    }

    public Boolean getBuildColonies() {
        return buildColonies;
    }

    public void setBuildColonies(Boolean buildColonies) {
        this.buildColonies = buildColonies;
    }

    public Boolean getDoResearches() {
        return doResearches;
    }

    public void setDoResearches(Boolean doResearches) {
        this.doResearches = doResearches;
    }
}
