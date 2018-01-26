package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Planet;
import ru.tki.models.actions.Action;
import ru.tki.po.*;
import ru.tki.po.components.ResourcesComponent;

//Go to planet and collect information about current levels of buildins, defence, fleet
public class UpdatePlanetInfoTask extends Task {

    transient Empire empire;

    public UpdatePlanetInfoTask(Empire empire, AbstractPlanet planet) {
        name = "Update information about planet: resources, fleets, defence etc";
        setPlanet(planet, false);
        this.empire = empire;
    }

    @Override
    public Action execute() {
        //Do not call parent execute, because this task should not make planet free for new tasks
        //super.execute();
        Navigation navigation = new Navigation();
        OverviewPage overviewPage = new OverviewPage();
        BuildingsPage buildingsPage = new BuildingsPage();
        FactoriesPage factoriesPage = new FactoriesPage();
        DefencePage defencePage = new DefencePage();
        FleetPage fleetPage = new FleetPage();
        ResourcesComponent resourcesComponent = new ResourcesComponent();

        Planet p = (Planet) getPlanet();
        navigation.selectPlanet(getPlanet());
        navigation.openOverview();
        getPlanet().setName(overviewPage.getPlanetName());
        getPlanet().setSize(overviewPage.getPlanetSize());
        getPlanet().setResources(resourcesComponent.getResources());

        navigation.openResources();
        p.setBuildings(buildingsPage.getBuildings());

        navigation.openFactory();
        p.setFactories(factoriesPage.getFactories());

        navigation.openDefense();
        p.setDefence(defencePage.getDefence());

        navigation.openFleet();
        getPlanet().setFleet(fleetPage.getFleet());

        empire.savePlanet(getPlanet());
        getPlanet().logResources();
        return null;
    }

    @Override
    public String toString() {
        return String.format("Update planet %s information", getPlanet().getCoordinates().getFormattedCoordinates());
    }
}
