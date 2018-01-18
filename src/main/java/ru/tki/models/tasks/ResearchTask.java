package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.ResearchType;
import ru.tki.models.StationType;
import ru.tki.models.actions.ResearchAction;
import ru.tki.models.actions.StationAction;
import ru.tki.po.BasePage;
import ru.tki.po.ResearchesPage;
import ru.tki.po.StationsPage;
import ru.tki.po.components.BuildDetailComponent;

public class ResearchTask extends Task {

    ResearchType type;

    public ResearchTask(AbstractPlanet planet, ResearchType type) {
        this.planet = planet;
        this.type = type;
    }

    public ResearchType getType() {
        return type;
    }

    public void setType(ResearchType type) {
        this.type = type;
    }

    @Override
    public ResearchAction execute() {
        ResearchAction action = new ResearchAction(planet);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(planet);
        basePage.leftMenu.openResearch();

        ResearchesPage researchesPage = new ResearchesPage();
        researchesPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.setEndDate(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        return action;
    }
}
