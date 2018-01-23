package ru.tki.models;


import com.google.gson.Gson;
import ru.tki.models.types.PlanetType;

public class Planet extends AbstractPlanet {

    Buildings  buildings = new Buildings();
    Factories  factories= new Factories();
    Researches researches;
    Defence    defence = new Defence();

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

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public Integer getLevel() {
        return buildings.getSolarPlant();
    }

    @Override
    public Integer getProduction() {
        return OGameLibrary.getMetalProduction(buildings.getMetalMine())
                + OGameLibrary.getCrystalProduction(buildings.getCrystalMine())
                + OGameLibrary.getDeuteriumProduction(buildings.getDeuteriumMine());
    }
}
