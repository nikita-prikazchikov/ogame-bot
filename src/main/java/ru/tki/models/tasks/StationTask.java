package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.StationType;
import ru.tki.models.actions.StationAction;
import ru.tki.po.BasePage;
import ru.tki.po.StationsPage;
import ru.tki.po.components.BuildDetailComponent;

public class StationTask extends Task {

    StationType type;

    public StationTask(AbstractPlanet planet, StationType type) {
        this.planet = planet;
        this.type = type;
    }

    public StationType getType() {
        return type;
    }

    public void setType(StationType type) {
        this.type = type;
    }

    @Override
    public StationAction execute() {
        StationAction action = new StationAction(planet);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(planet);
        basePage.leftMenu.openStation();

        StationsPage stationsPage = new StationsPage();
        stationsPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.setEndDate(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        return action;
    }
}
