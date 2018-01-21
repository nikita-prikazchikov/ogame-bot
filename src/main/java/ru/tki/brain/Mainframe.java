package ru.tki.brain;

import ru.tki.executor.Navigation;
import ru.tki.models.*;
import ru.tki.models.actions.Action;
import ru.tki.models.tasks.*;
import ru.tki.models.types.BuildingType;
import ru.tki.models.types.PlanetType;
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

    private void thinkBuildings(){
        for(AbstractPlanet planet:empire.getPlanets()){
            if (planet.getType() == PlanetType.PLANET && !planet.getBuildInProgress()){
                thinkBuildings((Planet) planet);
            }
            else if (planet.getType() == PlanetType.MOON){
                //Do nothing now
            }
        }
    }

    private void thinkBuildings(Planet planet) {
        // Rules for building
        // Keep buildings flat amount on planet
        // Solar main level X
        // Metal = X Crystal = X - 2 Deuterium X / 2
        // MS = M / 3 CS = C / 3 DS = D / 3
        Resources resources = planet.getResources();
        Buildings buildings = planet.getBuildings();
        Factories factories = planet.getFactories();
        Integer currentMax = buildings.getSolarPlant();

        if (buildings.getMetalMine() < currentMax) {
            if (resources.isEnoughFor(PriceLibrary.getBuildingPrice(BuildingType.METAL_MINE, buildings.getMetalMine() + 1))) {
                empire.addTask(new BuildingTask(empire, planet, BuildingType.METAL_MINE));
                return;
            }
        }
        if (buildings.getCrystalMine() < currentMax - 2) {
            if (resources.isEnoughFor(PriceLibrary.getBuildingPrice(BuildingType.CRYSTAL_MINE, buildings.getCrystalMine()))) {
                empire.addTask(new BuildingTask(empire, planet, BuildingType.CRYSTAL_MINE));
                return;
            }
        }
        if (buildings.getDeuteriumMine() < currentMax / 2) {
            if (resources.isEnoughFor(PriceLibrary.getBuildingPrice(BuildingType.DEUTERIUM_MINE, buildings.getDeuteriumMine()))) {
                empire.addTask(new BuildingTask(empire, planet, BuildingType.DEUTERIUM_MINE));
                return;
            }
        }
        if (buildings.getMetalStorage() < currentMax / 3) {
            if (resources.isEnoughFor(PriceLibrary.getBuildingPrice(BuildingType.METAL_STORAGE, buildings.getMetalStorage()))) {
                empire.addTask(new BuildingTask(empire, planet, BuildingType.METAL_STORAGE));
                return;
            }
        }
        if (buildings.getCrystalStorage() < (currentMax - 2) / 3) {
            if (resources.isEnoughFor(PriceLibrary.getBuildingPrice(BuildingType.CRYSTAL_STORAGE, buildings.getCrystalStorage()))) {
                empire.addTask(new BuildingTask(empire, planet, BuildingType.CRYSTAL_STORAGE));
                return;
            }
        }
        if (buildings.getDeuteriumStorage() < currentMax / 6) {
            if (resources.isEnoughFor(PriceLibrary.getBuildingPrice(BuildingType.DEUTERIUM_STORAGE, buildings.getDeuteriumStorage()))) {
                empire.addTask(new BuildingTask(empire, planet, BuildingType.DEUTERIUM_STORAGE));
                return;
            }
        }
        if (resources.isEnoughFor(PriceLibrary.getBuildingPrice(BuildingType.SOLAR_PLANT, buildings.getSolarPlant()))) {
            empire.addTask(new BuildingTask(empire, planet, BuildingType.SOLAR_PLANT));
        }
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
