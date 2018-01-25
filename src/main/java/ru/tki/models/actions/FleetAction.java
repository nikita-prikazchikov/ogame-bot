package ru.tki.models.actions;

import ru.tki.models.*;
import ru.tki.models.tasks.FleetTask;
import ru.tki.models.types.FleetSpeed;
import ru.tki.models.types.MissionType;

import java.time.Instant;

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

    public Boolean isFinished() {
        switch (missionType) {
            case COLONIZATION:
            case HOLD_ON:
            case KEEP:
                return Instant.now().compareTo(startDate.plus(duration)) > 0;
        }
        return Instant.now().compareTo(startDate.plus(duration.multipliedBy(2))) > 0;
    }

    public Boolean isTargetAchieved() {
        switch (missionType){
            case EXPEDITION:
            case RECYCLING:
            case TRANSPORT:
            case ESPIONAGE:
            case ATTACK:
            case JOINT_ATTACK:
            case DESTROY:
                return Instant.now().compareTo(startDate.plus(duration)) > 0;
        }
        return false;
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

    @Override
    public void complete(Empire empire) {
    }
}
