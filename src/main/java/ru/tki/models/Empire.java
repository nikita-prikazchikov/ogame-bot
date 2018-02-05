package ru.tki.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openqa.selenium.InvalidArgumentException;
import ru.tki.BotConfigMain;
import ru.tki.ContextHolder;
import ru.tki.helpers.FileHelper;
import ru.tki.models.actions.Action;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.tasks.Task;
import ru.tki.models.types.MissionType;
import ru.tki.models.types.PlanetType;
import ru.tki.models.types.ShipType;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Empire {
    private static Logger logger = Logger.getLogger(String.valueOf(Empire.class));

    private static final String STORAGE    = "storage";
    private static final String PLANETS    = "planets";
    private static final String RESEARCHES = "researches.json";

    private Researches           researches = new Researches();
    private List<AbstractPlanet> planets    = new ArrayList<>();

    private List<Task>   tasks   = new ArrayList<>();
    private List<Action> actions = new ArrayList<>();

    private List<FleetAction> enemyFleets = new ArrayList<>();

    private           File          storageDirectory;
    private           File          stateDirectory;
    private           boolean       isAdmiralActive;
    private           BotConfigMain config;
    private transient Gson          gson;
    private transient Galaxy        galaxy;
    private transient GalaxyHelper  galaxyHelper;

    private boolean isUnderAttack;
    private boolean researchInProgress = false;
    private Integer activeFleets       = 0;
    private Integer activeExpeditions  = 0;

    // value of hours to take resources off the planet
    private Integer productionTimeHours;

    public Empire() {
        this(ContextHolder.getBotConfigMain());
    }

    public Empire(BotConfigMain config) {
        this.config = config;

        storageDirectory = new File(STORAGE + File.separator +
                config.UNIVERSE.toLowerCase() + File.separator +
                config.LOGIN.toLowerCase());

        stateDirectory = new File(storageDirectory, "stateLogs");
        createDirectory(storageDirectory);
        createDirectory(stateDirectory);

        gson = new GsonBuilder().setPrettyPrinting().create();
        galaxyHelper = new GalaxyHelper();
        galaxy = new Galaxy(storageDirectory);

        // Timeframe for transport resources from colony to main planet
        productionTimeHours = 4;
    }

    public List<AbstractPlanet> getPlanets() {
        return planets;
    }

    public List<AbstractPlanet> getMainPlanets() {
        return planets.stream().filter(this::isPlanetMain).collect(Collectors.toList());
    }

    public void setPlanets(List<AbstractPlanet> planets) {
        this.planets = planets;
    }

    public void addPlanet(AbstractPlanet planet) {
        if (null != planet) {
            planets.add(planet);
        }
    }

    public Researches getResearches() {
        return researches;
    }

    public void setResearches(Researches researches) {
        this.researches = researches;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        if (null != tasks) {
            this.tasks = tasks;
        } else {
            this.tasks = new ArrayList<>();
        }
    }

    public void addTasks(List<Task> tasks) {
        if (null != tasks) {
            tasks.forEach(this::addTask);
        }
    }

    public void addTask(Task task) {
        if (null != task) {
            System.out.println("Add task: " + task);
            tasks.add(task);
            if (task.hasSubtask()) {
                task.getTasks().forEach(task1 -> {
                    System.out.println("With subtask: " + task1);
                });
            }
            if (config.LOG_STATE) {
                System.out.println("Save empire state to : " + saveState());
            }
        }
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public List<Action> getActions() {
        return actions;
    }

    public void addAction(Action action) {
        System.out.println("Add action: " + action.toLog());
        actions.add(action);
    }

    public void removeAction(Action action) {
        System.out.println("Remove finished action: " + action.toLog());
        actions.remove(action);
    }

    public List<FleetAction> getEnemyFleets() {
        return enemyFleets;
    }

    public void addEnemyFleet(FleetAction fleet) {
        System.out.println("Enemy fleet is coming: " + fleet.toLog());
        enemyFleets.add(fleet);
    }

    public void addEnemyFleets(List<FleetAction> enemyFleets) {
        enemyFleets.forEach(this::addEnemyFleet);
    }

    public void setEnemyFleets(List<FleetAction> enemyFleets) {
        if (enemyFleets == null) {
            this.enemyFleets = new ArrayList<>();
            return;
        }
        this.enemyFleets = enemyFleets;
    }

    public boolean isUnderAttack() {
        return isUnderAttack;
    }

    public void setUnderAttack(boolean underAttack) {
        isUnderAttack = underAttack;
    }

    public boolean isResearchInProgress() {
        return researchInProgress;
    }

    public void setResearchInProgress(boolean researchInProgress) {
        this.researchInProgress = researchInProgress;
    }

    public Integer getActiveFleets() {
        return activeFleets;
    }

    public void setActiveFleets(Integer count) {
        this.activeFleets = count;
    }

    public void addActiveFleet() {
        this.activeFleets++;
    }

    public Integer getMaxFleets() {
        return researches.getComputer() + 1 + (isAdmiralActive() ? 2 : 0);
    }

    public Integer getActiveAttackFleets() {
        Long res = actions.stream().filter(action -> action instanceof FleetAction && ((FleetAction) action).getMissionType() == MissionType.ATTACK).count();
        return res.intValue();
    }

    public void removeActiveFleet() {
        if (this.activeFleets > 0) {
            this.activeFleets--;
        }
    }

    public Integer getMaxExpeditions() {
        int bonus = isAdmiralActive ? 1 : 0;
        int level = researches.getAstrophysics();
        if (level == 0) {
            return 0;
        } else if (level < 4) {
            return 1 + bonus;
        } else if (level < 9) {
            return 2 + bonus;
        }
        return 3 + bonus;
    }

    public Integer getActiveExpeditions() {
        return activeExpeditions;
    }

    public void setActiveExpeditions(Integer count) {
        this.activeExpeditions = count;
    }

    public void addActiveExpedition() {
        this.activeExpeditions++;
    }

    public void removeActiveExpedition() {
        if (this.activeExpeditions > 0) {
            this.activeExpeditions--;
        }
    }

    public AbstractPlanet findPlanet(Coordinates coordinates) {
        for (AbstractPlanet planet : planets) {
            if (planet.equals(coordinates)) {
                return planet;
            }
        }
        return null;
    }

    public Integer getCurrentPlanetsCount() {
        return ((Long) planets.stream().filter(AbstractPlanet::isPlanet).count()).intValue();
    }

    public Integer getCurrentPlanetsWithResearchLabCount() {
        return ((Long) planets.stream().filter((planet) ->
                planet.isPlanet() && ((Planet) planet).getFactories().getResearchLab() > 0).count()).intValue();
    }

    public Integer getMaxPlanetsCount() {
        return (researches.getAstrophysics() + 1) / 2 + 1;
    }

    public boolean isMyPlanet(AbstractPlanet planet) {
        return null != findPlanet(planet.getCoordinates());
    }

    public boolean canSendFleet() {
        return activeFleets < getMaxFleets();
    }

    public boolean isLastFleetSlot() {
        return getMaxFleets() - activeFleets <= 1;
    }

    public boolean isPlanetMain(AbstractPlanet planet) {
        return getPlanetTotalFleet(planet).getCapacity() * .8 > getProductionOnPlanetInTimeframe(planet);
    }

    public boolean isPlanetUnderAttack(AbstractPlanet planet) {
        return getEnemyFleets().stream().filter(action -> action.getTargetPlanet().equals(planet) &&
                (action.getMissionType() == MissionType.ATTACK
                        || action.getMissionType() == MissionType.JOINT_ATTACK)
        ).count() > 0;
    }

    public boolean isAttackMeetsFleet(FleetAction myFleet) {
        List<FleetAction> fleets = getEnemyFleets().stream().filter(action ->
                action.getTargetPlanet().equals(myFleet.getTargetPlanet()) &&
                        (action.getMissionType() == MissionType.ATTACK
                                || action.getMissionType() == MissionType.JOINT_ATTACK)
        ).collect(Collectors.toList());
        for (FleetAction fleetAction : fleets) {
            Duration gap = Duration.between(fleetAction.getFinishTime(), myFleet.getFleetTimeToTarget()).abs();
            if (gap.minus(Duration.ofSeconds(config.FLEET_SAVE_TIMEOUT)).isNegative()) {
                return true;
            }
        }
        return false;
    }

    public Fleet getFleetForExpedition(AbstractPlanet planet) {
        Fleet fleet = new Fleet();
        Fleet existingFleet = planet.getFleet();
        if (existingFleet.getLargeCargo() > 0) {
            fleet.setLargeCargo(Math.min(Math.max(1, existingFleet.getLargeCargo() / 5), 100));
        } else if (existingFleet.getSmallCargo() > 0) {
            fleet.setSmallCargo(Math.min(Math.max(1, existingFleet.getSmallCargo() / 5), 100));
        }
        addStrongestBattleship(existingFleet, fleet);
        return fleet;
    }

    private void addStrongestBattleship(Fleet existing, Fleet target) {
        if (existing.get(ShipType.DESTROYER) > 0) {
            target.set(ShipType.DESTROYER, 1);
            return;
        }
        if (existing.get(ShipType.BATTLECRUISER) > 0) {
            target.set(ShipType.BATTLECRUISER, 1);
            return;
        }
        if (existing.get(ShipType.BATTLESHIP) > 0) {
            target.set(ShipType.BATTLESHIP, 1);
            return;
        }
        if (existing.get(ShipType.CRUISER) > 0) {
            target.set(ShipType.CRUISER, 1);
            return;
        }
        if (existing.get(ShipType.HEAVY_FIGHTER) > 0) {
            target.set(ShipType.HEAVY_FIGHTER, 1);
            return;
        }
        if (existing.get(ShipType.LIGHT_FIGHTER) > 0) {
            target.set(ShipType.LIGHT_FIGHTER, 1);
        }
    }

    //Get planet fleet minus required for move resources fleet
    public Fleet getPlanetFleetToMove(AbstractPlanet planet) {
        return planet.getFleet().deduct(planet.getFleet().getRequiredFleet(getProductionOnPlanetInTimeframe(planet)));
    }

    public AbstractPlanet selectMain() {
        return planets.stream().max(Comparator.comparingInt(AbstractPlanet::getLevel)).get();
    }

    public AbstractPlanet getClosestMainPlanet(AbstractPlanet planet) {
        Supplier<Stream<AbstractPlanet>> mainPlanets = () -> planets.stream().filter(this::isPlanetMain);
        if (mainPlanets.get().count() > 0) {
            return mainPlanets.get().reduce(planet::closer).get();
        } else {
            return selectMain();
        }
    }

    public AbstractPlanet findNewColony(AbstractPlanet planet) {
        return galaxyHelper.findNewColony(planet);
    }

    public AbstractPlanet getPlanetForExpedition(AbstractPlanet planet) {
        Coordinates coordinates = planet.getCoordinates();
        if (getMaxExpeditions() == 1 || getActiveExpeditions() == 0 || Math.random() > 0.4) {
            return new Planet(new Coordinates(coordinates.getGalaxy(), coordinates.getSystem(), 16));
        }
        Integer system = coordinates.getSystem();
        Long newSystem;
        do {
            newSystem = system + Math.round(Math.random() * 30 - 15);
        }
        while (newSystem < 1 || newSystem > 499);
        Planet planet1 = new Planet();
        planet1.setCoordinates(new Coordinates(coordinates.getGalaxy(), newSystem.intValue(), 16));
        return planet1;
    }

    public Fleet getPlanetTotalFleet(AbstractPlanet planet) {
        Supplier<Stream<FleetAction>> fleetActions = () -> actions.stream().filter(action -> action instanceof FleetAction).map(action -> (FleetAction) action);
        Fleet fleet = planet.getFleet();
        //Move fleet from one planet to another (non save moves)
        fleetActions.get().filter(fleetAction ->
                fleetAction.getTargetPlanet() != null
                        && !(fleetAction.isSaveFlight() || fleetAction.isReturnFlight())
                        && fleetAction.getTargetPlanet().equals(planet)
                        && fleetAction.getMissionType() == MissionType.KEEP)
                .map(FleetAction::getFleet)
                .reduce(fleet, Fleet::add);

        //Save fleet actions
        fleetActions.get().filter(fleetAction ->
                fleetAction.getPlanet().equals(planet)
                        && (fleetAction.isSaveFlight() || fleetAction.isReturnFlight()))
                .map(FleetAction::getFleet)
                .reduce(fleet, Fleet::add);

        fleetActions.get().filter(fleetAction -> fleetAction.getPlanet().equals(planet)
                && (fleetAction.getMissionType() == MissionType.TRANSPORT
//                Disable attack calculation to build small cargo required for normal attacks
//                || fleetAction.getMissionType() == MissionType.ATTACK
                || fleetAction.getMissionType() == MissionType.JOINT_ATTACK
                || fleetAction.getMissionType() == MissionType.RECYCLING))
                .map(FleetAction::getFleet)
                .reduce(fleet, Fleet::add);
        return fleet;
    }

    public Resources getPlanetTotalResources(AbstractPlanet planet) {
        Resources resources = planet.getResources();
        Supplier<Stream<FleetAction>> fleetActions = () -> actions.stream().filter(action -> action instanceof FleetAction).map(action -> (FleetAction) action);
        fleetActions.get().filter(fleetAction -> fleetAction.getTargetPlanet() != null
                && fleetAction.getTargetPlanet().equals(planet)
                && (fleetAction.getMissionType() == MissionType.KEEP
                || fleetAction.getMissionType() == MissionType.TRANSPORT))
                .map(FleetAction::getResources)
                .reduce(resources, Resources::add);
        return resources;
    }

    public Integer getProductionOnPlanet(AbstractPlanet planet) {
        if (getResearches().getPlasma() > 0) {
            Double res = planet.getProduction() + planet.getProduction() * 0.01 * getResearches().getPlasma();
            return res.intValue();
        } else {
            return planet.getProduction();
        }
    }

    public Integer getProductionOnPlanetInTimeframe(AbstractPlanet planet) {
        return getProductionOnPlanet(planet) * productionTimeHours;
    }

    public void save() {
        save(storageDirectory);
    }

    private void save(File directory) {
        savePlanets(directory);
        saveResearches(directory);
    }

    private void savePlanets(File directory) {
        File planetsDir = new File(directory, PLANETS);

        createDirectory(planetsDir);
        for (AbstractPlanet planet : planets) {
            savePlanet(planetsDir, planet);
        }
    }

    public void savePlanet(AbstractPlanet planet) {
        savePlanet(new File(storageDirectory, PLANETS), planet);
    }

    private void savePlanet(File directory, AbstractPlanet planet) {
        File file = new File(directory, planet.getCoordinates().getFileSafeString() + "_" + planet.getType() + ".json");
        String jsonString = gson.toJson(planet);
        FileHelper.writeToFile(file, jsonString);
    }

    public void saveResearches() {
        saveResearches(storageDirectory);
    }

    private void saveResearches(File directory) {
        File file = new File(directory, RESEARCHES);
        String jsonString = gson.toJson(researches);
        FileHelper.writeToFile(file, jsonString);
    }

    public boolean load() {
        return load(storageDirectory);
    }

    public boolean load(File directory) {
        System.out.println("Read empire details from directory: " + directory);
        if (!directory.exists()) {
            return false;
        }
        boolean isLoaded;
        isLoaded = loadPlanets(directory);
        isLoaded = isLoaded && loadResearches(directory);

        galaxy.loadGalaxy();

        return isLoaded;
    }

    public boolean loadPlanets(File directory) {
        File planetsDir = new File(directory, PLANETS);

        if (!planetsDir.exists()) {
            return false;
        }
        planets = new ArrayList<>();

        for (File fileEntry : planetsDir.listFiles()) {
            if (!fileEntry.isDirectory()) {
                if (fileEntry.getName().contains(PlanetType.PLANET.toString())) {
                    addPlanet(FileHelper.readFromFile(fileEntry, Planet.class));
                }
                if (fileEntry.getName().contains(PlanetType.MOON.toString())) {
                    addPlanet(FileHelper.readFromFile(fileEntry, Moon.class));
                }
            }
        }
        return true;
    }

    public boolean loadResearches(File directory) {
        File file = new File(directory, RESEARCHES);
        Researches researches = FileHelper.readFromFile(file, Researches.class);

        if (null != researches) {
            this.researches = researches;
            return true;
        }
        return false;
    }

    private void createDirectory(File directory) {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new InvalidArgumentException("Unable to create directory: " + directory);
            }
        }
    }

    private String saveState() {
        String fileName = "empire_" + Instant.now().toString().replace(":", "-") + ".json";
        File directory = new File(stateDirectory, fileName);
        String jsonString = gson.toJson(this);
        FileHelper.writeToFile(directory, jsonString);
        return fileName;
    }

    public Integer getProductionTimeHours() {
        return productionTimeHours;
    }

    public void setProductionTimeHours(Integer productionTimeHours) {
        this.productionTimeHours = productionTimeHours;
    }

    public Galaxy getGalaxy() {
        return galaxy;
    }

    public boolean isAdmiralActive() {
        return isAdmiralActive;
    }

    public void setAdmiralActive(boolean admiralActive) {
        isAdmiralActive = admiralActive;
    }
}
