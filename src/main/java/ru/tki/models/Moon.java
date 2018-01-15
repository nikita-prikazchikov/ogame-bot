package ru.tki.models;


public class Moon implements IPlanet {

    private Coordinates coordinates;
    private String name;

    public Moon() {
    }

    public Moon(String coordinates) {
        this.coordinates = new Coordinates(coordinates);
    }

    public Moon(String coordinates, String name) {
        this.coordinates = new Coordinates(coordinates);
        this.name = name;
    }

    public Moon(Coordinates coordinates, String name) {
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
        return PlanetType.MOON;
    }
}
