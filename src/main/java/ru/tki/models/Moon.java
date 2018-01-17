package ru.tki.models;


public class Moon  extends AbstractPlanet {

    public Moon() {
    }

    public Moon(String coordinates) {
        super(coordinates);
    }

    public Moon(String coordinates, String name) {
        super(coordinates, name);
    }

    public Moon(Coordinates coordinates, String name) {
        super(coordinates, name);
    }

    public PlanetType getType() {
        return PlanetType.MOON;
    }
}
