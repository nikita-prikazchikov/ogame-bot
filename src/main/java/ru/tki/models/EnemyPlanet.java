package ru.tki.models;


import java.time.Instant;

public class EnemyPlanet extends Planet {

    private Long fleetCost;
    private Long defenceCost;

    public boolean fleetDiscovered   = false;
    public boolean defenceDiscovered = false;

    private boolean isInactive = false;
    private Instant lastUpdated;

    public EnemyPlanet() {
        setLastUpdated();
    }

    public EnemyPlanet(String coordinates) {
        super(coordinates);
        setLastUpdated();
    }

    public EnemyPlanet(String coordinates, String name) {
        super(coordinates, name);
        setLastUpdated();
    }

    public EnemyPlanet(Coordinates coordinates) {
        super(coordinates);
        setLastUpdated();
    }

    public EnemyPlanet(Coordinates coordinates, String name) {
        super(coordinates, name);
    }

    public Long getFleetCost() {
        return fleetCost != null ? fleetCost : fleet.getCost();
    }

    public void setFleetCost(Long fleetCost) {
        this.fleetCost = fleetCost;
    }

    public Long getDefenceCost() {
        return defenceCost != null ? defenceCost : defence.getCost();
    }

    public void setDefenceCost(Long defenceCost) {
        this.defenceCost = defenceCost;
    }

    public boolean isInactive() {
        return isInactive;
    }

    public void setInactive(boolean isInactive) {
        this.isInactive = isInactive;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated() {
        lastUpdated = Instant.now();
    }



}
