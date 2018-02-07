package ru.tki.brain;

import ru.tki.BotConfigMain;
import ru.tki.models.*;
import ru.tki.models.actions.FactoryAction;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.actions.ShipyardAction;
import ru.tki.models.tasks.*;
import ru.tki.models.types.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class TaskGenerator {

    // Rules for building
    // Keep buildings flat amount on planet
    // Solar main level X
    // Metal = X Crystal = X - 2 Deuterium X / 2
    // MS = M / 3 CS = C / 3 DS = D / 3

    //Rescan sector for inactive players every 25 hours
    private static Duration checkGalaxySectorTimeout = Duration.ofHours(40);


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
    private BotConfigMain config;

    TaskGenerator(Empire empire, BotConfigMain config) {
        this.empire = empire;
        this.config = config;
    }

    public void setEmpire(Empire empire) {
        this.empire = empire;
    }

    Task getTask(Planet planet) {
        Researches researches = empire.getResearches();

        Task task = getSolarSatelliteTask(planet);
        if (task != null) return task;

        task = getTaskBuilding(planet);
        if (task != null) return task;

        task = getTaskResearches(planet, researches);
        if (task != null) return task;

        task = getFleetBuildTask(planet);
        if (task != null) return task;

        task = getDefenceTask(planet);
        if (task != null) return task;

        return null;
    }

    //Check if we need to move resources from colony to main planet
    Task getFleetMoveResourcesTask(Planet planet) {
        if (empire.isPlanetMain(planet) || planet.getLevel() <= 15 || empire.isLastFleetSlot()) {
            return null;
        }
        AbstractPlanet target = empire.getClosestMainPlanet(planet);
        Integer production = empire.getProductionOnPlanetInTimeframe(planet);
        if (!target.equals(planet)
                && planet.getResources().getCapacity() > production
                && !empire.isLastFleetSlot()
                && planet.getFleet().getCapacity() > production * 0.6) {

            System.out.println(String.format("Move resources %s from colony %s to main planet %s",
                    planet.getResources(), planet.getCoordinates().getFormattedCoordinates(),
                    target.getCoordinates().getFormattedCoordinates()));
            FleetTask task = new FleetTask(empire, planet, target,
                    planet.getFleet().getRequiredFleet(planet.getResources()),
                    MissionType.TRANSPORT,
                    planet.getResources());
            if (task.isEnoughFuel()) {
                return task;
            }
        }
        return null;
    }

    // Minimize main planets count in the empire and move them from small planets
    // Keep them 2 for now
    // Move main planet from 1-3 positions
    // Move main from planet with nanite building or shipyard building
    Task checkMainPlanets() {
        if (empire.isLastFleetSlot()) {
            return null;
        }
        //Move main planet from small planets to the biggest by size. To avoid storage build
        Optional<AbstractPlanet> small = empire.getMainPlanets().stream().filter(planet ->
                empire.getActions().stream().filter(action ->
                        action.getPlanet().equals(planet)
                                && action instanceof FactoryAction
                                && (
                                ((FactoryAction) action).getFactoryType() == FactoryType.NANITE_FACTORY
                                        || ((FactoryAction) action).getFactoryType() == FactoryType.SHIPYARD)
                ).count() > 0
        ).findFirst();
        Task task = moveMainPlanet(small, "Planet with active building of nanite factory can't be main. Move it to closest big planet");
        if (null != task) return task;

        //Move main planet from small planets to the biggest by size. To avoid storage build
        small = empire.getMainPlanets().stream().filter(planet -> planet.getCoordinates().getPlanet() < 4).findFirst();
        task = moveMainPlanet(small, "Small planet can't be main. Move it to closest big planet");
        if (null != task) return task;

        if (empire.getMainPlanets().size() > empire.getCurrentPlanetsCount() / 4 + 1) {
            AbstractPlanet smallest = empire.getMainPlanets().stream().min(Comparator.comparingLong(p -> p.getFleet().getCost())).get();
            AbstractPlanet biggest = empire.getMainPlanets().stream().filter(planet -> !planet.equals(smallest)).max(Comparator.comparingLong(p -> p.getFleet().getCost())).get();
            Fleet fleet = empire.getPlanetFleetToMove(smallest);
            if (!fleet.isEmpty() && !empire.isPlanetUnderAttack(biggest) && !biggest.hasTask()) {
                System.out.println(String.format("There are more than 2 main planets. Move fleet from planet %s to %s",
                        smallest.getCoordinates(), biggest.getCoordinates()));
                return moveMainPlanet(smallest, biggest, fleet);
            }
        }
        return null;
    }

    private Task moveMainPlanet(Optional<AbstractPlanet> planetFrom, String description) {
        if (planetFrom.isPresent()) {
            AbstractPlanet smallPlanet = planetFrom.get();
            System.out.println(description);
            Optional<AbstractPlanet> targetOptional = empire.getMainPlanets().stream().filter(planet -> !planet.equals(smallPlanet)).max(Comparator.comparingInt(AbstractPlanet::getSize));
            if (!targetOptional.isPresent()) {
                targetOptional = empire.getPlanets().stream().filter(planet -> !planet.equals(smallPlanet)).max(Comparator.comparingInt(AbstractPlanet::getLevel));
            }
            if (targetOptional.isPresent() && !smallPlanet.hasTask()) {
                AbstractPlanet target = targetOptional.get();
                Fleet fleet = empire.getPlanetFleetToMove(smallPlanet);
                if (!fleet.isEmpty()) {
                    return moveMainPlanet(smallPlanet, target, fleet);
                }
            }
        }
        return null;
    }

    private Task moveMainPlanet(AbstractPlanet from, AbstractPlanet to, Fleet fleet) {
        FleetTask task = new FleetTask(empire, from, to, fleet, MissionType.KEEP, from.getResources());
        task.setRandomTransportSpeed();
        if (task.isEnoughFuel()) {
            return task;
        }
        return null;
    }

    // Verify if we can build or research something with move of resources from one planet to another
    // Don't sent single level building until level 13
    Task checkTransportForBuild() {
        Task task;
        if (empire.isLastFleetSlot()) {
            return null;
        }
        for (AbstractPlanet planet : empire.getPlanets()) {
            //avoid 2 tasks on 1 planet
            if (planet.hasTask() || empire.isPlanetUnderAttack(planet)) {
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
                    if (!planet.equals(main) && !main.hasTask()) {
                        //Find build task with extra resources from main planet
                        task = getTaskBuilding((Planet) planet, main.getResources());
                        if (null == task) {
                            //Find research task
                            task = getTaskResearches((Planet) planet, empire.getResearches(), main.getResources());
                        }
                        if (null != task) {
                            Resources requiredResources;
                            Fleet fleet;
                            MissionType missionType;
                            if (task.getResources().getCapacity() > 1000 * 1000) {
                                missionType = MissionType.KEEP;
                                fleet = empire.getPlanetFleetToMove(main);
                                if (fleet.getCapacity() > main.getResources().getCapacity()) {
                                    requiredResources = main.getResources();
                                } else {
                                    task.removeFromQueue();
                                    continue;
                                }
                            } else {
                                requiredResources = task.getResources().deduct(planet.getResources());
                                fleet = main.getFleet().getRequiredFleet(requiredResources);
                                missionType = MissionType.TRANSPORT;
                            }

                            if (main.getFleet().has(fleet)) {
                                FleetTask fleetTask = new FleetTask(empire, main, planet, fleet, missionType, requiredResources);
                                fleetTask.setRandomTransportSpeed();
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

    private Task findResourcesOnMain(AbstractPlanet planet, Resources resources, Task subtask) {
        for (AbstractPlanet main : empire.getMainPlanets()) {
            if (!planet.equals(main) && !main.hasTask() && main.hasResources(resources) && !empire.isLastFleetSlot()) {
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

    Task getColonyTask() {
        Integer currentPlanets = empire.getCurrentPlanetsCount();
        Integer maxPlanets = empire.getMaxPlanetsCount();
        if (!config.BUILD_COLONIES) {
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
                        t.addTask(new CheckColonyTask(empire, colony, config.COLONY_MIN_SIZE));
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

    Task sendExpedition() {
        if (empire.getMaxExpeditions() > empire.getActiveExpeditions()
                && !empire.isLastFleetSlot()
                && config.SEND_EXPEDITIONS) {
            Optional<AbstractPlanet> planet = empire.getPlanets().stream().filter(planet1 -> empire.isPlanetMain(planet1) && !planet1.hasTask()).max((a, b) -> {
                if (a.getFleet().getLargeCargo() > 0 || b.getFleet().getLargeCargo() > 0) {
                    return a.getFleet().getLargeCargo() - b.getFleet().getLargeCargo();
                }
                return a.getFleet().getSmallCargo() - b.getFleet().getSmallCargo();
            });
            if (planet.isPresent()) {
                AbstractPlanet p = planet.get();
                Fleet fleet = empire.getFleetForExpedition(p);
                if (!fleet.isEmpty()) {
                    FleetTask task = new FleetTask(empire, p, empire.getPlanetForExpedition(p), fleet, MissionType.EXPEDITION);
                    if (task.isEnoughFuel()) {
                        return task;
                    }
                }
            }
        }
        return null;
    }

    Task getTaskImportExport() {

        Optional<AbstractPlanet> planetOptional = empire.getPlanets().stream().max(Comparator.comparingInt(p -> p.getResources().getCapacity()));
        if (planetOptional.isPresent()) {
            AbstractPlanet planet = planetOptional.get();
            if (planet.getLevel() < 20) {
                //Don't spend resources until level 20
                return null;
            }
            return new GetDailyBonusTask(empire, planet);
        }
        return null;
    }

    Task raidInactivePlayers() {

        if (!config.RAID_INACTIVE
                || empire.getResearches().getEspionage() < 6) {
            return null;
        }

        Optional<AbstractPlanet> planetOptional = getPlanetWithMaxSpies();
        if (planetOptional.isPresent()
                && empire.getMaxFleets() - empire.getActiveFleets() > 3) {
            AbstractPlanet planet = planetOptional.get();

            if (planet.getLevel() >= 15
                    && !empire.isPlanetUnderAttack(planet)
                    && !planet.hasTask()
                    ) {
                Task task = getScanGalaxyTask(planet);
                if (task != null) return task;

                task = rescanInactivePlayers(planet);
                if (task != null) return task;
            }
        }
        Optional<AbstractPlanet> mainPlanetOptional = getMainPlanetWithMaxSmallTransports();
        if (mainPlanetOptional.isPresent()) {
            AbstractPlanet planet = mainPlanetOptional.get();
            Task task = attackInactivePlayers(planet);
            if (task != null) return task;
        }
        return null;
    }

    private Task getScanGalaxyTask(AbstractPlanet planet) {
        Galaxy galaxy = empire.getGalaxy();
        for (GalaxySector sector : galaxy.getSectorsForScan(planet, empire.getResearches().getReactiveEngine(), empire.getResearches().getImpulseEngine())) {
            if (null == sector.getLastUpdated() || sector.getLastUpdated().plus(checkGalaxySectorTimeout).compareTo(Instant.now()) < 0) {
                return new ScanSectorForInactivePlayerTask(empire, planet, sector);
            }
        }
        return null;
    }

    private Task attackInactivePlayers(AbstractPlanet planet) {
        EnemyPlanet enemyPlanet = empire.getGalaxy().getBestTarget();
        if (null != enemyPlanet
                && empire.getActiveAttackFleets() <= empire.getMaxFleets() / 2
                && !empire.isLastFleetSlot()) {
            enemyPlanet.setUnderAttack(true);
            FleetTask task = new FleetTask(empire, planet, enemyPlanet, planet.getFleet().getRequiredSmallCargo(enemyPlanet.getResources().multiply(0.5).getCapacity()), MissionType.ATTACK);
            if (task.isEnoughFuel()) {
                return task;
            }
        }
        return null;
    }

    private Task rescanInactivePlayers(AbstractPlanet planet) {
        //Don't send more than 8 spies at once so limit to this amount here.
        int MAX_PLANETS = 8;

        List<EnemyPlanet> planetList = new ArrayList<>();

        //scan planets with result 1+ days
        planetList.addAll(empire.getGalaxy().getPlanetsOutdated());

        //scan planets 6+ h with known fleet and defence
        if (planetList.size() < MAX_PLANETS) {
            planetList.addAll(empire.getGalaxy().getPlanetsOutdated6HWithKnownDetails());
        }

        //scan 3+ h with max resources and known fleet and defence
        if (planetList.size() < MAX_PLANETS) {
            planetList.addAll(empire.getGalaxy().getPlanetsOutdatedWithKnownDetails());
        }

        planetList = planetList.stream().sorted((o1, o2) -> {
            int sComp = o1.getCoordinates().getGalaxy().compareTo(o2.getCoordinates().getGalaxy());
            if (sComp != 0) {
                return sComp;
            } else {
                sComp = o1.getCoordinates().getSystem().compareTo(o2.getCoordinates().getSystem());
                if (sComp != 0) {
                    return sComp;
                } else {
                    return o1.getCoordinates().getPlanet().compareTo(o2.getCoordinates().getPlanet());
                }
            }
        }).collect(Collectors.toList());

        if (planetList.size() > MAX_PLANETS) {
            planetList = planetList.subList(0, MAX_PLANETS);
        }
        if (!planetList.isEmpty()) {
            return new RescanInactivePlayersTask(empire, planet, planetList);
        }
        return null;
    }

    private Optional<AbstractPlanet> getPlanetWithMaxSpies() {
        return empire.getPlanets().stream().filter(planet ->
                planet.getFleet().getEspionageProbe() > 3
                        && !planet.hasTask()
        ).max(Comparator.comparingInt(p -> p.getFleet().getEspionageProbe()));
    }

    private Optional<AbstractPlanet> getMainPlanetWithMaxSmallTransports() {
        return empire.getMainPlanets().stream().filter(planet ->
                planet.getCoordinates().getPlanet() > 3
                        && !empire.isPlanetUnderAttack(planet)
                        && !planet.hasTask()
                        && planet.getFleet().getSmallCargo() > 0).max(Comparator.comparingInt(p -> p.getFleet().getSmallCargo()));
    }

    private Task getSolarSatelliteTask(Planet planet) {
        Resources resources = planet.getResources();
        if (!planet.getShipyardBusy() && planet.getLevel() > 15) {
            if (planet.getResources().getEnergy() < 0) {
                //Build 1/4 of required solar satellites
                Integer count = Math.max(1, planet.getResources().getEnergy() * -1 / 30 / 4);
                if (OGameLibrary.canBuild(empire, planet, ShipType.SOLAR_SATELLITE)
                        && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.SOLAR_SATELLITE, 1).multiply(count))) {
                    return new ShipyardTask(empire, planet, ShipType.SOLAR_SATELLITE, count);
                }
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
        if (!planet.getBuildInProgress() && config.BUILD_FACTORIES) {
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
                        && factories.getResearchLab() < 10
                        && factories.getResearchLab() < currentMax / 2.5
                        && (planet.getFactories().getResearchLab() > 0
                        || empire.getCurrentPlanetsWithResearchLabCount() < empire.getResearches().getMis() + 1)
                        && OGameLibrary.canBuild(empire, planet, FactoryType.RESEARCH_LAB)
                        && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.RESEARCH_LAB, factories.getResearchLab()))) {
                    return new FactoryTask(empire, planet, FactoryType.RESEARCH_LAB);
                }
            }
            if (config.BUILD_DEFENCE && currentMax > 20
                    && factories.getMissileSilos() < currentMax / 5
                    && OGameLibrary.canBuild(empire, planet, FactoryType.MISSILE_SILOS)
                    && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.MISSILE_SILOS, factories.getMissileSilos()))) {
                return new FactoryTask(empire, planet, FactoryType.MISSILE_SILOS);
            }

            if (OGameLibrary.canBuild(empire, planet, FactoryType.NANITE_FACTORY)
                    && factories.getNaniteFactory() < 4
                    && factories.getNaniteFactory() < currentMax / 8
                    && resources.isEnoughFor(OGameLibrary.getFactoryPrice(FactoryType.NANITE_FACTORY, factories.getNaniteFactory()))) {
                return new FactoryTask(empire, planet, FactoryType.NANITE_FACTORY);
            }
        }
        if (!planet.getBuildInProgress() && config.BUILD_RESOURCES) {

            if (buildings.getCrystalMine() < currentMax - 2
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.CRYSTAL_MINE, buildings.getCrystalMine()))) {
                return new BuildingTask(empire, planet, BuildingType.CRYSTAL_MINE);
            }
            if (buildings.getMetalMine() < currentMax
                    && buildings.getMetalMine() <= buildings.getCrystalMine() + 1
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.METAL_MINE, buildings.getMetalMine()))) {
                return new BuildingTask(empire, planet, BuildingType.METAL_MINE);
            }
            if (currentMax > 8
                    && buildings.getDeuteriumMine() < currentMax / 2 + currentMax / 9 - (planet.getCoordinates().getPlanet() < 4 ? 10 : 0)
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.DEUTERIUM_MINE, buildings.getDeuteriumMine()))) {

                return new BuildingTask(empire, planet, BuildingType.DEUTERIUM_MINE);
            }


            if (currentMax > 11) {
                if (planet.getResources().getMetal() >= OGameLibrary.getStorageCapacity(buildings.getMetalStorage()) * .9
                        && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.METAL_STORAGE, buildings.getMetalStorage()))) {
                    return new BuildingTask(empire, planet, BuildingType.METAL_STORAGE);
                }
                if (planet.getResources().getCrystal() >= OGameLibrary.getStorageCapacity(buildings.getCrystalStorage()) * .9
                        && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.CRYSTAL_STORAGE, buildings.getCrystalStorage()))) {
                    return new BuildingTask(empire, planet, BuildingType.CRYSTAL_STORAGE);
                }
                if (planet.getResources().getDeuterium() >= OGameLibrary.getStorageCapacity(buildings.getDeuteriumStorage()) * .9
                        && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.DEUTERIUM_STORAGE, buildings.getDeuteriumStorage()))) {
                    return new BuildingTask(empire, planet, BuildingType.DEUTERIUM_STORAGE);
                }
            }
            if (buildings.getSolarPlant() <= buildings.getCrystalMine() + 2
                    && resources.isEnoughFor(OGameLibrary.getBuildingPrice(BuildingType.SOLAR_PLANT, buildings.getSolarPlant()))) {
                return new BuildingTask(empire, planet, BuildingType.SOLAR_PLANT);
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
        if (!empire.isResearchInProgress() && currentMax > 12 && config.DO_RESEARCHES) {
            if (researches.getAstrophysics() <= 17
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
            if (
                    (currentMax > 25
                            || (currentMax > 15 && researches.getWeapon() < 3)//need 3 for gauss
                    )
                            && researches.getWeapon() <= 25
                            && OGameLibrary.canBuild(empire, planet, ResearchType.WEAPON)
                            && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.WEAPON, researches.getEspionage()))) {
                return new ResearchTask(empire, planet, ResearchType.WEAPON);
            }

            if (
                    (currentMax > 25
                            || (currentMax > 15 && researches.getShields() < 5)
                    )
                            && researches.getShields() <= 25
                            && OGameLibrary.canBuild(empire, planet, ResearchType.SHIELDS)
                            && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.SHIELDS, researches.getShields()))) {
                return new ResearchTask(empire, planet, ResearchType.SHIELDS);
            }

            if (
                    (researches.getArmor() < 2
                            || currentMax > 25
                    )
                            && researches.getArmor() <= 25
                            && OGameLibrary.canBuild(empire, planet, ResearchType.ARMOR)
                            && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.ARMOR, researches.getArmor()))) {
                return new ResearchTask(empire, planet, ResearchType.ARMOR);
            }
            if (
                    (researches.getLaser() < 6
                            || (currentMax > 20 && researches.getLaser() < 10)
                            || (currentMax > 25 && researches.getLaser() < 12)
                    )
                            && OGameLibrary.canBuild(empire, planet, ResearchType.LASER)
                            && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.LASER, researches.getLaser()))) {
                return new ResearchTask(empire, planet, ResearchType.LASER);
            }
            if (
                    (researches.getEnergy() < 8
                            || (currentMax > 26 && researches.getEnergy() < 12)
                    )
                            && OGameLibrary.canBuild(empire, planet, ResearchType.ENERGY)
                            && resources.isEnoughFor(OGameLibrary.getResearchPrice(ResearchType.ENERGY, researches.getEnergy()))) {
                return new ResearchTask(empire, planet, ResearchType.ENERGY);
            }
        }

        return null;
    }

    private Task getFleetBuildTask(Planet planet) {
        Task task = getRequiredCargoTask(planet);
        if (task != null) return task;

        task = getRequiredSpies(planet);
        if (task != null) return task;

        return null;
    }

    //Build minimal amount of transports on planet to move resources out on main planet or keep them in case of attack
    private Task getRequiredCargoTask(Planet planet) {
        Integer currentMax = planet.getLevel();
        Resources resources = planet.getResources();
        if (!planet.getShipyardBusy() && currentMax > 12 && config.BUILD_FLEET) {
            Integer planetProduction = empire.getProductionOnPlanetInTimeframe(planet);
            //task to build 1/8 of transports required to cover full planet production
            Integer buildAmount = Math.max(planetProduction / (5000 * 8), 1);
            if (empire.getPlanetTotalFleet(planet).getSmallCargoCapacity() < planetProduction
                    && OGameLibrary.canBuild(empire, planet, ShipType.SMALL_CARGO)
                    && resources.isEnoughFor(OGameLibrary.getShipPrice(ShipType.SMALL_CARGO).multiply(buildAmount))) {

                System.out.println(String.format("Details: need to build %d %s on %s planet to meet production capacity %s by small cargo" +
                                " Current fleet: %s",
                        buildAmount, ShipType.SMALL_CARGO, planet.getCoordinates(), planetProduction, planet.getFleet().getDetails()));
                return new ShipyardTask(empire, planet, ShipType.SMALL_CARGO, buildAmount);
            }
            //Main planet need to have minimum double amount of transports for regular production or full coverage for existing fleet
            buildAmount = Math.max(buildAmount / 5, 1);
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

    private Task getRequiredSpies(Planet planet) {
        Integer requiredSpies = Math.min(empire.getResearches().getComputer(), 8);
        if (!planet.getShipyardBusy()
                && empire.isPlanetMain(planet)
                && planet.getLevel() > 15
                && planet.getFleet().getEspionageProbe() < requiredSpies
                && config.BUILD_FLEET) {
            Integer buildAmount = requiredSpies - planet.getFleet().getEspionageProbe();
            if (OGameLibrary.canBuild(empire, planet, ShipType.ESPIONAGE_PROBE)
                    && planet.getResources().isEnoughFor(OGameLibrary.getShipPrice(ShipType.ESPIONAGE_PROBE).multiply(buildAmount))) {
                return new ShipyardTask(empire, planet, ShipType.ESPIONAGE_PROBE, buildAmount);
            }
        }
        return null;
    }

    private Task getDefenceTask(Planet planet) {
        Integer currentMax = planet.getLevel();
        Resources resources = planet.getResources();
        Defence defence = planet.getDefence();
        if (!planet.getShipyardBusy() && currentMax > 15 && config.BUILD_DEFENCE) {
            Integer multiplier = currentMax * currentMax / 120 - 1;
            if (defence.getRocket() < optimalDefence.get(DefenceType.ROCKET) * multiplier
                    && OGameLibrary.canBuild(empire, planet, DefenceType.ROCKET)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.ROCKET).multiply(5))) {
                return new DefenceTask(empire, planet, DefenceType.ROCKET, 5);
            }
            if (defence.getLightLaser() < optimalDefence.get(DefenceType.LIGHT_LASER) * multiplier
                    && OGameLibrary.canBuild(empire, planet, DefenceType.LIGHT_LASER)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.LIGHT_LASER).multiply(5))) {
                return new DefenceTask(empire, planet, DefenceType.LIGHT_LASER, 5);
            }
            if (defence.getHeavyLaser() < optimalDefence.get(DefenceType.HEAVY_LASER) * multiplier
                    && OGameLibrary.canBuild(empire, planet, DefenceType.HEAVY_LASER)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.HEAVY_LASER))) {
                return new DefenceTask(empire, planet, DefenceType.HEAVY_LASER, 1);
            }
            if (defence.getGauss() < optimalDefence.get(DefenceType.GAUSS) * multiplier
                    && OGameLibrary.canBuild(empire, planet, DefenceType.GAUSS)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.GAUSS))) {
                return new DefenceTask(empire, planet, DefenceType.GAUSS, 1);
            }
            if (defence.getPlasma() < optimalDefence.get(DefenceType.PLASMA) * multiplier
                    && OGameLibrary.canBuild(empire, planet, DefenceType.PLASMA)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.PLASMA))) {
                return new DefenceTask(empire, planet, DefenceType.PLASMA, 1);
            }
            if (defence.getSmallShield() < 1
                    && OGameLibrary.canBuild(empire, planet, DefenceType.SMALL_SHIELD)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.SMALL_SHIELD))) {
                return new DefenceTask(empire, planet, DefenceType.SMALL_SHIELD, 1);
            }
            if (defence.getBigShield() < 1
                    && OGameLibrary.canBuild(empire, planet, DefenceType.BIG_SHIELD)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.BIG_SHIELD))) {
                return new DefenceTask(empire, planet, DefenceType.BIG_SHIELD, 1);
            }

            if (defence.getDefenceMissile() < planet.getFactories().getMissileSilos() * 10
                    && OGameLibrary.canBuild(empire, planet, DefenceType.DEFENCE_MISSILE)
                    && resources.isEnoughFor(OGameLibrary.getDefencePrice(DefenceType.DEFENCE_MISSILE))) {
                return new DefenceTask(empire, planet, DefenceType.DEFENCE_MISSILE, 1);
            }
        }
        return null;
    }
}
