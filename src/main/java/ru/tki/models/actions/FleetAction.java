package ru.tki.models.actions;

import ru.tki.models.*;
import ru.tki.models.tasks.CheckFleetsCountTask;
import ru.tki.models.tasks.FleetTask;
import ru.tki.models.tasks.SaveFleetTask;
import ru.tki.models.types.FleetSpeed;
import ru.tki.models.types.MissionType;

import java.time.Duration;
import java.time.Instant;

public class FleetAction extends Action {

    protected AbstractPlanet targetPlanet;
    protected Fleet          fleet;
    protected Resources      resources;
    protected MissionType    missionType;

    protected Instant    oneSideFleetTime = null;
    protected Boolean    isSaveFlight     = false;
    protected Boolean    isReturnFlight   = false;
    protected FleetSpeed fleetSpeed       = FleetSpeed.S100;

    public FleetAction() {
        super();
        name = "Fleet";
        fleet = new Fleet();
        resources = new Resources();
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

    public FleetAction(SaveFleetTask task) {
        this();
        this.planet = task.getPlanet();
        this.targetPlanet = task.getTargetPlanet();
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

    public void setDurationOfFlight(Duration duration) {
        this.oneSideFleetTime = Instant.now().plus(duration).plus(Duration.ofSeconds(5));
    }

    public void setTargetAchieved() {
        this.oneSideFleetTime = null;
        setReturnFlight(true);
    }

    public Instant getFleetTimeToTarget() {
        return oneSideFleetTime;
    }

    public Boolean isSaveFlight() {
        return isSaveFlight;
    }

    public void setSaveFlight(Boolean saveFlight) {
        isSaveFlight = saveFlight;
    }

    public Boolean isReturnFlight() {
        return isReturnFlight;
    }

    public void setReturnFlight(Boolean returnFlight) {
        isReturnFlight = returnFlight;
    }

    @Override
    public void complete(Empire empire) {
        empire.removeActiveFleet();
        if (missionType == MissionType.EXPEDITION) {
            empire.removeActiveExpedition();
            empire.addTask(new CheckFleetsCountTask(empire));
        }
    }

    @Override
    public String toLog() {
        return String.format("Flight from planet %s to %s with %s mission: fleet detail: %s",
                getPlanet().getCoordinates(), targetPlanet.getCoordinates(),
                missionType, fleet.getDetails());
    }
}
