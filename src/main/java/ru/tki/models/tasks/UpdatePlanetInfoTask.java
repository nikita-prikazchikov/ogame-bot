package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Planet;
import ru.tki.models.actions.Action;
import ru.tki.po.*;
import ru.tki.po.components.ResourcesComponent;

public class UpdatePlanetInfoTask extends Task {

    Empire empire;

    public UpdatePlanetInfoTask(Empire empire, AbstractPlanet planet) {
        setPlanet(planet);
        this.empire = empire;
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        OverviewPage overviewPage = new OverviewPage();
        BuildingsPage buildingsPage = new BuildingsPage();
        FactoriesPage factoriesPage = new FactoriesPage();
        DefencePage defencePage = new DefencePage();
        FleetPage fleetPage = new FleetPage();
        ResourcesComponent resourcesComponent = new ResourcesComponent();

        Planet p = (Planet) planet;
        navigation.selectPlanet(planet);
        navigation.openOverview();
        planet.setName(overviewPage.getPlanetName());
        planet.setSize(overviewPage.getPlanetSize());
        planet.setResources(resourcesComponent.getResources());

        navigation.openResources();
        p.setBuildings(buildingsPage.getBuildings());

        navigation.openFactory();
        p.setFactories(factoriesPage.getFactories());

        navigation.openDefense();
        p.setDefence(defencePage.getDefence());

        navigation.openFleet();
        planet.setFleet(fleetPage.getFleet());

        empire.savePlanet(planet);
        return null;
    }

    @Override
    public String toString() {
        return String.format("Update planet %s information", planet.getCoordinates().getFormattedCoordinates());
    }
}
