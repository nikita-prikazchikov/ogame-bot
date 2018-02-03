package ru.tki.models;

import com.google.gson.Gson;
import ru.tki.models.types.PlanetType;
import ru.tki.po.BasePage;


public abstract class AbstractPlanet {
    protected Coordinates coordinates;
    protected String      name;
    protected Integer     size;
    protected Boolean     buildInProgress = false;
    protected Boolean     shipyardBusy = false;
    protected Boolean     hasTask = false;



    protected Resources resources = new Resources();
    protected Fleet fleet = new Fleet();

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

    //Current planet level. Most of the logic of building relates on it.
    public abstract Integer getLevel();

    public abstract Integer getProduction();

    public void navigate() {
        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(this);
    }

    public AbstractPlanet closer(AbstractPlanet a, AbstractPlanet b) {
        if( a.getCoordinates().equals(coordinates.closer(a.getCoordinates(), b.getCoordinates()))){
            return a;
        }
        return b;
    }

    public boolean isPlanet(){
        return getType() == PlanetType.PLANET;
    }

    public boolean hasResources(Resources resources){
        return this.resources.isEnoughFor(resources);
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

    public Boolean getShipyardBusy() {
        return shipyardBusy;
    }

    public void setShipyardBusy(Boolean shipyardBusy) {
        this.shipyardBusy = shipyardBusy;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public Fleet getFleet() {
        return fleet;
    }

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }

    public Boolean hasTask() {
        return hasTask;
    }

    public void setHasTask(Boolean hasTask) {
        this.hasTask = hasTask;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public boolean equals(AbstractPlanet planet) {
        return this.coordinates.equals(planet.getCoordinates()) && this.getType().equals(planet.getType());
    }

    public boolean equals(Coordinates coordinates) {
        return this.coordinates.equals(coordinates);
    }

    public void logResources(){
        System.out.println(String.format("Resources on %s %s %s", getType(), getCoordinates().getFormattedCoordinates(), getResources()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPlanet)) return false;

        AbstractPlanet planet = (AbstractPlanet) o;

        return coordinates != null ? coordinates.equals(planet.coordinates) : planet.coordinates == null;
    }

    @Override
    public int hashCode() {
        return coordinates != null ? coordinates.hashCode() : 0;
    }
}
