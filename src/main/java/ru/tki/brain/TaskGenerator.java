package ru.tki.brain;

import ru.tki.BotConfigMain;
import ru.tki.ContextHolder;
import ru.tki.models.*;
import ru.tki.models.tasks.*;
import ru.tki.models.types.*;

import java.util.HashMap;
import java.util.Map;

public class TaskGenerator {

    // Rules for building
    // Keep buildings flat amount on planet
    // Solar main level X
    // Metal = X Crystal = X - 2 Deuterium X / 2
    // MS = M / 3 CS = C / 3 DS = D / 3

    private static final Map<DefenceType, Integer> optimalDefence = new HashMap<DefenceType, Integer>() {{
        put(DefenceType.ROCKET, 60);
        put(DefenceType.LIGHT_LASER, 60);
        put(DefenceType.HEAVY_LASER, 15);
        put(DefenceType.GAUSS, 3);
        put(DefenceType.ION, 0);
        put(DefenceType.PLASMA, 1);
        put(DefenceType.SMALL_SHIELD, 1);
        put(DefenceType.BIG_SHIELD, 1);
        put(DefenceType.DEFENCE_MISSILE, 1);
        put(DefenceType.MISSILE, 1);
    }};

    Empire        empire;
    BotConfigMain botConfig;

    public TaskGenerator(Empire empire) {
        this.empire = empire;
        this.botConfig = ContextHolder.getBotConfigMain();
    }

    public Task getTask(Planet planet) {
        Resources resources = planet.getResources();
        Researches researches = empire.getResearches();

        if (!planet.getShipyardBusy()) {
            if (planet.getResources().getEnergy() < 0) {
                if (OGameLibrary.canBuild(empire, planet, ShipType.SOLAR_SATELLITE)
                        && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.SOLAR_SATELLITE, 1))) {
                    Task task = new ShipyardTask(empire, planet, ShipType.SOLAR_SATELLITE, 1);
                    task.setSubtask(new UpdatePlanetInfoTask(empire, planet));
                    return task;
                }
            }
        }

        Task task = getTaskBuilding(planet);
        if (task != null) return task;

        task = getTaskResearches(planet, researches);
        if (task != null) return task;

        task = getRequiredCargoTask(planet);
        if (task != null) {
            task.setSubtask(new UpdatePlanetInfoTask(empire, planet));
            return task;
        }
        task = getDefenceTask(planet);
        if (task != null) {
            task.setSubtask(new UpdatePlanetInfoTask(empire, planet));
            return task;
        }

        return null;
    }

    public Task getFleetTask(Planet planet){
        Task task = getFleetMoveResourcesTask(planet);
        if (task != null) return task;

        return null;
    }

    private Task getRequiredCargoTask(Planet planet) {
        Integer currentMax = planet.getLevel();
        Resources resources = planet.getResources();
        if (!planet.getShipyardBusy() && currentMax > 12 && botConfig.getBuildFleet()) {
            Integer planetProduction = empire.getProductionOnPlanetInTimeframe(planet);
            Integer buildAmount = Math.max(planetProduction/(5000 * 5 ), 2);
            if(empire.getPlanetTotalFleet(planet).getCapacity() < planetProduction
                    && OGameLibrary.canBuild(empire, planet, ShipType.SMALL_CARGO)
                    && resources.isEnoughFor(OGameLibrary.getShipPrice(ShipType.SMALL_CARGO).multiply(buildAmount))){
                return new ShipyardTask(empire, planet, ShipType.SMALL_CARGO, buildAmount);
            }
        }
        return null;
    }


    private Task getFleetMoveResourcesTask(Planet planet){
        if(empire.isPlanetMain(planet)){
            return null;
        }
        AbstractPlanet main = empire.getClosestMainPlanet(planet);
        if( !main.equals(planet) && planet.getResources().getCapacity() > empire.getProductionOnPlanetInTimeframe(planet)
                && empire.canSendFleet()){
            return new FleetTask(empire, planet, empire.getClosestMainPlanet(planet),
                    planet.getFleet().getRequiredFleet(planet.getResources()),
                    MissionType.TRANSPORT,
                    planet.getResources());
        }
        return null;
    }

    private Task getDefenceTask(Planet planet) {
        Integer currentMax = planet.getLevel();
        Resources resources = planet.getResources();
        Defence defence = planet.getDefence();
        if (!planet.getShipyardBusy() && currentMax > 15 && botConfig.getBuildDefence()) {
            Integer multiplier = currentMax / 5;
            if (defence.getRocket() < optimalDefence.get(DefenceType.ROCKET) * multiplier
                    && OGameLibrary.canBuild(empire, planet, DefenceType.ROCKET)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.ROCKET).multiply(5))) {
                return new DefenceTask(planet, DefenceType.ROCKET, 5);
            }
            if (defence.getLightLaser() < optimalDefence.get(DefenceType.LIGHT_LASER) * multiplier
                    && OGameLibrary.canBuild(empire, planet, DefenceType.LIGHT_LASER)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.LIGHT_LASER).multiply(5))) {
                return new DefenceTask(planet, DefenceType.LIGHT_LASER, 5);
            }
            if (defence.getHeavyLaser() < optimalDefence.get(DefenceType.HEAVY_LASER) * multiplier
                    && OGameLibrary.canBuild(empire, planet, DefenceType.HEAVY_LASER)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.HEAVY_LASER))) {
                return new DefenceTask(planet, DefenceType.HEAVY_LASER, 1);
            }
            if (defence.getGauss() < optimalDefence.get(DefenceType.GAUSS) * multiplier
                    && OGameLibrary.canBuild(empire, planet, DefenceType.GAUSS)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.GAUSS))) {
                return new DefenceTask(planet, DefenceType.GAUSS, 1);
            }
            if (defence.getPlasma() < optimalDefence.get(DefenceType.PLASMA) * multiplier
                    && OGameLibrary.canBuild(empire, planet, DefenceType.PLASMA)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.PLASMA))) {
                return new DefenceTask(planet, DefenceType.PLASMA, 1);
            }
            if (defence.getSmallShield() < 1
                    && OGameLibrary.canBuild(empire, planet, DefenceType.SMALL_SHIELD)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.SMALL_SHIELD))) {
                return new DefenceTask(planet, DefenceType.SMALL_SHIELD, 1);
            }
            if (defence.getBigShield() < 1
                    && OGameLibrary.canBuild(empire, planet, DefenceType.BIG_SHIELD)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.BIG_SHIELD))) {
                return new DefenceTask(planet, DefenceType.BIG_SHIELD, 1);
            }

            if (defence.getDefenceMissile() < planet.getFactories().getMissileSilos() * 10
                    && OGameLibrary.canBuild(empire, planet, DefenceType.DEFENCE_MISSILE)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.DEFENCE_MISSILE))) {
                return new DefenceTask(planet, DefenceType.DEFENCE_MISSILE, 1);
            }
        }
        return null;
    }

    private Task getTaskResearches(Planet planet, Researches researches) {
        Integer currentMax = planet.getLevel();
        Resources resources = planet.getResources();
        if (!empire.isResearchInProgress() && currentMax > 12 && botConfig.getDoResearches()) {
            if (researches.getComputer() <= 20
                    && OGameLibrary.canBuild(empire, planet, ResearchType.COMPUTER)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.COMPUTER, researches.getComputer()))) {
                return new ResearchTask(empire, planet, ResearchType.COMPUTER);
            }
            if (researches.getAstrophysics() <= 20
                    && OGameLibrary.canBuild(empire, planet, ResearchType.ASTROPHYSICS)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.ASTROPHYSICS, researches.getAstrophysics()))) {
                return new ResearchTask(empire, planet, ResearchType.ASTROPHYSICS);
            }
            if (researches.getPlasma() <= 20
                    && OGameLibrary.canBuild(empire, planet, ResearchType.PLASMA)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.PLASMA, researches.getPlasma()))) {
                return new ResearchTask(empire, planet, ResearchType.PLASMA);
            }
            if (researches.getEspionage() <= 20
                    && OGameLibrary.canBuild(empire, planet, ResearchType.ESPIONAGE)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.ESPIONAGE, researches.getEspionage()))) {
                return new ResearchTask(empire, planet, ResearchType.ESPIONAGE);
            }
            if (researches.getHyper() <= 8
                    && OGameLibrary.canBuild(empire, planet, ResearchType.HYPER)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.HYPER, researches.getHyper()))) {
                return new ResearchTask(empire, planet, ResearchType.HYPER);
            }
            if (researches.getIon() <= 5
                    && OGameLibrary.canBuild(empire, planet, ResearchType.ION)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.ION, researches.getIon()))) {
                return new ResearchTask(empire, planet, ResearchType.ION);
            }
            if (researches.getMis() <= 7
                    && OGameLibrary.canBuild(empire, planet, ResearchType.MIS)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.MIS, researches.getMis()))) {
                return new ResearchTask(empire, planet, ResearchType.MIS);
            }

            if (researches.getHyperEngine() <= 20
                    && OGameLibrary.canBuild(empire, planet, ResearchType.HYPER_ENGINE)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.HYPER_ENGINE, researches.getHyperEngine()))) {
                return new ResearchTask(empire, planet, ResearchType.HYPER_ENGINE);
            }
            if (researches.getImpulseEngine() <= 20
                    && OGameLibrary.canBuild(empire, planet, ResearchType.IMPULSE_ENGINE)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.IMPULSE_ENGINE, researches.getImpulseEngine()))) {
                return new ResearchTask(empire, planet, ResearchType.IMPULSE_ENGINE);
            }
            if (researches.getReactiveEngine() <= 20
                    && OGameLibrary.canBuild(empire, planet, ResearchType.REACTIVE_ENGINE)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.REACTIVE_ENGINE, researches.getReactiveEngine()))) {
                return new ResearchTask(empire, planet, ResearchType.REACTIVE_ENGINE);
            }
            if (researches.getWeapon() <= 25
                    && OGameLibrary.canBuild(empire, planet, ResearchType.WEAPON)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.WEAPON, researches.getEspionage()))) {
                return new ResearchTask(empire, planet, ResearchType.WEAPON);
            }
            if (researches.getShields() <= 25
                    && OGameLibrary.canBuild(empire, planet, ResearchType.SHIELDS)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.SHIELDS, researches.getShields()))) {
                return new ResearchTask(empire, planet, ResearchType.SHIELDS);
            }
            if (researches.getArmor() <= 25
                    && OGameLibrary.canBuild(empire, planet, ResearchType.ARMOR)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.ARMOR, researches.getArmor()))) {
                return new ResearchTask(empire, planet, ResearchType.ARMOR);
            }
            if (researches.getLaser() <= 12
                    && OGameLibrary.canBuild(empire, planet, ResearchType.LASER)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.LASER, researches.getLaser()))) {
                return new ResearchTask(empire, planet, ResearchType.LASER);
            }
            if (researches.getEnergy() <= 12
                    && OGameLibrary.canBuild(empire, planet, ResearchType.ENERGY)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.ENERGY, researches.getEnergy()))) {
                return new ResearchTask(empire, planet, ResearchType.ENERGY);
            }
        }
        return null;
    }

    private Task getTaskBuilding(Planet planet) {
        Integer currentMax = planet.getLevel();
        Resources resources = planet.getResources();
        Buildings buildings = planet.getBuildings();
        Factories factories = planet.getFactories();
        if (!planet.getBuildInProgress() && botConfig.getBuildResources()) {
            if (factories.getRobotsFactory() <= 10
                    && currentMax > 8
                    && factories.getRobotsFactory() < currentMax / 3
                    && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.ROBOTS_FACTORY, factories.getRobotsFactory()))) {
                return new FactoryTask(empire, planet, FactoryType.ROBOTS_FACTORY);

            }
            if(currentMax > 12) {
                if (!planet.getShipyardBusy() && factories.getShipyard() <= 9
                        && factories.getShipyard() < currentMax / 3
                        && OGameLibrary.canBuild(empire, planet, FactoryType.SHIPYARD)
                        && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.SHIPYARD, factories.getShipyard()))) {
                    return new FactoryTask(empire, planet, FactoryType.SHIPYARD);

                }
                if (!empire.isResearchInProgress()
                        && factories.getResearchLab() <= 10
                        && factories.getResearchLab() < currentMax / 3
                        && OGameLibrary.canBuild(empire, planet, FactoryType.RESEARCH_LAB)
                        && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.RESEARCH_LAB, factories.getResearchLab()))) {
                    return new FactoryTask(empire, planet, FactoryType.RESEARCH_LAB);
                }
                if (factories.getMissileSilos() < currentMax / 5
                        && OGameLibrary.canBuild(empire, planet, FactoryType.MISSILE_SILOS)
                        && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.MISSILE_SILOS, factories.getMissileSilos()))) {
                    return new FactoryTask(empire, planet, FactoryType.MISSILE_SILOS);
                }
            }

            if (buildings.getMetalMine() < currentMax
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.METAL_MINE, buildings.getMetalMine()))) {
                return new BuildingTask(empire, planet, BuildingType.METAL_MINE);
            }
            if (buildings.getCrystalMine() < currentMax - 2
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.CRYSTAL_MINE, buildings.getCrystalMine()))) {
                return new BuildingTask(empire, planet, BuildingType.CRYSTAL_MINE);
            }
            if (buildings.getDeuteriumMine() < currentMax / 2
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.DEUTERIUM_MINE, buildings.getDeuteriumMine()))) {
                return new BuildingTask(empire, planet, BuildingType.DEUTERIUM_MINE);
            }
            if (buildings.getMetalStorage() < currentMax / 3
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.METAL_STORAGE, buildings.getMetalStorage()))) {
                return new BuildingTask(empire, planet, BuildingType.METAL_STORAGE);
            }
            if (buildings.getCrystalStorage() < (currentMax - 2) / 3
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.CRYSTAL_STORAGE, buildings.getCrystalStorage()))) {
                return new BuildingTask(empire, planet, BuildingType.CRYSTAL_STORAGE);
            }
            if (buildings.getDeuteriumStorage() < currentMax / 6
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.DEUTERIUM_STORAGE, buildings.getDeuteriumStorage()))) {
                return new BuildingTask(empire, planet, BuildingType.DEUTERIUM_STORAGE);
            }
            if (resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.SOLAR_PLANT, buildings.getSolarPlant()))) {
                return new BuildingTask(empire, planet, BuildingType.SOLAR_PLANT);
            }
        }
        return null;
    }
}
