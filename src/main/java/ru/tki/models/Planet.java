package ru.tki.models;


import ru.tki.models.types.PlanetType;

public class Planet extends AbstractPlanet {

    Buildings  buildings;
    Factories  factories;
    Researches researches;
    Defence    defence;
    Fleet      fleet;

    public Planet() {
        buildings = new Buildings();
        factories = new Factories();
        researches = new Researches();
        defence = new Defence();
        fleet = new Fleet();
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

    public Buildings getBuildings() {
        return buildings;
    }

    public void setBuildings(Buildings buildings) {
        this.buildings = buildings;
    }

    public Factories getFactories() {
        return factories;
    }

    public void setFactories(Factories factories) {
        this.factories = factories;
    }

    public Researches getResearches() {
        return researches;
    }

    public void setResearches(Researches researches) {
        this.researches = researches;
    }

    public Defence getDefence() {
        return defence;
    }

    public void setDefence(Defence defence) {
        this.defence = defence;
    }

    public Fleet getFleet() {
        return fleet;
    }

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }
}
