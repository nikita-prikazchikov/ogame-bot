package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.po.FleetDetailsPage;

//Identify existing fleets and expeditions count to avoid delayed expeditions and keep counter actual
public class CheckFleetsCountTask extends Task {

    transient  Empire empire;

    public CheckFleetsCountTask(Empire empire) {
        this.empire = empire;
        name = "Check if fleet and expedition count is valid";
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        navigation.openFleetMove();

        FleetDetailsPage fleetDetailsPage = new FleetDetailsPage();
        empire.setActiveExpeditions(fleetDetailsPage.getActiveExpeditions());
        empire.setActiveFleets(fleetDetailsPage.getActiveFleets());
        return null;
    }

    @Override
    public String toString() {
        return "Check current fleets and expeditions count";
    }
}
