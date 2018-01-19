package ru.tki.models.actions;

import ru.tki.models.*;
import ru.tki.models.tasks.FleetTask;
import ru.tki.models.types.FleetSpeed;
import ru.tki.models.types.MissionType;

public class FleetAction extends Action {

    protected AbstractPlanet targetPlanet;
    protected Fleet          fleet;
    protected Resources      resources;
    protected MissionType    missionType;

    protected FleetSpeed fleetSpeed = FleetSpeed.S100;

    public FleetAction(FleetTask task) {
        this.planet = task.getPlanet();
        this.targetPlanet = task.getTargetPlanet();
        this.fleet = task.getFleet();
        this.resources = task.getResources();
        this.fleetSpeed = task.getFleetSpeed();
        this.missionType = task.getMissionType();
    }

    public AbstractPlanet getTargetPlanet() {
        return targetPlanet;
    }

    public void setTargetPlanet(AbstractPlanet targetPlanet) {
        this.targetPlanet = targetPlanet;
    }

    public Fleet getFleet() {
        return fleet;
    }

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }

    public FleetSpeed getFleetSpeed() {
        return fleetSpeed;
    }

    public void setFleetSpeed(FleetSpeed fleetSpeed) {
        this.fleetSpeed = fleetSpeed;
    }
}
