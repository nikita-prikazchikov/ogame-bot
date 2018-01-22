package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Planet;
import ru.tki.models.Researches;
import ru.tki.models.types.FactoryType;
import ru.tki.models.types.PlanetType;
import ru.tki.models.types.ResearchType;
import ru.tki.models.actions.ResearchAction;
import ru.tki.po.BasePage;
import ru.tki.po.ResearchesPage;
import ru.tki.po.components.BuildDetailComponent;

public class ResearchTask extends Task {

    ResearchType type;
    Empire       empire;

    public ResearchTask(Empire empire, AbstractPlanet planet, ResearchType type) {
        this.empire = empire;
        empire.setResearchInProgress(true);
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
        Researches researches = researchesPage.getResearches();
        researchesPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.setDuration(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        planet.setResources(basePage.resources.getResources());
        researches.set(type, researches.get(type) +1 );
        empire.setResearchInProgress(true);
        empire.setResearches(researches);
        empire.saveResearches();

        return action;
    }

    @Override
    public String toString() {
        return String.format("Research %s technology level %d on planet %s", type, empire.getResearches().get(type) + 1, planet.getCoordinates().getFormattedCoordinates());
    }
}
