package ru.tki.models.tasks;

import ru.tki.models.*;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.types.FleetSpeed;
import ru.tki.models.types.MissionType;
import ru.tki.po.BasePage;
import ru.tki.po.FleetPage;

public class FleetTask extends Task {

    protected AbstractPlanet targetPlanet;
    protected Fleet          fleet;
    protected Resources      resources;
    protected MissionType missionType = MissionType.TRANSPORT;

    protected FleetSpeed fleetSpeed = FleetSpeed.S100;

    public FleetTask(AbstractPlanet planet) {
        this.planet = planet;
    }

    public FleetTask(AbstractPlanet fromPlanet, AbstractPlanet targetPlanet, Fleet fleet) {
        this(fromPlanet);
        this.targetPlanet = targetPlanet;
        this.fleet = fleet;
    }

    public  FleetTask(AbstractPlanet fromPlanet, AbstractPlanet targetPlanet, Fleet fleet, MissionType missionType) {
        this(fromPlanet, targetPlanet, fleet);
        this.missionType = missionType;
    }

    public  FleetTask(AbstractPlanet fromPlanet, AbstractPlanet targetPlanet, Fleet fleet, MissionType missionType, Resources resources) {
        this(fromPlanet, targetPlanet, fleet, missionType);
        this.resources = resources;
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

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
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

    @Override
    public FleetAction execute() {
        FleetAction action = new FleetAction(this);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(planet);
        basePage.leftMenu.openFleet();

        FleetPage fleetPage = new FleetPage();
        fleetPage.selectFleet(fleet);
        fleetPage.clickContinue();

        basePage.pause(300);

        //Fleet page 2
        fleetPage.setTarget(targetPlanet.getType());
        fleetPage.setCoordinates(targetPlanet.getCoordinates());
        fleetPage.setSpeed(fleetSpeed);
        fleetPage.clickContinue();

        basePage.pause(300);

        //Fleet page 3
        fleetPage.setMission(missionType);
        action.setDuration(fleetPage.getDuration());
        if (missionType == MissionType.ATTACK
                || missionType == MissionType.TRANSPORT
                || missionType == MissionType.KEEP){
            fleetPage.setResources(resources);
        }
        fleetPage.clickStart();

        return action;
    }
}
