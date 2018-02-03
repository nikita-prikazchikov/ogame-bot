package ru.tki.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openqa.selenium.InvalidArgumentException;
import ru.tki.helpers.FileHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Galaxy {
    private static final String galaxyFolder = "galaxy";
    public static Integer MIN_SYSTEM = 1;
    public static Integer MAX_SYSTEM = 499;
    public static Integer SECTOR_SIZE = 50;

    private List<EnemyPlanet> enemyPlanets = new LinkedList<>();
    private List<GalaxySector> sectors = new ArrayList<>(60);

    private           File galaxyDirectory;
    private transient Gson gson;

    public Galaxy(File storageDirectory) {
        this.galaxyDirectory = new File(storageDirectory, galaxyFolder);
        gson = new GsonBuilder().setPrettyPrinting().create();
        if(!loadGalaxy()){
            createGalaxy();
        }
    }

    public void addPlanet(EnemyPlanet planet){
        if(enemyPlanets.contains(planet)){
            enemyPlanets.remove(planet);
        }
        enemyPlanets.add(planet);
        savePlanet(planet);
    }

    public void removePlanet(EnemyPlanet planet){
        if(enemyPlanets.contains(planet)){
            enemyPlanets.remove(planet);
            deletePlanet(planet);
        }
    }

    public boolean hasPlanet(AbstractPlanet planet){
        return enemyPlanets.contains(planet);
    }

    public GalaxySector getPlanetSector(AbstractPlanet planet){
        return sectors.stream().filter(sector -> sector.planetInSector(planet)).findFirst().get();
    }

    public void addSector(GalaxySector sector){
        if(sectors.contains(sector)){
            sectors.remove(sector);
        }
        sectors.add(sector);
    }

    public void updateSector(GalaxySector sector){
        sector.setUpdatedNow();
        addSector(sector);
        saveSector(sector);
    }

    public boolean loadGalaxy(){
        if (!galaxyDirectory.exists() ||  galaxyDirectory.listFiles().length == 0) {
            return false;
        }

        for (File fileEntry : galaxyDirectory.listFiles()) {
            if (!fileEntry.isDirectory()) {
                if (fileEntry.getName().contains("planet")) {
                    enemyPlanets.add(FileHelper.readFromFile(fileEntry, EnemyPlanet.class));
                }
                if (fileEntry.getName().contains("sector")) {
                    addSector(FileHelper.readFromFile(fileEntry, GalaxySector.class));
                }
            }
        }
        return true;
    }

    private void createGalaxy(){
        for(int galaxy = 1; galaxy < 6; galaxy++) {
            for (int system = 0; system < MAX_SYSTEM; system += SECTOR_SIZE) {
                int start = system + 1;
                int end = Math.min(system + SECTOR_SIZE, MAX_SYSTEM);
                GalaxySector sector = new GalaxySector(new Coordinates(galaxy, start, 1), new Coordinates(galaxy, end, 1));
                addSector(sector);
                saveSector(sector);
            }
        }
    }

    private void saveSector(GalaxySector sector){
        if (!galaxyDirectory.exists()) {
            if (!galaxyDirectory.mkdirs()) {
                throw new InvalidArgumentException("Unable to create directory: " + galaxyDirectory);
            }
        }
        String jsonString = gson.toJson(sector);
        FileHelper.writeToFile(getSectorFilename(sector), jsonString);
    }

    private File getSectorFilename(GalaxySector sector){
        return new File(galaxyDirectory, "sector_" + sector.getStart().getFileSafeString()+ "__" + sector.getEnd().getFileSafeString() + ".json");
    }

    private void savePlanet(EnemyPlanet planet){
        if (!galaxyDirectory.exists()) {
            if (!galaxyDirectory.mkdirs()) {
                throw new InvalidArgumentException("Unable to create directory: " + galaxyDirectory);
            }
        }
        String jsonString = gson.toJson(planet);
        FileHelper.writeToFile(getPlanetFilename(planet), jsonString);
    }

    private File getPlanetFilename(EnemyPlanet planet){
        return new File(galaxyDirectory, "planet_" + planet.getCoordinates().getFileSafeString() + ".json");
    }

    private void deletePlanet(EnemyPlanet planet){
        File file = getPlanetFilename(planet);
        if(file.exists()){
            boolean deleted = file.delete();
        }
        if(enemyPlanets.contains(planet)){
            enemyPlanets.remove(planet);
        }
    }
}


