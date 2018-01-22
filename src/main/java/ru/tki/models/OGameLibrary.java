package ru.tki.models;

import ru.tki.models.types.BuildingType;
import ru.tki.models.types.FactoryType;
import ru.tki.models.types.ResearchType;
import ru.tki.models.types.ShipType;

import java.util.HashMap;
import java.util.Map;

public class OGameLibrary {

    private static final Map<BuildingType, Resources> buildings = new HashMap<BuildingType, Resources>() {{
        put(BuildingType.METAL_MINE, new Resources(60, 15));
        put(BuildingType.CRYSTAL_MINE, new Resources(48, 24));
        put(BuildingType.DEUTERIUM_MINE, new Resources(225, 75));
        put(BuildingType.SOLAR_PLANT, new Resources(75, 30));
        put(BuildingType.SOLAR_SATELLITE, new Resources(0, 2000, 500));
        put(BuildingType.METAL_STORAGE, new Resources(1000, 0));
        put(BuildingType.CRYSTAL_STORAGE, new Resources(1000, 500));
        put(BuildingType.DEUTERIUM_STORAGE, new Resources(1000, 1000));
    }};

    private static final Map<FactoryType, Resources> factories = new HashMap<FactoryType, Resources>() {{
        put(FactoryType.ROBOTS_FACTORY, new Resources(400, 120, 200));
        put(FactoryType.NANITE_FACTORY, new Resources(1000000, 500000, 100000));
        put(FactoryType.SHIPYARD, new Resources(400, 200, 100));
        put(FactoryType.RESEARCH_LAB, new Resources(200, 400, 200));
        put(FactoryType.TERRAFORMER, new Resources(0, 50000, 100000));
        put(FactoryType.ALLIANCE_WAREHOUSE, new Resources(20000, 40000));
        put(FactoryType.MISSILE_SILOS, new Resources(20000, 20000, 1000));
    }};

    private static final Map<ResearchType, Resources> researches = new HashMap<ResearchType, Resources>() {{
        put(ResearchType.ENERGY, new Resources(0, 800, 400));
        put(ResearchType.LASER, new Resources(200, 100));
        put(ResearchType.ION, new Resources(1000, 300, 100));
        put(ResearchType.PLASMA, new Resources(2000, 4000, 1000));
        put(ResearchType.HYPER, new Resources(0, 4000, 2000));
        put(ResearchType.REACTIVE_ENGINE, new Resources(400, 0, 600));
        put(ResearchType.IMPULSE_ENGINE, new Resources(2000, 4000, 600));
        put(ResearchType.HYPER_ENGINE, new Resources(10000, 20000, 6000));
        put(ResearchType.ESPIONAGE, new Resources(200, 1000, 200));
        put(ResearchType.COMPUTER, new Resources(0, 400, 600));
        put(ResearchType.ASTROPHYSICS, new Resources(4000, 8000, 4000));
        put(ResearchType.MIS, new Resources(240000, 400000, 160000));
        put(ResearchType.GRAVITY, new Resources(0, 0, 0, 300000));
        put(ResearchType.WEAPON, new Resources(800, 200));
        put(ResearchType.SHIELDS, new Resources(0, 800, 400));
        put(ResearchType.ENERGY, new Resources(200, 600));
        put(ResearchType.ARMOR, new Resources(1000, 0));
    }};

    public static Resources getBuildingPrice(BuildingType type, Integer currentLevel) {
        switch (type) {
            case METAL_MINE:
            case CRYSTAL_MINE:
            case DEUTERIUM_MINE:
            case SOLAR_PLANT:
                return buildings.get(type).multiply(Math.pow(1.5, currentLevel));
            case SOLAR_SATELLITE:
                return buildings.get(type);
            case METAL_STORAGE:
            case CRYSTAL_STORAGE:
            case DEUTERIUM_STORAGE:
                return buildings.get(type).multiply(Math.pow(2, currentLevel));
        }
        return null;
    }

    public static Resources getFactoryPrice(FactoryType type, Integer currentLevel) {
        switch (type) {
            case ROBOTS_FACTORY:
            case SHIPYARD:
            case RESEARCH_LAB:
            case ALLIANCE_WAREHOUSE:
            case MISSILE_SILOS:
            case NANITE_FACTORY:
            case TERRAFORMER:
                return factories.get(type).multiply(Math.pow(2, currentLevel));
            case SPACE_DOCK:
                return factories.get(type).multiply(Math.pow(5, currentLevel));
        }
        return null;
    }

    public static Resources getResearchPrice(ResearchType type, Integer currentLevel) {
        switch (type) {
            case ENERGY:
            case LASER:
            case ION:
            case HYPER:
            case PLASMA:
            case REACTIVE_ENGINE:
            case IMPULSE_ENGINE:
            case HYPER_ENGINE:
            case ESPIONAGE:
            case COMPUTER:
            case ASTROPHYSICS:
            case MIS:
            case GRAVITY:
            case WEAPON:
            case SHIELDS:
            case ARMOR:
                return researches.get(type).multiply(Math.pow(2, currentLevel));
        }
        return null;
    }

    public static Boolean canBuild(Empire empire, Planet planet, ShipType type) {
        switch (type) {
            case LIGHT_FIGHTER:
                return planet.getFactories().getShipyard() >= 1 && empire.getResearches().getReactiveEngine() >= 1;
            case HEAVY_FIGHTER:
                return planet.getFactories().getShipyard() >= 3
                        && empire.getResearches().getImpulseEngine() >= 2
                        && empire.getResearches().getArmor() >= 2;
            case CRUISER:
                return planet.getFactories().getShipyard() >= 5
                        && empire.getResearches().getImpulseEngine() >= 4
                        && empire.getResearches().getIon() >= 2;
            case BATTLESHIP:
                return planet.getFactories().getShipyard() >= 7
                        && empire.getResearches().getHyperEngine() >= 4;
            case BATTLECRUISER:
                return planet.getFactories().getShipyard() >= 8
                        && empire.getResearches().getLaser() >= 12
                        && empire.getResearches().getHyper() >= 5
                        && empire.getResearches().getHyperEngine() >= 5;
            case BOMBER:
                return planet.getFactories().getShipyard() >= 8
                        && empire.getResearches().getImpulseEngine() >= 6
                        && empire.getResearches().getPlasma() >= 5;
            case DESTROYER:
                return planet.getFactories().getShipyard() >= 9
                        && empire.getResearches().getHyperEngine() >= 6
                        && empire.getResearches().getHyper() >= 5;
            case DEATHSTAR:
                return false;
            case SMALL_CARGO:
                return planet.getFactories().getShipyard() >= 2
                        && empire.getResearches().getReactiveEngine() >= 1;
            case LARGE_CARGO:
                return planet.getFactories().getShipyard() >= 4
                        && empire.getResearches().getReactiveEngine() >= 6;
            case COLONY_SHIP:
                return planet.getFactories().getShipyard() >= 4
                        && empire.getResearches().getImpulseEngine() >= 3;
            case RECYCLER:
                return planet.getFactories().getShipyard() >= 4
                        && empire.getResearches().getReactiveEngine() >= 6
                        && empire.getResearches().getShields() >= 2;
            case ESPIONAGE_PROBE:
                return planet.getFactories().getShipyard() >= 3
                        && empire.getResearches().getReactiveEngine() >= 3
                        && empire.getResearches().getEspionage() >= 2;
            case SOLAR_SATELLITE:
                return planet.getFactories().getShipyard() >= 2;
        }
        return false;
    }

    public static Boolean canBuild(Empire empire, Planet planet, FactoryType type) {
        switch (type) {
            case ROBOTS_FACTORY:
            case RESEARCH_LAB:
            case ALLIANCE_WAREHOUSE:
                return true;
            case SHIPYARD:
                return planet.getFactories().getRobotsFactory() >= 2;
            case MISSILE_SILOS:
                return planet.getFactories().getShipyard() >= 1;
            case NANITE_FACTORY:
                return planet.getFactories().getRobotsFactory() >= 10
                        && empire.getResearches().getComputer() >= 10;
            case TERRAFORMER:
                return planet.getFactories().getNaniteFactory() >= 1
                        && empire.getResearches().getEnergy() >= 12;
            case SPACE_DOCK:
                return planet.getFactories().getShipyard() >= 2;
        }
        return false;
    }

    public static Boolean canBuild(Empire empire, Planet planet, ResearchType type) {
        switch (type) {
            case ENERGY:
                return planet.getFactories().getResearchLab() >= 1;
            case LASER:
                return empire.getResearches().getEnergy() >= 2;
            case ION:
                return planet.getFactories().getResearchLab() >= 4
                        && empire.getResearches().getLaser() >= 5
                        && empire.getResearches().getEnergy() >= 4;
            case HYPER:
                return planet.getFactories().getResearchLab() >= 7
                        && empire.getResearches().getShields() >= 5
                        && empire.getResearches().getEnergy() >= 5;
            case PLASMA:
                return empire.getResearches().getLaser() >= 10
                        && empire.getResearches().getIon() >= 5
                        && empire.getResearches().getEnergy() >= 8;
            case REACTIVE_ENGINE:
                return planet.getFactories().getResearchLab() >= 4
                        && empire.getResearches().getLaser() >= 5
                        && empire.getResearches().getEnergy() >= 4;
            case IMPULSE_ENGINE:
                return planet.getFactories().getResearchLab() >= 2
                        && empire.getResearches().getEnergy() >= 1;
            case HYPER_ENGINE:
                return empire.getResearches().getHyper() >= 3;
            case ESPIONAGE:
                return planet.getFactories().getResearchLab() >= 3;
            case COMPUTER:
                return planet.getFactories().getResearchLab() >= 1;
            case ASTROPHYSICS:
                return empire.getResearches().getEspionage() >= 4
                        && empire.getResearches().getImpulseEngine() >= 3;
            case MIS:
                return planet.getFactories().getResearchLab() >= 10
                        && empire.getResearches().getHyper() >= 8
                        && empire.getResearches().getComputer() >= 8;
            case GRAVITY:
                return false;
            case WEAPON:
                return planet.getFactories().getResearchLab() >= 4;
            case SHIELDS:
                return planet.getFactories().getResearchLab() >= 6
                        && empire.getResearches().getEnergy() >= 3;
            case ARMOR:
                return planet.getFactories().getResearchLab() >= 2;
        }
        return false;
    }
}
