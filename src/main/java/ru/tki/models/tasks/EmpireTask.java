package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Planet;
import ru.tki.models.actions.Action;
import ru.tki.models.types.PlanetType;
import ru.tki.po.*;
import ru.tki.po.components.ResourcesComponent;

import java.util.List;

// Collect information about the whole empire
public class EmpireTask extends Task {

    Empire empire;

    public EmpireTask(Empire empire) {
        this.empire = empire;
    }

    @Override
    public Action execute() {
        BasePage basePage = new BasePage();
        List<AbstractPlanet> planets = basePage.myWorlds.getPlanets();

        Navigation navigation = new Navigation();
        navigation.openResearch();

        ResearchesPage researchesPage = new ResearchesPage();
        empire.setResearches(researchesPage.getResearches());

        ResourcesComponent resourcesComponent = new ResourcesComponent();
        OverviewPage overviewPage = new OverviewPage();
        BuildingsPage buildingsPage = new BuildingsPage();
        FactoriesPage factoriesPage = new FactoriesPage();
        DefencePage defencePage = new DefencePage();
        FleetPage fleetPage = new FleetPage();

        for (AbstractPlanet planet : planets) {
            basePage.myWorlds.selectPlanet(planet);
            if (planet.isPlanet()) {
                Planet p = (Planet) planet;

                navigation.openOverview();
                p.setName(overviewPage.getPlanetName());
                p.setSize(overviewPage.getPlanetSize());
                p.setResources(resourcesComponent.getResources());

                navigation.openResources();
                p.setBuildings(buildingsPage.getBuildings());

                navigation.openFactory();
                p.setFactories(factoriesPage.getFactories());

                navigation.openDefense();
                p.setDefence(defencePage.getDefence());

                navigation.openFleet();
                p.setFleet(fleetPage.getFleet());
            }
            if (planet.getType() == PlanetType.MOON){
                //todo: extend this load if needed
            }
        }
        empire.setPlanets(planets);
        empire.save();
        return null;
    }

    @Override
    public String toString() {
        return "Collect information about the whole empire";
    }
}
