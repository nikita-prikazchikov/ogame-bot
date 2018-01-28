package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.types.UpdateTaskType;
import ru.tki.po.BasePage;
import ru.tki.po.FleetDetailsPage;
import ru.tki.po.OverviewPage;

import java.util.List;

//Identify existing actions like buildings, researches and shipyard action
public class CheckExistingActionsTask extends Task {

    transient Empire empire;

    public CheckExistingActionsTask(Empire empire) {
        this.empire = empire;
        name = "Check if empire has active tasks and fleet flights";
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        navigation.openOverview();
        BasePage basePage = new BasePage();
        OverviewPage overviewPage = new OverviewPage();

        Action action = overviewPage.getResearchAction(empire, empire.getPlanets().get(0));
        addActionWithUpdateSubtask(action, new UpdateInfoTask(empire, empire.selectMain(), UpdateTaskType.RESEARCHES));

        navigation.openFleetMove();
        FleetDetailsPage fleetDetailsPage = new FleetDetailsPage();
        List<FleetAction> actions = fleetDetailsPage.getFleetActions(empire);
        actions.forEach(a -> {
            empire.addAction(a);
            empire.addActiveFleet();
        });

        navigation.openOverview();

        for (AbstractPlanet planet : empire.getPlanets()) {
            navigation.selectPlanet(planet);
            planet.setResources(basePage.resources.getResources());
            planet.logResources();

            action = overviewPage.getBuildAction(planet);
            addActionWithUpdateSubtask(action, new UpdateInfoTask(empire, planet, UpdateTaskType.All));

            action = overviewPage.getShipyardAction(planet);
            addActionWithUpdateSubtask(action, new UpdateInfoTask(empire, planet, UpdateTaskType.All));
        }
        return null;
    }

    private void addActionWithUpdateSubtask(Action action, Task task) {
        if (null != action) {
            action.addTask(task);
            empire.addAction(action);
        }
    }

    @Override
    public String toString() {
        return "Check existing buildings and researches";
    }
}
