package ru.tki;


public class BotConfigMain {
    public String URL;
    public String LOGIN;
    public String PASSWORD;
    public String UNIVERSE;

    public Boolean HEADLESS  = false;
    public Boolean LOG_STATE = true;

    public Boolean BUILD_RESOURCES  = true;
    public Boolean BUILD_FACTORIES  = true;
    public Boolean BUILD_DEFENCE    = true;
    public Boolean BUILD_FLEET      = true;
    public Boolean BUILD_COLONIES   = true;
    public Boolean DO_RESEARCHES    = true;
    public Boolean SEND_EXPEDITIONS = true;

    public Integer UNIVERSE_SPEED       = 1;
    public Integer UNIVERSE_FLEET_SPEED = 1;

    public Integer EXECUTION_HOURS   = 0;
    public Integer EXECUTION_MINUTES = 0;

    public Integer COLONY_MIN_SIZE;
    //Sleep In milliseconds between fails and when nothing to do
    public Integer SLEEP_TIMEOUT = 10000;
    //In seconds
    public Integer ATTACK_CHECK_TIMEOUT = 120;
    public Integer UPDATE_RESOURCES_TIMEOUT = 900;
    public Integer FLEET_SAVE_TIMEOUT = 180;

    public Boolean DO_CHECK_ATTACK = true;
    public Boolean SCAN_FOR_INACTIVE = true;
}
