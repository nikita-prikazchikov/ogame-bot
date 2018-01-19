package ru.tki.models;

import ru.tki.models.types.FactoryType;

public class Factories {

    Integer robotsFactory,
            shipyard,
            researchLab,
            allianceWarehouse,
            missileSilos,
            naniteFactory,
            terraformer,
            spaceDock;

    public Factories() {
        this.robotsFactory = 0;
        this.shipyard = 0;
        this.researchLab = 0;
        this.allianceWarehouse = 0;
        this.missileSilos = 0;
        this.naniteFactory = 0;
        this.terraformer = 0;
        this.spaceDock = 0;
    }

    public Integer get(FactoryType type) {
        switch (type) {
            case ROBOTS_FACTORY:
                return robotsFactory;
            case SHIPYARD:
                return shipyard;
            case RESEARCH_LAB:
                return researchLab;
            case ALLIANCE_WAREHOUSE:
                return allianceWarehouse;
            case MISSILE_SILOS:
                return missileSilos;
            case NANITE_FACTORY:
                return naniteFactory;
            case TERRAFORMER:
                return terraformer;
            case SPACE_DOCK:
                return spaceDock;
        }
        return 0;
    }

    public void set(FactoryType type, Integer count) {
        switch (type) {
            case ROBOTS_FACTORY:
                setRobotsFactory(count);
                break;
            case SHIPYARD:
                setShipyard(count);
                break;
            case RESEARCH_LAB:
                setResearchLab(count);
                break;
            case ALLIANCE_WAREHOUSE:
                setAllianceWarehouse(count);
                break;
            case MISSILE_SILOS:
                setMissileSilos(count);
                break;
            case NANITE_FACTORY:
                setNaniteFactory(count);
                break;
            case TERRAFORMER:
                setTerraformer(count);
                break;
            case SPACE_DOCK:
                setSpaceDock(count);
                break;
        }
    }

    public Integer getRobotsFactory() {
        return robotsFactory;
    }

    public void setRobotsFactory(Integer robotsFactory) {
        this.robotsFactory = robotsFactory;
    }

    public Integer getShipyard() {
        return shipyard;
    }

    public void setShipyard(Integer shipyard) {
        this.shipyard = shipyard;
    }

    public Integer getResearchLab() {
        return researchLab;
    }

    public void setResearchLab(Integer researchLab) {
        this.researchLab = researchLab;
    }

    public Integer getAllianceWarehouse() {
        return allianceWarehouse;
    }

    public void setAllianceWarehouse(Integer allianceWarehouse) {
        this.allianceWarehouse = allianceWarehouse;
    }

    public Integer getMissileSilos() {
        return missileSilos;
    }

    public void setMissileSilos(Integer missileSilos) {
        this.missileSilos = missileSilos;
    }

    public Integer getNaniteFactory() {
        return naniteFactory;
    }

    public void setNaniteFactory(Integer naniteFactory) {
        this.naniteFactory = naniteFactory;
    }

    public Integer getTerraformer() {
        return terraformer;
    }

    public void setTerraformer(Integer terraformer) {
        this.terraformer = terraformer;
    }

    public Integer getSpaceDock() {
        return spaceDock;
    }

    public void setSpaceDock(Integer spaceDock) {
        this.spaceDock = spaceDock;
    }
}
