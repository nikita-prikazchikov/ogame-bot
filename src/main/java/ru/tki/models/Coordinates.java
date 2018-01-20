package ru.tki.models;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coordinates {

    private String galaxy;
    private String system;
    private String planet;

    public Coordinates(String input) {
        Pattern p = Pattern.compile("(\\d):(\\d{1,3}):(\\d{1,2})");
        Matcher m = p.matcher(input);
        if (m.find()) {
            galaxy = m.group(1);
            system = m.group(2);
            planet = m.group(3);
        }
    }

    public String getFormattedCoordinates(){
        return String.format("%s:%s:%s", galaxy, system, planet);
    }

    public String getGalaxy() {
        return galaxy;
    }

    public String getSystem() {
        return system;
    }

    public String getPlanet() {
        return planet;
    }

    @Override
    public String toString() {
        return String.format("[%s:%s:%s]", galaxy, system, planet);
    }

    public String getFileSafeString(){
        return String.format("%s_%s_%s", galaxy, system, planet);
    }
}
