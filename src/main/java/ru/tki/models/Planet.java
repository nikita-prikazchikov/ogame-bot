package ru.tki.models;


public class Planet extends AbstractPlanet {

    public Planet() {
    }

    public Planet(String coordinates) {
        super(coordinates);
    }

    public Planet(String coordinates, String name) {
        super(coordinates, name);
    }

    public Planet(Coordinates coordinates, String name) {
        super(coordinates, name);
    }

    public PlanetType getType() {
        return PlanetType.PLANET;
    }
}
