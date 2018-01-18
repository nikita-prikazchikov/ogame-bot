package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.StationType;
import ru.tki.models.actions.BuildStationAction;
import ru.tki.po.BasePage;
import ru.tki.po.StationsPage;
import ru.tki.po.components.BuildDetailComponent;

public class BuildStationTask extends Task {

    StationType type;

    public BuildStationTask(AbstractPlanet planet, StationType type) {
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
    public BuildStationAction execute() {
        BuildStationAction action = new BuildStationAction(planet);

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
