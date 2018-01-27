package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Planet;
import ru.tki.models.actions.Action;
import ru.tki.models.types.UpdateTaskType;
import ru.tki.po.*;
import ru.tki.po.components.ResourcesComponent;

//Go to planet and collect information about current levels of buildins, defence, fleet
public class UpdateInfoTask extends Task {

    transient Empire         empire;
    private   UpdateTaskType type;

    private Navigation navigation;

    public UpdateInfoTask(Empire empire, AbstractPlanet planet, UpdateTaskType type) {
        name = "Update information about planet: " + type;
        setPlanet(planet, false);
        this.type = type;
        this.empire = empire;
    }

    @Override
    public Action execute() {
        //Do not call parent execute, because this task should not make planet free for new tasks
        //super.execute();

        navigation = new Navigation();

        ResourcesComponent resourcesComponent = new ResourcesComponent();

        Planet p = (Planet) getPlanet();
        navigation.selectPlanet(getPlanet());
        getPlanet().setResources(resourcesComponent.getResources());
        switch (type){

            case All:
                updateDetails();
                updateBuildings(p);
                updateFactories(p);
                updateDefence(p);
                updateFleet();
                break;
            case RESOURCES:
                //Always update resources. This is more stub value for now
                break;
            case DETAILS:
                updateDetails();
                break;
            case BUILDINGS:
                updateBuildings(p);
                break;
            case FACTORIES:
                updateFactories(p);
                break;
            case RESEARCHES:
                break;
            case FLEET:
                updateFleet();
                break;
            case DEFENCE:
                updateDefence(p);
                break;
        }

        empire.savePlanet(getPlanet());
        getPlanet().logResources();
        return null;
    }

    private void updateDetails() {
        navigation.openOverview();
        OverviewPage overviewPage = new OverviewPage();
        getPlanet().setName(overviewPage.getPlanetName());
        getPlanet().setSize(overviewPage.getPlanetSize());
    }

    private void updateBuildings(Planet p) {
        navigation.openResources();
        p.setBuildings(new BuildingsPage().getBuildings());
    }

    private void updateFactories(Planet p) {
        navigation.openFactory();
        p.setFactories(new FactoriesPage().getFactories());
    }

    private void updateDefence(Planet p) {
        navigation.openDefense();
        p.setDefence(new DefencePage().getDefence());
    }

    private void updateFleet() {
        navigation.openFleet();
        getPlanet().setFleet(new FleetPage().getFleet());
    }

    private void updateResearches() {
        navigation.selectPlanet(empire.selectMain());
        navigation.openResearch();

        empire.setResearches(new ResearchesPage().getResearches());
        empire.saveResearches();
    }

    @Override
    public String toString() {
        return String.format("Update planet %s information: %s", getPlanet().getCoordinates().getFormattedCoordinates(), type);
    }
}
