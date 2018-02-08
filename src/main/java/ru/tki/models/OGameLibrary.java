package ru.tki.models;

import ru.tki.ContextHolder;
import ru.tki.models.types.*;

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

    private static final Map<DefenceType, Resources> defence = new HashMap<DefenceType, Resources>() {{
        put(DefenceType.ROCKET, new Resources(2000, 0, 0));
        put(DefenceType.LIGHT_LASER, new Resources(1500, 500, 0));
        put(DefenceType.HEAVY_LASER, new Resources(6000, 2000, 0));
        put(DefenceType.GAUSS, new Resources(20000, 15000, 2000));
        put(DefenceType.ION, new Resources(2000, 6000, 0));
        put(DefenceType.PLASMA, new Resources(50000, 50000, 30000));
        put(DefenceType.SMALL_SHIELD, new Resources(10000, 10000, 0));
        put(DefenceType.BIG_SHIELD, new Resources(50000, 50000, 0));
        put(DefenceType.DEFENCE_MISSILE, new Resources(8000, 2000, 0));
        put(DefenceType.MISSILE, new Resources(12500, 2500, 10000));
    }};

    private static final Map<ShipType, Resources> ships = new HashMap<ShipType, Resources>() {{
        put(ShipType.LIGHT_FIGHTER, new Resources(3000, 1000, 0));
        put(ShipType.HEAVY_FIGHTER, new Resources(6000, 4000, 0));
        put(ShipType.CRUISER, new Resources(20000, 7000, 2000));
        put(ShipType.BATTLESHIP, new Resources(45000, 15000, 0));
        put(ShipType.BATTLECRUISER, new Resources(30000, 40000, 15000));
        put(ShipType.BOMBER, new Resources(50000, 250000, 15000));
        put(ShipType.DESTROYER, new Resources(60000, 50000, 15000));
        put(ShipType.DEATHSTAR, new Resources(5000000, 4000000, 1000000));

        put(ShipType.SMALL_CARGO, new Resources(2000, 2000, 0));
        put(ShipType.LARGE_CARGO, new Resources(6000, 6000, 0));
        put(ShipType.COLONY_SHIP, new Resources(10000, 20000, 10000));
        put(ShipType.RECYCLER, new Resources(10000, 6000, 2000));
        put(ShipType.ESPIONAGE_PROBE, new Resources(0, 1000, 0));
        put(ShipType.SOLAR_SATELLITE, new Resources(0, 2000, 500));
    }};

    private static final Map<ShipType, Integer> fuelCost = new HashMap<ShipType, Integer>() {{
        put(ShipType.LIGHT_FIGHTER, 20);
        put(ShipType.HEAVY_FIGHTER, 75);
        put(ShipType.CRUISER, 300);
        put(ShipType.BATTLESHIP, 500);
        put(ShipType.BATTLECRUISER, 250);
        put(ShipType.BOMBER, 2000);
        put(ShipType.DESTROYER, 1000);
        put(ShipType.DEATHSTAR, 1);

        put(ShipType.SMALL_CARGO, 20);
        put(ShipType.LARGE_CARGO, 50);
        put(ShipType.COLONY_SHIP, 1000);
        put(ShipType.RECYCLER, 300);
        put(ShipType.ESPIONAGE_PROBE, 1);
        put(ShipType.SOLAR_SATELLITE, 0);
    }};

    private static final Map<ShipType, Integer> shipSpeed = new HashMap<ShipType, Integer>() {{
        put(ShipType.LIGHT_FIGHTER, 12500);
        put(ShipType.HEAVY_FIGHTER, 10000);
        put(ShipType.CRUISER, 15000);
        put(ShipType.BATTLESHIP, 10000);
        put(ShipType.BATTLECRUISER, 10000);
        put(ShipType.BOMBER, 4000);
        put(ShipType.DESTROYER, 5000);
        put(ShipType.DEATHSTAR, 100);

        put(ShipType.SMALL_CARGO, 5000);
        put(ShipType.LARGE_CARGO, 7500);
        put(ShipType.COLONY_SHIP, 2500);
        put(ShipType.RECYCLER, 2000);
        put(ShipType.ESPIONAGE_PROBE, 100000000);
        put(ShipType.SOLAR_SATELLITE, 0);
    }};


    private static final Map<FleetSpeed, Integer> speedValue = new HashMap<FleetSpeed, Integer>() {{
        put(FleetSpeed.S10, 1);
        put(FleetSpeed.S20, 2);
        put(FleetSpeed.S30, 3);
        put(FleetSpeed.S40, 4);
        put(FleetSpeed.S50, 5);
        put(FleetSpeed.S60, 6);
        put(FleetSpeed.S70, 7);
        put(FleetSpeed.S80, 8);
        put(FleetSpeed.S90, 9);
        put(FleetSpeed.S100, 10);
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
        put(ResearchType.ARMOR, new Resources(1000, 0));
    }};

    public static Resources getBuildingPrice(BuildingType type, Integer currentLevel) {
        switch (type) {
            case METAL_MINE:
            case DEUTERIUM_MINE:
            case SOLAR_PLANT:
                return buildings.get(type).multiply(Math.pow(1.5, currentLevel));
            case CRYSTAL_MINE:
                return buildings.get(type).multiply(Math.pow(1.6, currentLevel));
            case SOLAR_SATELLITE:
                return buildings.get(type);
            case METAL_STORAGE:
            case CRYSTAL_STORAGE:
            case DEUTERIUM_STORAGE:
                return buildings.get(type).multiply(Math.pow(2, currentLevel));
        }
        return new Resources(10000000, 10000000, 10000000);
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
            case ASTROPHYSICS:
                return researches.get(type).multiply(Math.pow(1.75, currentLevel));
            case MIS:
            case GRAVITY:
            case WEAPON:
            case SHIELDS:
            case ARMOR:
            case LASER:
            case ION:
            case HYPER:
            case PLASMA:
            case REACTIVE_ENGINE:
            case IMPULSE_ENGINE:
            case HYPER_ENGINE:
            case ESPIONAGE:
            case COMPUTER:
            case ENERGY:
                return researches.get(type).multiply(Math.pow(2, currentLevel));
        }
        return null;
    }

    public static Resources getDefencePrice(DefenceType type) {
        return defence.get(type);
    }

    public static Resources getShipPrice(ShipType type) {
        return ships.get(type);
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
                        && empire.getResearches().getReactiveEngine() >= 2;
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
                return planet.getFactories().getResearchLab() >= 1
                        && empire.getResearches().getEnergy() >= 2;
            case ION:
                return planet.getFactories().getResearchLab() >= 4
                        && empire.getResearches().getLaser() >= 5
                        && empire.getResearches().getEnergy() >= 4;
            case HYPER:
                return planet.getFactories().getResearchLab() >= 7
                        && empire.getResearches().getShields() >= 5
                        && empire.getResearches().getEnergy() >= 5;
            case PLASMA:
                return planet.getFactories().getResearchLab() >= 1
                        && empire.getResearches().getLaser() >= 10
                        && empire.getResearches().getIon() >= 5
                        && empire.getResearches().getEnergy() >= 8;
            case REACTIVE_ENGINE:
                return planet.getFactories().getResearchLab() >= 1
                        && empire.getResearches().getEnergy() >= 1;
            case IMPULSE_ENGINE:
                return planet.getFactories().getResearchLab() >= 2
                        && empire.getResearches().getEnergy() >= 1;
            case HYPER_ENGINE:
                return planet.getFactories().getResearchLab() >= 1
                        && empire.getResearches().getHyper() >= 3;
            case ESPIONAGE:
                return planet.getFactories().getResearchLab() >= 3;
            case COMPUTER:
                return planet.getFactories().getResearchLab() >= 1;
            case ASTROPHYSICS:
                return planet.getFactories().getResearchLab() >= 1
                        && empire.getResearches().getEspionage() >= 4
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

    public static Boolean canBuild(Empire empire, Planet planet, DefenceType type) {
        switch (type) {
            case ROCKET:
                return planet.getFactories().getShipyard() >= 2;
            case LIGHT_LASER:
                return planet.getFactories().getShipyard() >= 2
                        && empire.getResearches().getLaser() >= 3;
            case HEAVY_LASER:
                return planet.getFactories().getShipyard() >= 4
                        && empire.getResearches().getLaser() >= 6
                        && empire.getResearches().getEnergy() >= 3;
            case GAUSS:
                return planet.getFactories().getShipyard() >= 6
                        && empire.getResearches().getWeapon() >= 3
                        && empire.getResearches().getEnergy() >= 6
                        && empire.getResearches().getShields() >= 1;
            case ION:
                return planet.getFactories().getShipyard() >= 4
                        && empire.getResearches().getIon() >= 4;
            case PLASMA:
                return planet.getFactories().getShipyard() >= 8
                        && empire.getResearches().getPlasma() >= 7;
            case SMALL_SHIELD:
                return planet.getFactories().getShipyard() >= 1
                        && empire.getResearches().getShields() >= 2;
            case BIG_SHIELD:
                return planet.getFactories().getShipyard() >= 6
                        && empire.getResearches().getShields() >= 6;
            case DEFENCE_MISSILE:
                return planet.getFactories().getMissileSilos() >= 2;
            case MISSILE:
                return planet.getFactories().getMissileSilos() >= 4;
        }
        return false;
    }

    public static Integer getMetalProduction(Integer level) {
        Double value = 30 * level * Math.pow(1.1, level) * ContextHolder.getBotConfigMain().UNIVERSE_SPEED;
        return value.intValue();
    }

    public static Integer getCrystalProduction(Integer level) {
        Double value = 20 * level * Math.pow(1.1, level) * ContextHolder.getBotConfigMain().UNIVERSE_SPEED;
        return value.intValue();
    }

    public static Integer getDeuteriumProduction(Integer level) {
        Double value = 10 * level * Math.pow(1.1, level) * (-0.004 * 0 + 1.36) * ContextHolder.getBotConfigMain().UNIVERSE_SPEED;
        return value.intValue();
    }

    public static Integer getStorageCapacity(Integer level) {
        Double value = 5000 * Math.floor(2.5 * Math.exp(20 * level.doubleValue() / 33));
        return value.intValue();
    }

    public static Integer getDistance(Coordinates c1, Coordinates c2) {
        if (!c1.getGalaxy().equals(c2.getGalaxy())) {
            return 20000 * Math.abs(c1.getGalaxy() - c2.getGalaxy());
        }
        if (!c1.getSystem().equals(c2.getSystem())) {
            return 2700 + 95 * Math.abs(c1.getSystem() - c2.getSystem());
        }
        return 1000 + 5 * Math.abs(c1.getPlanet() - c2.getPlanet());
    }

    public static Integer getDistance(AbstractPlanet p1, AbstractPlanet p2) {
        return getDistance(p1.getCoordinates(), p2.getCoordinates());
    }

    public static Integer getFuelConsumption(Fleet fleet, Integer distance, FleetSpeed speed, Researches researches) {
        Double fuel = .0;
        Double maxSpeed = Math.floor(getFleetSpeed(fleet, researches));
        Double duration = Math.ceil(35000 / speedValue.get(speed) * Math.sqrt(distance * 10 / maxSpeed) + 10) / ContextHolder.getBotConfigMain().UNIVERSE_FLEET_SPEED;

        for (ShipType type : ShipType.values()) {
            if (fleet.get(type) > 0) {
                Double f;
                f = fleet.get(type) * fuelCost.get(type) * ContextHolder.getBotConfigMain().FUEL_CONSUMPTION_MULTIPLIER * distance / 35000.0 *
                        Math.pow(
                                (
                                        35000 / (duration * ContextHolder.getBotConfigMain().UNIVERSE_FLEET_SPEED - 10) *
                                                Math.sqrt(distance * 10 / getSpeed(type, researches)) / 10 + 1)
                                , 2);
                fuel += f;
            }
        }
        fuel = Math.ceil(fuel);
        return fuel.intValue();
    }

    public static Double getSpeed(ShipType type, Researches researches) {
        switch (type) {
            case LIGHT_FIGHTER:
            case LARGE_CARGO:
            case ESPIONAGE_PROBE:
                return getRealSpeed(shipSpeed.get(type), getDriveBonus(ResearchType.REACTIVE_ENGINE), researches.getReactiveEngine());
            case HEAVY_FIGHTER:
            case CRUISER:
            case COLONY_SHIP:
                return getRealSpeed(shipSpeed.get(type), getDriveBonus(ResearchType.IMPULSE_ENGINE), researches.getImpulseEngine());
            case BATTLESHIP:
            case BATTLECRUISER:
            case DESTROYER:
            case DEATHSTAR:
                return getRealSpeed(shipSpeed.get(type), getDriveBonus(ResearchType.HYPER_ENGINE), researches.getHyperEngine());
            case BOMBER:
                if (researches.getHyperEngine() >= 8) {
                    return getRealSpeed(5000, getDriveBonus(ResearchType.HYPER_ENGINE), researches.getHyperEngine());
                }
                return getRealSpeed(shipSpeed.get(type), getDriveBonus(ResearchType.IMPULSE_ENGINE), researches.getImpulseEngine());
            case SMALL_CARGO:
                if (researches.getImpulseEngine() >= 5) {
                    return getRealSpeed(10000, getDriveBonus(ResearchType.IMPULSE_ENGINE), researches.getImpulseEngine());
                }
                return getRealSpeed(shipSpeed.get(type), getDriveBonus(ResearchType.REACTIVE_ENGINE), researches.getReactiveEngine());
            case RECYCLER:
                if (researches.getHyperEngine() >= 15) {
                    return getRealSpeed(6000, getDriveBonus(ResearchType.HYPER_ENGINE), researches.getHyperEngine());
                }
                if (researches.getImpulseEngine() >= 17) {
                    return getRealSpeed(4000, getDriveBonus(ResearchType.IMPULSE_ENGINE), researches.getImpulseEngine());
                }
                return getRealSpeed(shipSpeed.get(type), getDriveBonus(ResearchType.REACTIVE_ENGINE), researches.getReactiveEngine());
            case SOLAR_SATELLITE:
                return .0;
        }
        return .0;
    }

    private static Double getRealSpeed(Integer speed, Double driveBonus, Integer driveLevel) {
        return speed * (1 + driveBonus * driveLevel);
    }

    private static Double getDriveBonus(ResearchType type) {
        switch (type) {
            case REACTIVE_ENGINE:
                return .1;
            case IMPULSE_ENGINE:
                return .2;
            case HYPER_ENGINE:
                return .3;
            default:
                return .0;
        }
    }

    public static Double getFleetSpeed(Fleet fleet, Researches researches) {
        Double speed = Double.MAX_VALUE;
        for (ShipType type : ShipType.values()) {
            if (fleet.get(type) > 0) {
                speed = Double.min(speed, getSpeed(type, researches));
            }
        }
        return speed;
    }
}
