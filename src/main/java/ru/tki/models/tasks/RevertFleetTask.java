package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.Empire;
import ru.tki.models.actions.FleetAction;
import ru.tki.po.FleetDetailsPage;

import java.time.Duration;

public class RevertFleetTask extends FleetTask {

    public RevertFleetTask(Empire empire, FleetAction action) {
        this.empire = empire;
        setPlanet(action.getPlanet(), false);
        this.targetPlanet = action.getTargetPlanet();
        this.missionType = action.getMissionType();
        this.fleet = action.getFleet();
        this.resources = action.getResources();
    }

    @Override
    public FleetAction execute() {
        FleetAction action = new FleetAction(this);

        if(fleet.isEmpty()){
            return action;
        }

        Navigation navigation = new Navigation();
        navigation.openFleetMove();

        FleetDetailsPage fleetDetailsPage = new FleetDetailsPage();

        Duration duration = fleetDetailsPage.getRevertDuration(this);
        action.addDuration(duration);
        fleetDetailsPage.revertFleet(this);
        action.setReturnFlight(true);
        action.addTask(new CheckExistingFlagsTask(empire));

        return action;
    }

    @Override
    public String toString() {
        return String.format("Revert fleet %s from planet %s to %s with %s mission and %s",
                fleet.getDetails(), getPlanet().getCoordinates().getFormattedCoordinates(), targetPlanet.getCoordinates(),
                missionType, resources == null ? "empty" : resources);
    }
}
