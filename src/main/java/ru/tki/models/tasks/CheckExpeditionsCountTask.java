package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.po.FleetDetailsPage;

//Identify existing expeditions count to avoid delayed expeditions and keep counter actual
public class CheckExpeditionsCountTask extends Task {

    transient  Empire empire;

    public CheckExpeditionsCountTask(Empire empire) {
        this.empire = empire;
        name = "Check if expeditions count is valid";
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        navigation.openFleetMove();

        FleetDetailsPage fleetDetailsPage = new FleetDetailsPage();
        empire.setActiveExpeditions(fleetDetailsPage.getActiveExpeditions());
        return null;
    }

    @Override
    public String toString() {
        return "Check current expeditions count";
    }
}
