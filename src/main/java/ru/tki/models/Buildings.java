package ru.tki.models;

import ru.tki.models.types.BuildingType;

public class Buildings {

    Integer metalMine,
            crystalMine,
            deuteriumMine,
            solarPlant,
            solarSatellite,
            metalStorage,
            crystalStorage,
            deuteriumStorage;

    public Buildings() {
        this.metalMine = 0;
        this.crystalMine = 0;
        this.deuteriumMine = 0;
        this.solarPlant = 0;
        this.solarSatellite = 0;
        this.metalStorage = 0;
        this.crystalStorage = 0;
        this.deuteriumStorage = 0;
    }

    public Integer get(BuildingType type){
        switch (type){
            case METAL_MINE:
                return metalMine;
            case CRYSTAL_MINE:
                return crystalMine;
            case DEUTERIUM_MINE:
                return deuteriumMine;
            case SOLAR_PLANT:
                return solarPlant;
            case SOLAR_SATELLITE:
                return solarSatellite;
            case METAL_STORAGE:
                return metalStorage;
            case CRYSTAL_STORAGE:
                return crystalStorage;
            case DEUTERIUM_STORAGE:
                return deuteriumStorage;
        }
        return 0;
    }

    public void set(BuildingType type, Integer count){
        switch (type){
            case METAL_MINE:
                setMetalMine(count);
                break;
            case CRYSTAL_MINE:
                setCrystalMine(count);
                break;
            case DEUTERIUM_MINE:
                setDeuteriumMine(count);
                break;
            case SOLAR_PLANT:
                setSolarPlant(count);
                break;
            case SOLAR_SATELLITE:
                setSolarSatellite(count);
                break;
            case METAL_STORAGE:
                setMetalStorage(count);
                break;
            case CRYSTAL_STORAGE:
                setCrystalStorage(count);
                break;
            case DEUTERIUM_STORAGE:
                setDeuteriumStorage(count);
                break;
        }
    }

    public Integer getMetalMine() {
        return metalMine;
    }

    public void setMetalMine(Integer metalMine) {
        this.metalMine = metalMine;
    }

    public Integer getCrystalMine() {
        return crystalMine;
    }

    public void setCrystalMine(Integer crystalMine) {
        this.crystalMine = crystalMine;
    }

    public Integer getDeuteriumMine() {
        return deuteriumMine;
    }

    public void setDeuteriumMine(Integer deuteriumMine) {
        this.deuteriumMine = deuteriumMine;
    }

    public Integer getSolarPlant() {
        return solarPlant;
    }

    public void setSolarPlant(Integer solarPlant) {
        this.solarPlant = solarPlant;
    }

    public Integer getSolarSatellite() {
        return solarSatellite;
    }

    public void setSolarSatellite(Integer solarSatellite) {
        this.solarSatellite = solarSatellite;
    }

    public Integer getMetalStorage() {
        return metalStorage;
    }

    public void setMetalStorage(Integer metalStorage) {
        this.metalStorage = metalStorage;
    }

    public Integer getCrystalStorage() {
        return crystalStorage;
    }

    public void setCrystalStorage(Integer crystalStorage) {
        this.crystalStorage = crystalStorage;
    }

    public Integer getDeuteriumStorage() {
        return deuteriumStorage;
    }

    public void setDeuteriumStorage(Integer deuteriumStorage) {
        this.deuteriumStorage = deuteriumStorage;
    }
}
