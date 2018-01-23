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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
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

    private File directory;
    private Gson gson;

    private boolean isUnderAttack;
    private boolean researchInProgress = false;
    private Integer activeFleets       = 0;

    // value of hours to take resources off the planet
    private Integer productionTimeHours;

    public Empire() {
        directory = new File(STORAGE + File.separator +
                ContextHolder.getBotConfigMain().getUniverse().toLowerCase() + File.separator +
                ContextHolder.getBotConfigMain().getLogin().toLowerCase());
        gson = new GsonBuilder().setPrettyPrinting().create();

        // Timeframe for transport resources from colony to main planet
        productionTimeHours = 4;
    }

    public List<AbstractPlanet> getPlanets() {
        return planets;
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
        System.out.println("Add task: " + task);
        tasks.add(task);
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

    public void addActiveFleet() {
        this.activeFleets++;
    }

    public void removeActiveFleet(){
        this.activeFleets--;
    }

    public Integer getMaxFleets(){
        //Actually +1 has to be here but need to keep at least one slot for save fleet in future
        return researches.getComputer();
    }

    public boolean canSendFleet(){
        return activeFleets < getMaxFleets();
    }

    public boolean isPlanetMain(AbstractPlanet planet) {
        return getPlanetTotalFleet(planet).getCapacity() * .95 > getProductionOnPlanetInTimeframe(planet);
    }

    public AbstractPlanet selectMain() {
        return planets.stream().max((a, b) -> a.getLevel() - b.getLevel()).get();
    }

    public AbstractPlanet getClosestMainPlanet(AbstractPlanet planet) {
        Stream<AbstractPlanet> mainPlanets = planets.stream().filter(this::isPlanetMain);
        if (mainPlanets.count() > 0) {
            return mainPlanets.reduce(planet::closer).get();
        } else {
            return selectMain();
        }
    }

    public Fleet getPlanetTotalFleet(AbstractPlanet planet) {
        Fleet fleet = planet.getFleet();
        actions.stream().filter(action -> action instanceof FleetAction).map(action -> (FleetAction) action)
                .filter(fleetAction -> fleetAction.getTargetPlanet().equals(planet)
                        && fleetAction.getMissionType() == MissionType.KEEP)
                .map(FleetAction::getFleet)
                .reduce(fleet, Fleet::add);
        actions.stream().filter(action -> action instanceof FleetAction).map(action -> (FleetAction) action)
                .filter(fleetAction -> fleetAction.getPlanet().equals(planet)
                        && (fleetAction.getMissionType() == MissionType.TRANSPORT
                        || fleetAction.getMissionType() == MissionType.ATTACK
                        || fleetAction.getMissionType() == MissionType.EXPEDITION))
                .map(FleetAction::getFleet)
                .reduce(fleet, Fleet::add);
        return fleet;
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
        save(directory);
    }

    private void save(File directory) {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new InvalidArgumentException("Unable to create directory: " + directory);
            }
        }
        savePlanets(directory);
        saveResearches(directory);
    }

    private void savePlanets(File directory) {
        File planetsDir = new File(directory, PLANETS);

        if (!planetsDir.exists()) {
            if (!planetsDir.mkdirs()) {
                throw new InvalidArgumentException("Unable to create directory: " + planetsDir);
            }
        }
        for (AbstractPlanet planet : planets) {
            savePlanet(planetsDir, planet);
        }
    }

    public void savePlanet(AbstractPlanet planet) {
        savePlanet(new File(directory, PLANETS), planet);
    }

    private void savePlanet(File directory, AbstractPlanet planet) {
        File file = new File(directory, planet.getCoordinates().getFileSafeString() + "_" + planet.getType() + ".json");
        String jsonString = gson.toJson(planet);
        writeToFile(file, jsonString);
    }

    public void saveResearches() {
        saveResearches(directory);
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
        return load(directory);
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
