package ru.tki.models;


import ru.tki.models.types.PlanetType;

public class Debris extends AbstractPlanet {

    public Debris() {
    }

    public Debris(String coordinates) {
        super(coordinates);
    }

    public Debris(String coordinates, String name) {
        super(coordinates, name);
    }

    public Debris(Coordinates coordinates, String name) {
        super(coordinates, name);
    }

    public PlanetType getType() {
        return PlanetType.DEBRIS;
    }
}
