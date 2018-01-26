package ru.tki.models.tasks;

import ru.tki.models.*;
import ru.tki.models.actions.BuildingAction;
import ru.tki.models.types.BuildingType;
import ru.tki.po.BasePage;
import ru.tki.po.BuildingsPage;
import ru.tki.po.components.BuildDetailComponent;

//Build new resource building in the empire
public class BuildingTask extends Task {

    BuildingType type;
    transient Empire empire;

    public BuildingTask(Empire empire, AbstractPlanet planet, BuildingType type) {
        name = "Building";
        this.empire = empire;
        this.type = type;
        setPlanet(planet);
        if(planet.isPlanet()) {
            setResources(OGameLibrary.getBuildingPrice(type, ((Planet)planet).getBuildings().get(type)));
        }
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
        BuildingAction action = new BuildingAction(getPlanet());


        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(getPlanet());
        basePage.leftMenu.openResources();

        BuildingsPage buildingsPage = new BuildingsPage();
        Buildings buildings = buildingsPage.getBuildings();
        buildingsPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.addDuration(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        getPlanet().setResources(basePage.resources.getResources());
        getPlanet().setBuildInProgress(true);
        if(getPlanet().isPlanet()){
            buildings.set(type, buildings.get(type) + 1);
            ((Planet) getPlanet()).setBuildings(buildings);
        }
        empire.savePlanet(getPlanet());

        return action;
    }

    @Override
    public String toString() {
        if(getPlanet().isPlanet()) {
            return String.format("Build new %s level %d on planet %s", type, ((Planet) getPlanet()).getBuildings().get(type) + 1, getPlanet().getCoordinates().getFormattedCoordinates());
        }
        else{
            return "Build some building";
        }
    }
}
