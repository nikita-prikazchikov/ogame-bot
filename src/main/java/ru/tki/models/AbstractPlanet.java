package ru.tki.models;

import com.google.gson.Gson;
import ru.tki.models.types.PlanetType;
import ru.tki.po.BasePage;


public abstract class AbstractPlanet {
    protected Coordinates coordinates;
    protected String      name;
    protected Integer     size;
    protected Boolean     buildInProgress = false;
    Resources resources = new Resources();

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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Boolean getBuildInProgress() {
        return buildInProgress;
    }

    public void setBuildInProgress(Boolean buildInProgress) {
        this.buildInProgress = buildInProgress;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
