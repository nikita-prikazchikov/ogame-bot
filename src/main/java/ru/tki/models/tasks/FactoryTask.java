package ru.tki.models.tasks;

import ru.tki.models.*;
import ru.tki.models.actions.FactoryAction;
import ru.tki.models.types.FactoryType;
import ru.tki.models.types.UpdateTaskType;
import ru.tki.po.BasePage;
import ru.tki.po.FactoriesPage;
import ru.tki.po.components.BuildDetailComponent;

import java.time.Duration;

public class FactoryTask extends Task {

    FactoryType type;
    transient Empire  empire;

    public FactoryTask(Empire empire, AbstractPlanet planet, FactoryType type) {
        name = "Factory task";
        this.empire = empire;
        this.type = type;
        setPlanet(planet);
        if(planet.isPlanet()) {
            setResources(OGameLibrary.getFactoryPrice(type, ((Planet)planet).getFactories().get(type)));
        }
        getPlanet().setBuildInProgress(true);
        if (type == FactoryType.SHIPYARD) {
            getPlanet().setShipyardBusy(true);
        }
        if (type == FactoryType.RESEARCH_LAB) {
            empire.setResearchInProgress(true);
        }
    }

    @Override
    public void removeFromQueue() {
        super.removeFromQueue();
        if (type == FactoryType.RESEARCH_LAB) {
            empire.setResearchInProgress(false);
        }
        if (type == FactoryType.SHIPYARD) {
            getPlanet().setShipyardBusy(false);
        }
    }

    public FactoryType getType() {
        return type;
    }

    public void setType(FactoryType type) {
        this.type = type;
    }

    @Override
    public FactoryAction execute() {
        super.execute();
        FactoryAction action = new FactoryAction(getPlanet(), type);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(getPlanet());
        basePage.leftMenu.openFactory();

        FactoriesPage factoriesPage = new FactoriesPage();
        getPlanet().setResources(basePage.resources.getResources());
        if(!getPlanet().getResources().isEnoughFor(OGameLibrary.getFactoryPrice(type, factoriesPage.getBuildingLevel(type)))){
            //There is no resources for build. Refresh planet info and start thinking again
            System.out.println(String.format("Can't start %s on planet %s because there is not enough resources", type, getPlanet().getCoordinates().getFormattedCoordinates()));
            action.addDuration(Duration.ZERO);
            action.addTask(new UpdateInfoTask(empire, getPlanet(), UpdateTaskType.FACTORIES));
            return action;
        }
        factoriesPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.addDuration(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        getPlanet().setResources(basePage.resources.getResources());
        action.addTask(new UpdateInfoTask(empire, getPlanet(), UpdateTaskType.FACTORIES));

        return action;
    }

    @Override
    public String toString() {
        if(getPlanet().isPlanet()) {
            return String.format("Build new %s level %d on planet %s", type, ((Planet) getPlanet()).getFactories().get(type) + 1, getPlanet().getCoordinates().getFormattedCoordinates());
        }
        else{
            return "Build some factory";
        }
    }
}
