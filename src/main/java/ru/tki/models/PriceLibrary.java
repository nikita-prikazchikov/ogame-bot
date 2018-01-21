package ru.tki.models;

import ru.tki.models.types.BuildingType;

import java.util.HashMap;
import java.util.Map;

public class PriceLibrary {

    private static final Map<BuildingType, Resources> buildings = new HashMap<BuildingType, Resources>() {{
        put(BuildingType.METAL_MINE, new Resources(60, 15));
        put(BuildingType.CRYSTAL_MINE, new Resources(48, 24));
        put(BuildingType.DEUTERIUM_MINE, new Resources(225, 75));
        put(BuildingType.SOLAR_PLANT, new Resources(75, 30));
        put(BuildingType.SOLAR_SATELLITE, new Resources(0, 2000, 500));
        put(BuildingType.METAL_STORAGE, new Resources(1000, 0));
        put(BuildingType.CRYSTAL_STORAGE,new Resources(1000, 500));
        put(BuildingType.DEUTERIUM_STORAGE, new Resources(1000, 1000));
    }};

    public static Resources getBuildingPrice(BuildingType type, Integer level){
        switch (type){
            case METAL_MINE:
            case CRYSTAL_MINE:
            case DEUTERIUM_MINE:
            case SOLAR_PLANT:
                return buildings.get(type).multiply(Math.pow(1.5, level - 1));
            case SOLAR_SATELLITE:
                return buildings.get(type);
            case METAL_STORAGE:
            case CRYSTAL_STORAGE:
            case DEUTERIUM_STORAGE:
                return buildings.get(type).multiply(Math.pow(2, level - 1));
        }
        return null;
    }
}
