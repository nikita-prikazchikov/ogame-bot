package ru.tki.models;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Coordinates {

    private Integer galaxy;
    private Integer system;
    private Integer planet;

    public Coordinates(Integer galaxy, Integer system, Integer planet) {
        this.galaxy = galaxy;
        this.system = system;
        this.planet = planet;
    }

    public Coordinates(String input) {
        Pattern p = Pattern.compile("(\\d):(\\d{1,3}):(\\d{1,2})");
        Matcher m = p.matcher(input);
        if (m.find()) {
            galaxy = Integer.parseInt(m.group(1));
            system = Integer.parseInt(m.group(2));
            planet = Integer.parseInt(m.group(3));
        }
    }

    public Coordinates closer(Coordinates a, Coordinates b) {
        if (Math.abs(this.galaxy - a.galaxy)
                > Math.abs(this.galaxy - b.galaxy)) {
            return b;
        } else if (Math.abs(this.system - a.system)
                > Math.abs(this.system - b.system)) {
            return b;
        } else if (Math.abs(this.planet - a.planet)
                > Math.abs(this.planet - b.planet)) {
            return b;
        }
        return a;
    }

    public Coordinates nextSystem(){
        return new Coordinates(galaxy, system + 1, planet);
    }

    public String getFormattedCoordinates() {
        return String.format("%s:%s:%s", galaxy, system, planet);
    }

    public Integer getGalaxy() {
        return galaxy;
    }

    public Integer getSystem() {
        return system;
    }

    public Integer getPlanet() {
        return planet;
    }

    @Override
    public String toString() {
        return String.format("[%s:%s:%s]", galaxy, system, planet);
    }

    public String getFileSafeString() {
        return String.format("%s_%s_%s", galaxy, system, planet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinates that = (Coordinates) o;

        return galaxy.equals(that.galaxy) && system.equals(that.system) && planet.equals(that.planet);
    }

    @Override
    public int hashCode() {
        int result = galaxy.hashCode();
        result = 31 * result + system.hashCode();
        result = 31 * result + planet.hashCode();
        return result;
    }
}
