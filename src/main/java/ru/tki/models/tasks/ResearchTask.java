package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.OGameLibrary;
import ru.tki.models.actions.ResearchAction;
import ru.tki.models.types.ResearchType;
import ru.tki.models.types.UpdateTaskType;
import ru.tki.po.BasePage;
import ru.tki.po.ResearchesPage;
import ru.tki.po.components.BuildDetailComponent;

import java.time.Duration;

//Start new research task
public class ResearchTask extends Task {

    private   ResearchType type;
    transient Empire       empire;

    public ResearchTask(Empire empire, AbstractPlanet planet, ResearchType type) {
        name = "Research task";
        this.empire = empire;
        empire.setResearchInProgress(true);
        setPlanet(planet);
        setResources(OGameLibrary.getResearchPrice(type, empire.getResearches().get(type)));
        this.type = type;
    }

    @Override
    public void removeFromQueue() {
        super.removeFromQueue();
        empire.setResearchInProgress(false);
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
        getPlanet().setResources(basePage.resources.getResources());
        if(!getPlanet().getResources().isEnoughFor(OGameLibrary.getResearchPrice(type, researchesPage.getResearchLevel(type)))){
            //There is no resources for build. Refresh planet info and start thinking again
            System.out.println(String.format("Can't start %s on planet %s because there is not enough resources", type, getPlanet().getCoordinates()));
            action.addDuration(Duration.ZERO);
            action.addTask(new UpdateInfoTask(empire, getPlanet(), UpdateTaskType.RESEARCHES));
            return action;
        }
        researchesPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.addDuration(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        getPlanet().setResources(basePage.resources.getResources());
        action.addTask(new UpdateInfoTask(empire, getPlanet(), UpdateTaskType.RESEARCHES));

        return action;
    }

    @Override
    public String toString() {
        return String.format("Research %s technology level %d on planet %s", type, empire.getResearches().get(type) + 1, getPlanet().getCoordinates());
    }
}
