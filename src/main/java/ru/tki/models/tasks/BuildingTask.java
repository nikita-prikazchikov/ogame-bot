package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Buildings;
import ru.tki.models.Empire;
import ru.tki.models.Planet;
import ru.tki.models.actions.BuildingAction;
import ru.tki.models.types.BuildingType;
import ru.tki.models.types.PlanetType;
import ru.tki.po.BasePage;
import ru.tki.po.BuildingsPage;
import ru.tki.po.components.BuildDetailComponent;

public class BuildingTask extends Task {

    BuildingType type;
    Empire empire;

    public BuildingTask(Empire empire, AbstractPlanet planet, BuildingType type) {
        this.empire = empire;
        this.planet = planet;
        this.type = type;
    }

    public BuildingType getType() {
        return type;
    }

    public void setType(BuildingType type) {
        this.type = type;
    }

    @Override
    public BuildingAction execute() {
        super.execute();
        BuildingAction action = new BuildingAction(planet);


        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(planet);
        basePage.leftMenu.openResources();

        BuildingsPage buildingsPage = new BuildingsPage();
        Buildings buildings = buildingsPage.getBuildings();
        buildingsPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.setDuration(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        planet.setResources(basePage.resources.getResources());
        planet.setBuildInProgress(true);
        if(planet.getType() == PlanetType.PLANET){
            buildings.set(type, buildings.get(type) + 1);
            ((Planet)planet).setBuildings(buildings);
        }
        empire.savePlanet(planet);

        return action;
    }

    @Override
    public String toString() {
        if(planet.getType() == PlanetType.PLANET) {
            return String.format("Build new %s level %d on planet %s", type, ((Planet)planet).getBuildings().get(type) + 1, planet.getCoordinates().getFormattedCoordinates());
        }
        else{
            return "Build some building";
        }
    }
}
