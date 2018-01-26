package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.OGameLibrary;
import ru.tki.models.Researches;
import ru.tki.models.actions.ResearchAction;
import ru.tki.models.types.ResearchType;
import ru.tki.po.BasePage;
import ru.tki.po.ResearchesPage;
import ru.tki.po.components.BuildDetailComponent;

//Start new research task
public class ResearchTask extends Task {

    ResearchType type;
    transient Empire empire;

    public ResearchTask(Empire empire, AbstractPlanet planet, ResearchType type) {
        name = "Research task";
        this.empire = empire;
        empire.setResearchInProgress(true);
        setPlanet(planet);
        setResources(OGameLibrary.getResearchPrice(type, empire.getResearches().get(type)));
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
        super.execute();
        ResearchAction action = new ResearchAction(getPlanet());

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(getPlanet());
        basePage.leftMenu.openResearch();

        ResearchesPage researchesPage = new ResearchesPage();
        Researches researches = researchesPage.getResearches();
        researchesPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.addDuration(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        getPlanet().setResources(basePage.resources.getResources());
        researches.set(type, researches.get(type) + 1);
        empire.setResearchInProgress(true);
        empire.setResearches(researches);
        empire.saveResearches();

        return action;
    }

    @Override
    public String toString() {
        return String.format("Research %s technology level %d on planet %s", type, empire.getResearches().get(type) + 1, getPlanet().getCoordinates().getFormattedCoordinates());
    }
}
