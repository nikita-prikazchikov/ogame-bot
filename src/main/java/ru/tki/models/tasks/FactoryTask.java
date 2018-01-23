package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Factories;
import ru.tki.models.Planet;
import ru.tki.models.types.FactoryType;
import ru.tki.models.actions.FactoryAction;
import ru.tki.models.types.PlanetType;
import ru.tki.po.BasePage;
import ru.tki.po.FactoriesPage;
import ru.tki.po.components.BuildDetailComponent;

public class FactoryTask extends Task {

    FactoryType type;
    Empire  empire;

    public FactoryTask(Empire empire, AbstractPlanet planet, FactoryType type) {
        this.empire = empire;
        this.planet = planet;
        this.type = type;
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
        FactoryAction action = new FactoryAction(planet, type);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(planet);
        basePage.leftMenu.openFactory();

        FactoriesPage factoriesPage = new FactoriesPage();
        Factories factories = factoriesPage.getFactories();
        factoriesPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.setDuration(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        planet.setResources(basePage.resources.getResources());
        planet.setBuildInProgress(true);
        if (type == FactoryType.SHIPYARD) {
            planet.setShipyardBusy(true);
        }
        if (type == FactoryType.RESEARCH_LAB) {
            empire.setResearchInProgress(true);
        }
        if (planet.getType() == PlanetType.PLANET) {
            factories.set(type, factories.get(type) + 1);
            ((Planet) planet).setFactories(factories);
        }
        empire.savePlanet(planet);

        return action;
    }

    @Override
    public String toString() {
        if(planet.getType() == PlanetType.PLANET) {
            return String.format("Build new %s level %d on planet %s", type, ((Planet)planet).getFactories().get(type) + 1, planet.getCoordinates().getFormattedCoordinates());
        }
        else{
            return "Build some factory";
        }
    }
}
