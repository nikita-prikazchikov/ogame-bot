package ru.tki.models;

import ru.tki.po.BasePage;


public abstract class AbstractPlanet {
    protected Coordinates coordinates;
    protected String      name;

    public AbstractPlanet() {
    }

    public AbstractPlanet(String coordinates) {
        this.coordinates = new Coordinates(coordinates);
    }

    public AbstractPlanet(String coordinates, String name) {
        this.coordinates = new Coordinates(coordinates);
        this.name = name;
    }

    public AbstractPlanet(Coordinates coordinates, String name) {
        this.coordinates = coordinates;
        this.name = name;
    }

    public abstract PlanetType getType();

    public void navigate() {
        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(this);
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

    @Override
    public String toString() {
        return "AbstractPlanet{" +
                "coordinates=" + coordinates +
                ", name=" + name +
                '}';
    }
}