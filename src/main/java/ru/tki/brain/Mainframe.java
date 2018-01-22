package ru.tki.brain;

import ru.tki.executor.Navigation;
import ru.tki.models.*;
import ru.tki.models.actions.Action;
import ru.tki.models.tasks.*;
import ru.tki.models.types.BuildingType;
import ru.tki.models.types.FactoryType;
import ru.tki.models.types.PlanetType;
import ru.tki.models.types.ShipType;
import ru.tki.po.LoginPage;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Mainframe {
    private static final Duration checkAttackDuration          = Duration.ofSeconds(120);
    private static final Duration checkUpdateResourcesDuration = Duration.ofMinutes(15);

    private static Logger logger = Logger.getLogger(String.valueOf(Mainframe.class));

    private Empire empire;
    private Instant lastAttackCheck = Instant.now();
    private Instant lastUpdateResources = Instant.now().minus(Duration.ofMinutes(15));

    Navigation navigation;
    LoginPage  loginPage;

    public Mainframe(Empire empire) {
        this.empire = empire;
        navigation = new Navigation();
        loginPage = new LoginPage();
    }

    public void start() {
        if (!empire.load()) {
            empire.addTask(new EmpireTask(empire));
            lastUpdateResources = Instant.now();
        }
        runExecution();
    }

    private void runExecution() {
        System.out.println("Let's the magic start!");
        do {
            try {
                if(!loginPage.isLoggedIn()){
                    navigation.openHomePage();
                    loginPage.login();
                }

                verifySchedules();
                verifyActions();
                think();
                execute();
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        while (true);
    }

    private void think() {
        thinkBuildings();
    }

    private void verifySchedules() {
        if(lastAttackCheck.plus(checkAttackDuration).compareTo(Instant.now())<0){
            empire.addTask(new CheckAttackTask(empire));
            lastAttackCheck = Instant.now();
        }

        if(lastUpdateResources.plus(checkUpdateResourcesDuration).compareTo(Instant.now())<0){
            empire.addTask(new UpdateResourcesTask(empire));
            lastUpdateResources = Instant.now();
        }
    }

    private void verifyActions(){
        List<Action> actions = new ArrayList<>();
        empire.getActions().stream().filter(Action::isFinished).forEach(action ->{
            action.complete(empire);
            actions.add(action);
        });
        actions.forEach(action -> {
            empire.getActions().remove(action);
        });
    }

    private void thinkBuildings() {
        Task task;
        for (AbstractPlanet planet : empire.getPlanets()) {
            if (planet.getType() == PlanetType.PLANET) {
                task = thinkBuildings((Planet) planet);
                if (null != task) {
                    empire.addTask(task);
                }
            } else if (planet.getType() == PlanetType.MOON) {
                //Do nothing now
            }
        }
    }

    private Task thinkBuildings(Planet planet) {
        // Rules for building
        // Keep buildings flat amount on planet
        // Solar main level X
        // Metal = X Crystal = X - 2 Deuterium X / 2
        // MS = M / 3 CS = C / 3 DS = D / 3
        Resources resources = planet.getResources();
        Buildings buildings = planet.getBuildings();
        Factories factories = planet.getFactories();
        Integer currentMax = buildings.getSolarPlant();

        if(!planet.getShipyardBusy()) {
            if (planet.getResources().getEnergy() < 0) {
                if (OGameLibrary.canBuild(empire, planet, ShipType.SOLAR_SATELLITE)
                        && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.SOLAR_SATELLITE, 1))) {
                    return new ShipyardTask(empire, planet, ShipType.SOLAR_SATELLITE, 1);
                }
            }
        }

        if(!planet.getBuildInProgress()) {
            if (factories.getRobotsFactory() <= 10 && factories.getRobotsFactory() < currentMax / 3) {
                if (resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.ROBOTS_FACTORY, factories.getRobotsFactory()))) {
                    return new FactoryTask(empire, planet, FactoryType.ROBOTS_FACTORY);
                }
            }
            if (!planet.getShipyardBusy() && factories.getShipyard() <= 9 && factories.getShipyard() < currentMax / 3) {
                if (OGameLibrary.canBuild(empire, planet, FactoryType.SHIPYARD)
                        && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.SHIPYARD, factories.getShipyard()))) {
                    return new FactoryTask(empire, planet, FactoryType.SHIPYARD);
                }
            }
            if (!empire.isResearchInProgress() && factories.getResearchLab() <= 10 && factories.getResearchLab() < currentMax / 3) {
                if (OGameLibrary.canBuild(empire, planet, FactoryType.RESEARCH_LAB)
                        && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.RESEARCH_LAB, factories.getResearchLab()))) {
                    return new FactoryTask(empire, planet, FactoryType.RESEARCH_LAB);
                }
            }

            if (buildings.getMetalMine() < currentMax) {
                if (resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.METAL_MINE, buildings.getMetalMine()))) {
                    return new BuildingTask(empire, planet, BuildingType.METAL_MINE);
                }
            }
            if (buildings.getCrystalMine() < currentMax - 2) {
                if (resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.CRYSTAL_MINE, buildings.getCrystalMine()))) {
                    return new BuildingTask(empire, planet, BuildingType.CRYSTAL_MINE);
                }
            }
            if (buildings.getDeuteriumMine() < currentMax / 2) {
                if (resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.DEUTERIUM_MINE, buildings.getDeuteriumMine()))) {
                    return new BuildingTask(empire, planet, BuildingType.DEUTERIUM_MINE);
                }
            }
            if (buildings.getMetalStorage() < currentMax / 3) {
                if (resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.METAL_STORAGE, buildings.getMetalStorage()))) {
                    return new BuildingTask(empire, planet, BuildingType.METAL_STORAGE);
                }
            }
            if (buildings.getCrystalStorage() < (currentMax - 2) / 3) {
                if (resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.CRYSTAL_STORAGE, buildings.getCrystalStorage()))) {
                    return new BuildingTask(empire, planet, BuildingType.CRYSTAL_STORAGE);
                }
            }
            if (buildings.getDeuteriumStorage() < currentMax / 6) {
                if (resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.DEUTERIUM_STORAGE, buildings.getDeuteriumStorage()))) {
                    return new BuildingTask(empire, planet, BuildingType.DEUTERIUM_STORAGE);
                }
            }
            if (resources.getEnergy() < 0) {
                if (resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.SOLAR_SATELLITE, buildings.getDeuteriumStorage()))) {
                    return new ShipyardTask(empire, planet, ShipType.SOLAR_SATELLITE, 1);
                }
            }
            if (resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.SOLAR_PLANT, buildings.getSolarPlant()))) {
                return new BuildingTask(empire, planet, BuildingType.SOLAR_PLANT);
            }
        }
        return null;
    }

    private void execute() throws InterruptedException {
        if (empire.getTasks().isEmpty()) {
            System.out.print('.');
            Thread.sleep(10000);
        }
        else {
            System.out.println("");
        }
        List<Task> tasksForRemove = new ArrayList<>();
        empire.getTasks().stream().filter(Task::canExecute).forEach(task -> {
            System.out.println("Execute task: " + task.toString());
            Action action = task.execute();
            if (null != action) {
                empire.addAction(action);
            }
            tasksForRemove.add(task);
        });
        for (Task task : tasksForRemove) {
            empire.removeTask(task);
        }
    }
}
