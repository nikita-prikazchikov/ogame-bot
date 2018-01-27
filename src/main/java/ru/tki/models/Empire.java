package ru.tki.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openqa.selenium.InvalidArgumentException;
import ru.tki.ContextHolder;
import ru.tki.models.actions.Action;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.tasks.Task;
import ru.tki.models.types.MissionType;
import ru.tki.models.types.PlanetType;

import java.io.*;
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

    private           File   storageDirectory;
    private           File   stateDirectory;
    private transient Gson   gson;
    private transient Galaxy galaxy;

    private boolean isUnderAttack;
    private boolean researchInProgress = false;
    private Integer activeFleets       = 0;
    private Integer activeExpeditions  = 0;

    // value of hours to take resources off the planet
    private Integer productionTimeHours;

    private Boolean doLogState = false;

    public Empire() {
        storageDirectory = new File(STORAGE + File.separator +
                ContextHolder.getBotConfigMain().getUniverse().toLowerCase() + File.separator +
                ContextHolder.getBotConfigMain().getLogin().toLowerCase());

        stateDirectory = new File(storageDirectory, "stateLogs");
        createDirectory(storageDirectory);
        createDirectory(stateDirectory);

        gson = new GsonBuilder().setPrettyPrinting().create();
        galaxy = new Galaxy();

        // Timeframe for transport resources from colony to main planet
        productionTimeHours = 4;
        doLogState = ContextHolder.getBotConfigMain().getLogState();
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

    public void addTask(Task task) {
        if (null != task) {
            System.out.println("Add task: " + task);
            tasks.add(task);
            if (null != task.getSubtask()) {
                System.out.println("With subtask: " + task.getSubtask());
            }
            if (doLogState) {
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

    public void removeActiveFleet() {
        if (this.activeFleets > 0) {
            this.activeFleets--;
        }
    }

    public Integer getMaxFleets() {
        //Actually +1 has to be here but need to keep at least one slot for save fleet in future
        return researches.getComputer();
    }

    public Integer getMaxExpeditions() {
        int level = researches.getAstrophysics();
        if (level == 0) {
            return 0;
        } else if (level < 4) {
            return 1;
        } else if (level < 9) {
            return 2;
        }
        return 3;
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

    public Integer getMaxPlanetsCount() {
        return (researches.getAstrophysics() + 1) / 2 + 1;
    }

    public boolean isMyPlanet(AbstractPlanet planet) {
        return null != findPlanet(planet.getCoordinates());
    }

    public boolean canSendFleet() {
        return activeFleets < getMaxFleets();
    }

    public boolean isPlanetMain(AbstractPlanet planet) {
        return getPlanetTotalFleet(planet).getCapacity() * .95 > getProductionOnPlanetInTimeframe(planet);
    }

    public Fleet getFleetForExpedition(AbstractPlanet planet) {
        Fleet fleet = new Fleet();
        Fleet existingFleet = planet.getFleet();
        if (existingFleet.getLargeCargo() > 0) {
            fleet.setLargeCargo(Math.max(1, existingFleet.getLargeCargo() / 5));
        } else if (existingFleet.getSmallCargo() > 0) {
            fleet.setSmallCargo(Math.max(1, existingFleet.getSmallCargo() / 5));
        }
        return fleet;
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
        return galaxy.findNewColony(planet);
    }

    public AbstractPlanet getPlanetForExpedition(AbstractPlanet planet) {
        Coordinates coordinates = planet.getCoordinates();
        if (getMaxExpeditions() == 1 || getActiveExpeditions() == 0 || Math.random() > 0.4) {
            return new Planet(new Coordinates(
                    Integer.parseInt(coordinates.getGalaxy()),
                    Integer.parseInt(coordinates.getSystem()),
                    16
            ));
        }
        Long system = Long.parseLong(coordinates.getSystem());
        Long newSystem;
        do {
            newSystem = system + Math.round(Math.random() * 30 - 15);
        }
        while (newSystem < 1 || newSystem > 499);
        Planet planet1 = new Planet();
        planet1.setCoordinates(new Coordinates(Integer.parseInt(coordinates.getGalaxy()), newSystem.intValue(), 16));
        return planet1;
    }

    public Fleet getPlanetTotalFleet(AbstractPlanet planet) {
        Supplier<Stream<FleetAction>> fleetActions = () -> actions.stream().filter(action -> action instanceof FleetAction).map(action -> (FleetAction) action);
        Fleet fleet = planet.getFleet();
        fleetActions.get().filter(fleetAction -> fleetAction.getTargetPlanet() != null && fleetAction.getTargetPlanet().equals(planet)
                && fleetAction.getMissionType() == MissionType.KEEP)
                .map(FleetAction::getFleet)
                .reduce(fleet, Fleet::add);
        fleetActions.get().filter(fleetAction -> fleetAction.getPlanet().equals(planet)
                && (fleetAction.getMissionType() == MissionType.TRANSPORT
                || fleetAction.getMissionType() == MissionType.ATTACK
                || fleetAction.getMissionType() == MissionType.EXPEDITION))
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
            Double res = planet.getProduction() * 0.01 * getResearches().getPlasma();
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
        writeToFile(file, jsonString);
    }

    public void saveResearches() {
        saveResearches(storageDirectory);
    }

    private void saveResearches(File directory) {
        File file = new File(directory, RESEARCHES);
        String jsonString = gson.toJson(researches);
        writeToFile(file, jsonString);
    }

    private void writeToFile(File file, String content) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean load() {
        return load(storageDirectory);
    }

    public boolean load(File directory) {
        logger.info("Read empire details from directory: " + directory);
        if (!directory.exists()) {
            return false;
        }
        boolean isLoaded;
        isLoaded = loadPlanets(directory);
        isLoaded = isLoaded && loadResearches(directory);
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
                    addPlanet(readFromFile(fileEntry, Planet.class));
                }
                if (fileEntry.getName().contains(PlanetType.MOON.toString())) {
                    addPlanet(readFromFile(fileEntry, Moon.class));
                }
            }
        }
        return true;
    }

    public boolean loadResearches(File directory) {
        File file = new File(directory, RESEARCHES);
        Researches researches = readFromFile(file, Researches.class);

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
        writeToFile(directory, jsonString);
        return fileName;
    }

    private <T> T readFromFile(File file, Class<T> classOfT) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            return gson.fromJson(br, classOfT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getProductionTimeHours() {
        return productionTimeHours;
    }

    public void setProductionTimeHours(Integer productionTimeHours) {
        this.productionTimeHours = productionTimeHours;
    }

}
