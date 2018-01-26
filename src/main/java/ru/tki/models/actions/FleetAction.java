package ru.tki.models.actions;

import ru.tki.models.*;
import ru.tki.models.tasks.FleetTask;
import ru.tki.models.types.FleetSpeed;
import ru.tki.models.types.MissionType;

import java.time.Duration;
import java.time.Instant;

public class FleetAction extends Action {

    protected AbstractPlanet targetPlanet;
    protected Fleet          fleet;
    protected Resources      resources;
    protected MissionType    missionType;
    protected Instant        oneSideFleetTime = null;

    protected FleetSpeed fleetSpeed = FleetSpeed.S100;

    public FleetAction() {
        name = "Fleet";
    }

    public FleetAction(FleetTask task) {
        this();
        this.planet = task.getPlanet();
        this.targetPlanet = task.getTargetPlanet();
        this.fleet = task.getFleet();
        this.resources = task.getResources();
        this.fleetSpeed = task.getFleetSpeed();
        this.missionType = task.getMissionType();
    }

    public Boolean isTargetAchieved() {
        return null != oneSideFleetTime && Instant.now().compareTo(oneSideFleetTime) > 0;
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

    public MissionType getMissionType() {
        return missionType;
    }

    public void setMissionType(MissionType missionType) {
        this.missionType = missionType;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public void setDurationOfFlight(Duration duration){
        this.oneSideFleetTime = Instant.now().plus(duration).plus(Duration.ofSeconds(10));
    }

    public void setTargetAchieved(){
        this.oneSideFleetTime = null;
    }

    @Override
    public void complete(Empire empire) {
        empire.removeActiveFleet();
    }
}
