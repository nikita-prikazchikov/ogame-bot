package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.po.BasePage;
import ru.tki.po.OverviewPage;

public class CheckExistingActionsTask extends Task {

    Empire empire;

    public CheckExistingActionsTask(Empire empire) {
        this.empire = empire;
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        navigation.openOverview();
        BasePage basePage = new BasePage();
        OverviewPage overviewPage = new OverviewPage();

        Action action = overviewPage.getResearchAction(empire, empire.getPlanets().get(0));
        addActionWithUpdateSubtask(action);

        for (AbstractPlanet planet : empire.getPlanets()) {
            navigation.selectPlanet(planet);
            planet.setResources(basePage.resources.getResources());
            planet.logResources();

            action = overviewPage.getBuildAction(planet);
            addActionWithUpdateSubtask(action);

            action = overviewPage.getShipyardAction(planet);
            addActionWithUpdateSubtask(action);
        }
        return null;
    }

    private void addActionWithUpdateSubtask(Action action) {
        Task task;
        if (null != action) {
            task = new UpdatePlanetInfoTask(empire, action.getPlanet());
            action.setSubtask(task);
            empire.addAction(action);
        }
    }

    @Override
    public String toString() {
        return "Check existing buildings and researches";
    }
}
