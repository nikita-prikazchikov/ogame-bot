package ru.tki.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openqa.selenium.InvalidArgumentException;
import ru.tki.ContextHolder;
import ru.tki.models.actions.Action;
import ru.tki.models.tasks.Task;
import ru.tki.models.types.PlanetType;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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

    public Empire() {
        directory = new File(STORAGE + File.separator +
                ContextHolder.getBotConfigMain().getUniverse().toLowerCase() + File.separator +
                ContextHolder.getBotConfigMain().getLogin().toLowerCase());
        gson = new GsonBuilder().setPrettyPrinting().create();
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
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public List<Action> getActions() {
        return actions;
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void removeAction(Action action) {
        actions.add(action);
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

    public void save() {
        save(directory);
    }

    public void save(File directory) {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new InvalidArgumentException("Unable to create directory: " + directory);
            }
        }
        savePlanets(directory);
        saveResearches(directory);
    }

    public void savePlanets(File directory) {
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

    public void savePlanet(AbstractPlanet planet){
        savePlanet(new File(directory, PLANETS), planet);
    }

    public void savePlanet(File directory, AbstractPlanet planet){
        File file = new File(directory, planet.getCoordinates().getFileSafeString() + "_" + planet.getType() + ".json");
        String jsonString = gson.toJson(planet);
        writeToFile(file, jsonString);
    }

    public void saveResearches(File directory) {
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
        ;
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
}
