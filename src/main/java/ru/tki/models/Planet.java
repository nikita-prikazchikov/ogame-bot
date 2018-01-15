package ru.tki.models;


public class Planet implements IPlanet {
    private Coordinates coordinates;
    private String name;

    public Planet() {
    }

    public Planet(String coordinates, String name) {
        this.coordinates = new Coordinates(coordinates);
        this.name = name;
    }

    public Planet(Coordinates coordinates, String name) {
        this.coordinates = coordinates;
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    public PlanetType getType() {
        return PlanetType.PLANET;
    }
}
