package ru.tki.models.tasks;

import ru.tki.models.*;
import ru.tki.models.actions.FactoryAction;
import ru.tki.models.types.FactoryType;
import ru.tki.po.BasePage;
import ru.tki.po.FactoriesPage;
import ru.tki.po.components.BuildDetailComponent;

public class FactoryTask extends Task {

    FactoryType type;
    transient Empire  empire;

    public FactoryTask(Empire empire, AbstractPlanet planet, FactoryType type) {
        this.empire = empire;
        this.type = type;
        setPlanet(planet);
        if(planet.isPlanet()) {
            setResources(OGameLibrary.getFactoryPrice(type, ((Planet)planet).getFactories().get(type)));
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
        Factories factories = factoriesPage.getFactories();
        factoriesPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.addDuration(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        getPlanet().setResources(basePage.resources.getResources());
        getPlanet().setBuildInProgress(true);
        if (type == FactoryType.SHIPYARD) {
            getPlanet().setShipyardBusy(true);
        }
        if (type == FactoryType.RESEARCH_LAB) {
            empire.setResearchInProgress(true);
        }
        if (getPlanet().isPlanet()) {
            factories.set(type, factories.get(type) + 1);
            ((Planet) getPlanet()).setFactories(factories);
        }
        empire.savePlanet(getPlanet());

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
