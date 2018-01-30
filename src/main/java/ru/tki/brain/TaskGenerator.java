package ru.tki.brain;

import ru.tki.BotConfigMain;
import ru.tki.ContextHolder;
import ru.tki.models.*;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.actions.ShipyardAction;
import ru.tki.models.tasks.*;
import ru.tki.models.types.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

    private Empire        empire;
    private BotConfigMain botConfig;

    public TaskGenerator(Empire empire) {
        this.empire = empire;
        this.botConfig = ContextHolder.getBotConfigMain();
    }

    public Task getTask(Planet planet) {
        Resources resources = planet.getResources();
        Researches researches = empire.getResearches();

        if (!planet.getShipyardBusy() && planet.getLevel() > 12) {
            if (planet.getResources().getEnergy() < 0) {
                if (OGameLibrary.canBuild(empire, planet, ShipType.SOLAR_SATELLITE)
                        && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.SOLAR_SATELLITE, 1))) {
                    return new ShipyardTask(empire, planet, ShipType.SOLAR_SATELLITE, 1);
                }
            }
        }

        Task task = getTaskBuilding(planet);
        if (task != null) return task;

        task = getTaskResearches(planet, researches);
        if (task != null) return task;

        task = getRequiredCargoTask(planet);
        if (task != null) {
            task.addTask(new UpdateInfoTask(empire, planet, UpdateTaskType.FLEET));
            return task;
        }
        task = getDefenceTask(planet);
        if (task != null) {
            task.addTask(new UpdateInfoTask(empire, planet, UpdateTaskType.DEFENCE));
            return task;
        }

        return null;
    }

    public Task getFleetTask(Planet planet) {
        Task task = getFleetMoveResourcesTask(planet);
        if (task != null) return task;

        return null;
    }

    //Minimize main planets count in the empire
    // Keep them 2 for now
    public Task checkMainPlanetsCount() {
        Supplier<Stream<AbstractPlanet>> mainPlanets = () ->
                empire.getPlanets().stream().filter(planet -> empire.isPlanetMain(planet));
        if (mainPlanets.get().count() > 2 && empire.canSendFleet()) {
            AbstractPlanet smallest = mainPlanets.get().min(Comparator.comparingInt(AbstractPlanet::getLevel)).get();
            AbstractPlanet biggest = mainPlanets.get().max(Comparator.comparingInt(AbstractPlanet::getLevel)).get();
            Fleet fleet = smallest.getFleet().deduct(smallest.getFleet().getRequiredFleet(empire.getProductionOnPlanetInTimeframe(smallest)));
            if (!fleet.isEmpty()) {
                System.out.println(String.format("There are more than 2 main planets. Move resources from planet %s to %s",
                        smallest.getCoordinates(), biggest.getCoordinates()));
                return new FleetTask(empire, smallest, biggest, fleet, MissionType.KEEP, smallest.getResources());
            }
        }
        return null;
    }

    // Verify if we can build or research something with move of resources from one planet to another
    // Don't sent single level building until level 13
    public Task checkTransportForBuild() {
        Task task;
        for (AbstractPlanet planet : empire.getPlanets()) {
            //avoid 2 tasks on 1 planet
            if (planet.hasTask()) {
                continue;
            }
            if (planet.isPlanet()) {
                //Initial planet build
                if (planet.getLevel() < 10 && empire.getPlanetTotalResources(planet).getCapacity() < 2000) {
                    // resources to build to 10 8 5 10 robots factory 4
                    return findResourcesOnMain(planet, new Resources(28000, 9600, 3000),
                            new UpdateInfoTask(empire, planet, UpdateTaskType.RESOURCES));
                }

                //Second level planet build
                if (planet.getLevel() < 13 && empire.getPlanetTotalResources(planet).getCapacity() < 5000) {
                    // resources to build from 10 8 5 10 to 13 11 8 7 13
                    return findResourcesOnMain(planet, new Resources(37500, 13500, 1600),
                            new UpdateInfoTask(empire, planet, UpdateTaskType.RESOURCES));
                }
                //planets infrastructure is higher than 13

                for (AbstractPlanet main : empire.getMainPlanets()) {
                    if (!planet.equals(main) && !main.hasTask() && empire.canSendFleet()) {
                        //Find build task with extra resources from main planet
                        task = getTaskBuilding((Planet) planet, main.getResources());
                        if (null == task) {
                            //Find research task
                            task = getTaskResearches((Planet) planet, empire.getResearches(), main.getResources());
                        }
                        if (null != task) {
                            //If we find possible task then create fleet task for resources transport with subtask for execution
                            Resources requiredResources = task.getResources().deduct(planet.getResources());
                            Fleet fleet = main.getFleet().getRequiredFleet(requiredResources);
                            if (main.getFleet().has(fleet)) {
                                FleetTask fleetTask = new FleetTask(empire, main, planet, fleet, MissionType.TRANSPORT, requiredResources);
                                fleetTask.addTask(task);
                                return fleetTask;
                            } else {
                                task.removeFromQueue();
                            }
                        }
                    }
                }
            }
//            else if (planet.getType() == PlanetType.MOON) {
//                //Do nothing now
//            }
        }
        return null;
    }

    public Task getColonyTask() {
        Integer currentPlanets = empire.getCurrentPlanetsCount();
        Integer maxPlanets = empire.getMaxPlanetsCount();
        if(!botConfig.getBuildColonies()){
            return null;
        }
        if (currentPlanets >= maxPlanets) {
            return null;
        }
        if (currentPlanets + empire.getActions().stream().filter(a ->
                a instanceof FleetAction && ((FleetAction) a).getMissionType() == MissionType.COLONIZATION).count()
                >= maxPlanets) {
            //Colony ship flight in progress do nothing
            return null;
        } else {
            //send colony ship if it exists
            Fleet fleet = new Fleet(ShipType.COLONY_SHIP, 1);
            for (AbstractPlanet planet : empire.getPlanets()) {
                if (planet.getFleet().has(fleet)) {
                    if (!empire.canSendFleet()) {
                        return null;
                    } else {
                        AbstractPlanet colony = empire.findNewColony(planet);
                        FleetTask t = new FleetTask(empire, planet, colony, fleet, MissionType.COLONIZATION);
                        t.addTask(new CheckColonyTask(empire, colony, botConfig.getColonyMinSize()));
                        return t;
                    }
                }
            }
        }

        if (currentPlanets + empire.getActions().stream().filter(a ->
                a instanceof ShipyardAction && ((ShipyardAction) a).getType() == ShipType.COLONY_SHIP).count()
                >= maxPlanets) {
            //Colony ship building in progress wait for now
            return null;
        } else {
            //Build colony ship
            for (AbstractPlanet planet : empire.getPlanets()) {
                if (planet.isPlanet()
                        && OGameLibrary.canBuild(empire, (Planet) planet, ShipType.COLONY_SHIP)
                        && planet.getResources().isEnoughFor(OGameLibrary.getShipPrice(ShipType.COLONY_SHIP))
                        && !planet.getShipyardBusy()
                        && !planet.hasTask()) {
                    return new ShipyardTask(empire, planet, ShipType.COLONY_SHIP, 1);
                }
            }
        }
        return null;
    }

    public Task sendExpedition() {
        if (empire.getMaxExpeditions() > empire.getActiveExpeditions()
                && empire.canSendFleet()
                && botConfig.getSendExpeditions()) {
            for (AbstractPlanet planet : empire.getMainPlanets()) {
                if (!planet.hasTask()) {
                    Fleet fleet = empire.getFleetForExpedition(planet);
                    if (!fleet.isEmpty()) {
                        return new FleetTask(empire, planet, empire.getPlanetForExpedition(planet), fleet, MissionType.EXPEDITION);
                    }
                }
            }
        }
        return null;
    }

    private Task findResourcesOnMain(AbstractPlanet planet, Resources resources, Task subtask) {
        for (AbstractPlanet main : empire.getMainPlanets()) {
            if (!planet.equals(main) && !main.hasTask() && main.hasResources(resources) && empire.canSendFleet()) {
                System.out.println(String.format("Move resources %s from main %s to colony planet %s for initial builds",
                        resources, main.getCoordinates().getFormattedCoordinates(), planet.getCoordinates().getFormattedCoordinates()));
                FleetTask task = new FleetTask(empire, main, planet,
                        main.getFleet().getRequiredFleet(resources),
                        MissionType.TRANSPORT,
                        resources);
                task.addTask(subtask);
                return task;
            }
        }
        return null;
    }

    //Build minimal amount of transports on planet to move resources out on main planet or keep them in case of attack
    private Task getRequiredCargoTask(Planet planet) {
        Integer currentMax = planet.getLevel();
        Resources resources = planet.getResources();
        if (!planet.getShipyardBusy() && currentMax > 12 && botConfig.getBuildFleet()) {
            Integer planetProduction = empire.getProductionOnPlanetInTimeframe(planet);
            //task to build 1/8 of transports required to cover full planet production
            Integer buildAmount = Math.max(planetProduction / (5000 * 8), 2);
            if (empire.getPlanetTotalFleet(planet).getSmallCargoCapacity() < planetProduction
                    && OGameLibrary.canBuild(empire, planet, ShipType.SMALL_CARGO)
                    && resources.isEnoughFor(OGameLibrary.getShipPrice(ShipType.SMALL_CARGO).multiply(buildAmount))) {

                System.out.println(String.format("Details: need to build %d %s on %s planet to meet production capacity %s by small cargo" +
                                " Current fleet: %s",
                        buildAmount, ShipType.SMALL_CARGO, planet.getCoordinates(), planetProduction, planet.getFleet().getDetails()));
                return new ShipyardTask(empire, planet, ShipType.SMALL_CARGO, buildAmount);
            }
            //Main planet need to have minimum double amount of transports for regular production or full coverage for existing fleet
            buildAmount = Math.max(buildAmount / 5, 2);
            if (empire.isPlanetMain(planet)
                    && (empire.getPlanetTotalFleet(planet).getCapacity() < planetProduction * 2
                    || planet.getFleet().getCapacity() < planet.getResources().getCapacity())
                    && OGameLibrary.canBuild(empire, planet, ShipType.LARGE_CARGO)
                    && resources.isEnoughFor(OGameLibrary.getShipPrice(ShipType.LARGE_CARGO).multiply(buildAmount))) {

                System.out.println(String.format("Details: need to build %d %s on %s planet to meet double production capacity %s or current resources %s" +
                                " Current fleet: %s",
                        buildAmount, ShipType.LARGE_CARGO, planet.getCoordinates(), planetProduction * 2, planet.getResources(), planet.getFleet().getDetails()));
                return new ShipyardTask(empire, planet, ShipType.LARGE_CARGO, buildAmount);
            }
        }
        return null;
    }

    //Check if we need to move resources from colony to main planet
    private Task getFleetMoveResourcesTask(Planet planet) {
        if (empire.isPlanetMain(planet) || planet.getLevel() <= 15) {
            return null;
        }
        AbstractPlanet target = empire.getClosestMainPlanet(planet);
        if (!target.equals(planet)
                && planet.getResources().getCapacity() > empire.getProductionOnPlanetInTimeframe(planet)
                && empire.canSendFleet()
                && !planet.getFleet().isEmpty()) {

            System.out.println(String.format("Move resources %s from colony %s to main planet %s",
                    planet.getResources(), planet.getCoordinates().getFormattedCoordinates(),
                    target.getCoordinates().getFormattedCoordinates()));
            return new FleetTask(empire, planet, target,
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
            Integer multiplier = currentMax * currentMax / 120 - 1;
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
        return this.getTaskResearches(planet, researches, new Resources());
    }

    private Task getTaskResearches(Planet planet, Researches researches, Resources additionalResources) {
        Integer currentMax = planet.getLevel();
        Resources resources = planet.getResources().add(additionalResources);
        if (!empire.isResearchInProgress() && currentMax > 12 && botConfig.getDoResearches()) {
            if (researches.getAstrophysics() <= 20
                    && OGameLibrary.canBuild(empire, planet, ResearchType.ASTROPHYSICS)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.ASTROPHYSICS, researches.getAstrophysics()))) {
                return new ResearchTask(empire, planet, ResearchType.ASTROPHYSICS);
            }
            if (researches.getComputer() <= 20
                    && OGameLibrary.canBuild(empire, planet, ResearchType.COMPUTER)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.COMPUTER, researches.getComputer()))) {
                return new ResearchTask(empire, planet, ResearchType.COMPUTER);
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
            if (researches.getHyper() < 8
                    && OGameLibrary.canBuild(empire, planet, ResearchType.HYPER)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.HYPER, researches.getHyper()))) {
                return new ResearchTask(empire, planet, ResearchType.HYPER);
            }
            if (researches.getIon() < 5
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
            if (currentMax > 25) {
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
            }
            if (researches.getLaser() < 12
                    && OGameLibrary.canBuild(empire, planet, ResearchType.LASER)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.LASER, researches.getLaser()))) {
                return new ResearchTask(empire, planet, ResearchType.LASER);
            }
            if (researches.getEnergy() < 12
                    && OGameLibrary.canBuild(empire, planet, ResearchType.ENERGY)
                    && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.ENERGY, researches.getEnergy()))) {
                return new ResearchTask(empire, planet, ResearchType.ENERGY);
            }
        }
        return null;
    }

    private Task getTaskBuilding(Planet planet) {
        return this.getTaskBuilding(planet, new Resources());
    }

    private Task getTaskBuilding(Planet planet, Resources additionalResources) {
        Integer currentMax = planet.getLevel();
        Resources resources = planet.getResources().add(additionalResources);
        Buildings buildings = planet.getBuildings();
        Factories factories = planet.getFactories();
        if (!planet.getBuildInProgress() && botConfig.getBuildFactories()) {
            if (factories.getRobotsFactory() < 10
                    && currentMax > 8
                    && factories.getRobotsFactory() < currentMax / 2.5
                    && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.ROBOTS_FACTORY, factories.getRobotsFactory()))) {
                return new FactoryTask(empire, planet, FactoryType.ROBOTS_FACTORY);
            }
            if (currentMax > 12) {
                if (!planet.getShipyardBusy() && factories.getShipyard() <= 9
                        && factories.getShipyard() < currentMax / 3
                        && OGameLibrary.canBuild(empire, planet, FactoryType.SHIPYARD)
                        && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.SHIPYARD, factories.getShipyard()))) {
                    return new FactoryTask(empire, planet, FactoryType.SHIPYARD);

                }
                if (!empire.isResearchInProgress()
                        && factories.getResearchLab() <= 10
                        && factories.getResearchLab() < currentMax / 2.5
                        && ( planet.getFactories().getResearchLab() > 0
                        || empire.getCurrentPlanetsWithResearchLabCount() < empire.getResearches().getMis() + 1)
                        && OGameLibrary.canBuild(empire, planet, FactoryType.RESEARCH_LAB)
                        && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.RESEARCH_LAB, factories.getResearchLab()))) {
                    return new FactoryTask(empire, planet, FactoryType.RESEARCH_LAB);
                }
            }
            if (currentMax > 20
                    && factories.getMissileSilos() < currentMax / 5
                    && OGameLibrary.canBuild(empire, planet, FactoryType.MISSILE_SILOS)
                    && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.MISSILE_SILOS, factories.getMissileSilos()))) {
                return new FactoryTask(empire, planet, FactoryType.MISSILE_SILOS);
            }
        }
        if (!planet.getBuildInProgress() && botConfig.getBuildResources()) {

            if (buildings.getCrystalMine() < currentMax - 2
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.CRYSTAL_MINE, buildings.getCrystalMine()))) {
                return new BuildingTask(empire, planet, BuildingType.CRYSTAL_MINE);
            }
            if (buildings.getMetalMine() < currentMax
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.METAL_MINE, buildings.getMetalMine()))) {
                return new BuildingTask(empire, planet, BuildingType.METAL_MINE);
            }
            if (currentMax > 8
                    && buildings.getDeuteriumMine() < currentMax / 2
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.DEUTERIUM_MINE, buildings.getDeuteriumMine()))) {
                return new BuildingTask(empire, planet, BuildingType.DEUTERIUM_MINE);
            }
            if (currentMax > 12) {
                //TODO: Review storage amount for actual required value of buildings
                //Build storage after solar plant level 13
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
            }
            if (resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.SOLAR_PLANT, buildings.getSolarPlant()))) {
                return new BuildingTask(empire, planet, BuildingType.SOLAR_PLANT);
            }
        }
        return null;
    }
}
