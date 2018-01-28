package ru.tki.models.tasks;

import ru.tki.models.*;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.types.FleetSpeed;
import ru.tki.models.types.MissionType;
import ru.tki.models.types.UpdateTaskType;
import ru.tki.po.BasePage;
import ru.tki.po.FleetPage;

import java.time.Duration;

//Send new fleet task
public class FleetTask extends Task {

    protected           AbstractPlanet targetPlanet;
    protected transient Empire         empire;
    protected           Fleet          fleet;

    protected MissionType missionType = MissionType.KEEP;
    protected FleetSpeed  fleetSpeed  = FleetSpeed.S100;

    public FleetTask(Empire empire, AbstractPlanet planet) {
        this.empire = empire;
        name = "Fleet task";
        setPlanet(planet);
        empire.addActiveFleet();
    }

    public FleetTask(Empire empire, AbstractPlanet fromPlanet, AbstractPlanet targetPlanet, Fleet fleet) {
        this(empire, fromPlanet);
        this.targetPlanet = targetPlanet;
        this.fleet = fleet;
    }

    public FleetTask(Empire empire, AbstractPlanet fromPlanet, AbstractPlanet targetPlanet, Fleet fleet, MissionType missionType) {
        this(empire, fromPlanet, targetPlanet, fleet);
        this.missionType = missionType;
    }

    public FleetTask(Empire empire, AbstractPlanet fromPlanet, AbstractPlanet targetPlanet, Fleet fleet, MissionType missionType, Resources resources) {
        this(empire, fromPlanet, targetPlanet, fleet, missionType);
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
        super.execute();
        FleetAction action = new FleetAction(this);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(getPlanet());
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
        Duration duration;
        switch (missionType) {
            case EXPEDITION:
                duration = fleetPage.getDuration();
                //All expeditions are 1 hour long
                action.addDuration(duration.multipliedBy(2).plusHours(1));
                break;
            case COLONIZATION:
            case KEEP:
            case HOLD_ON:
                duration = fleetPage.getDuration();
                action.setDurationOfFlight(duration);
                action.addDuration(duration.plusSeconds(30));
                break;
            case RECYCLING:
            case TRANSPORT:
            case ESPIONAGE:
            case ATTACK:
            case JOINT_ATTACK:
            case DESTROY:
                duration = fleetPage.getDuration();
                action.addDuration(duration.multipliedBy(2));
                action.setDurationOfFlight(duration);
                break;
        }
        if (missionType == MissionType.ATTACK
                || missionType == MissionType.TRANSPORT
                || missionType == MissionType.KEEP) {
            fleetPage.setResources(resources);
        }
        fleetPage.clickStart();
        fleetPage.waitPage1();

        getPlanet().setResources(basePage.resources.getResources());
        addTask(new UpdateInfoTask(empire, getPlanet(), UpdateTaskType.FLEET));
        //Add expedition only after it was actually sent
        if (missionType == MissionType.EXPEDITION) {
            empire.addActiveExpedition();
        }
        return action;
    }

    @Override
    public String toString() {
        return String.format("Sent fleet %s from planet %s to %s with %s mission and %s",
                fleet.getDetails(), getPlanet().getCoordinates().getFormattedCoordinates(), targetPlanet.getCoordinates().getFormattedCoordinates(),
                missionType, resources == null ? "empty" : resources);
    }
}
