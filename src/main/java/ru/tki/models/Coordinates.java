package ru.tki.models;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coordinates {

    private String galaxy;
    private String system;
    private String planet;

    public Coordinates(Integer galaxy, Integer system, Integer planet) {
        this.galaxy = galaxy.toString();
        this.system = system.toString();
        this.planet = planet.toString();
    }

    public Coordinates(String input) {
        Pattern p = Pattern.compile("(\\d):(\\d{1,3}):(\\d{1,2})");
        Matcher m = p.matcher(input);
        if (m.find()) {
            galaxy = m.group(1);
            system = m.group(2);
            planet = m.group(3);
        }
    }

    public Coordinates closer(Coordinates a, Coordinates b) {
        if (Math.abs(Integer.parseInt(this.galaxy) - Integer.parseInt(a.galaxy))
                > Math.abs(Integer.parseInt(this.galaxy) - Integer.parseInt(b.galaxy))) {
            return b;
        } else if (Math.abs(Integer.parseInt(this.system) - Integer.parseInt(a.system))
                > Math.abs(Integer.parseInt(this.system) - Integer.parseInt(b.system))) {
            return b;
        } else if (Math.abs(Integer.parseInt(this.planet) - Integer.parseInt(a.planet))
                > Math.abs(Integer.parseInt(this.planet) - Integer.parseInt(b.planet))) {
            return b;
        }
        return a;
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

    public boolean equals(Coordinates obj) {
        return  this.galaxy.equals(obj.galaxy)
                && this.system.equals(obj.system)
                && this.planet.equals(obj.planet);
    }
}
