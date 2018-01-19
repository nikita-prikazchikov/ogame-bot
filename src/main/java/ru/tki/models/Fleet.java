package ru.tki.models;


import ru.tki.models.types.ShipType;

public class Fleet {

    Integer lightFighter,
            heavyFighter,
            cruiser,
            battleship,
            battlecruiser,
            bomber,
            destroyer,
            deathStar,
            smallCargo,
            largeCargo,
            colonyShip,
            recycler,
            espionageProbe,
            solarSatellite;

    public Fleet() {
    }

    public Fleet(ShipType type, Integer count) {
        setShipCount(type, count);
    }

    public Fleet(ShipType type, Integer count, ShipType type2, Integer count2) {
        setShipCount(type, count);
        setShipCount(type2, count2);
    }

    public Fleet(ShipType type, Integer count, ShipType type2, Integer count2, ShipType type3, Integer count3) {
        setShipCount(type, count);
        setShipCount(type2, count2);
        setShipCount(type3, count3);
    }

    public int getShipCount(ShipType type){
        switch (type){

            case LIGHT_FIGHTER:
                return lightFighter;
            case HEAVY_FIGHTER:
                return heavyFighter;
            case CRUISER:
                return cruiser;
            case BATTLESHIP:
                return battleship;
            case BATTLECRUISER:
                return battlecruiser;
            case BOMBER:
                return bomber;
            case DESTROYER:
                return destroyer;
            case DEATHSTAR:
                return deathStar;
            case SMALL_CARGO:
                return smallCargo;
            case LARGE_CARGO:
                return largeCargo;
            case COLONY_SHIP:
                return colonyShip;
            case RECYCLER:
                return recycler;
            case ESPIONAGE_PROBE:
                return espionageProbe;
            case SOLAR_SATELLITE:
                return solarSatellite;
        }
        return 0;
    }

    public void setShipCount(ShipType type, Integer count){
        switch (type){
            case LIGHT_FIGHTER:
                setLightFighter(count);
                break;
            case HEAVY_FIGHTER:
                setHeavyFighter(count);
                break;
            case CRUISER:
                setCruiser(count);
                break;
            case BATTLESHIP:
                setBattleship(count);
                break;
            case BATTLECRUISER:
                setBattlecruiser(count);
                break;
            case BOMBER:
                setBomber(count);
                break;
            case DESTROYER:
                setDestroyer(count);
                break;
            case DEATHSTAR:
                setDeathStar(count);
                break;
            case SMALL_CARGO:
                setSmallCargo(count);
                break;
            case LARGE_CARGO:
                setLargeCargo(count);
                break;
            case COLONY_SHIP:
                setColonyShip(count);
                break;
            case RECYCLER:
                setRecycler(count);
                break;
            case ESPIONAGE_PROBE:
                setEspionageProbe(count);
                break;
            case SOLAR_SATELLITE:
                setSolarSatellite(count);
                break;
        }
    }

    public Integer getLightFighter() {
        return lightFighter;
    }

    public void setLightFighter(Integer lightFighter) {
        this.lightFighter = lightFighter;
    }

    public Integer getHeavyFighter() {
        return heavyFighter;
    }

    public void setHeavyFighter(Integer heavyFighter) {
        this.heavyFighter = heavyFighter;
    }

    public Integer getCruiser() {
        return cruiser;
    }

    public void setCruiser(Integer cruiser) {
        this.cruiser = cruiser;
    }

    public Integer getBattleship() {
        return battleship;
    }

    public void setBattleship(Integer battleship) {
        this.battleship = battleship;
    }

    public Integer getBattlecruiser() {
        return battlecruiser;
    }

    public void setBattlecruiser(Integer battlecruiser) {
        this.battlecruiser = battlecruiser;
    }

    public Integer getBomber() {
        return bomber;
    }

    public void setBomber(Integer bomber) {
        this.bomber = bomber;
    }

    public Integer getDestroyer() {
        return destroyer;
    }

    public void setDestroyer(Integer destroyer) {
        this.destroyer = destroyer;
    }

    public Integer getDeathStar() {
        return deathStar;
    }

    public void setDeathStar(Integer deathStar) {
        this.deathStar = deathStar;
    }

    public Integer getSmallCargo() {
        return smallCargo;
    }

    public void setSmallCargo(Integer smallCargo) {
        this.smallCargo = smallCargo;
    }

    public Integer getLargeCargo() {
        return largeCargo;
    }

    public void setLargeCargo(Integer largeCargo) {
        this.largeCargo = largeCargo;
    }

    public Integer getColonyShip() {
        return colonyShip;
    }

    public void setColonyShip(Integer colonyShip) {
        this.colonyShip = colonyShip;
    }

    public Integer getRecycler() {
        return recycler;
    }

    public void setRecycler(Integer recycler) {
        this.recycler = recycler;
    }

    public Integer getEspionageProbe() {
        return espionageProbe;
    }

    public void setEspionageProbe(Integer espionageProbe) {
        this.espionageProbe = espionageProbe;
    }

    public Integer getSolarSatellite() {
        return solarSatellite;
    }

    public void setSolarSatellite(Integer solarSatellite) {
        this.solarSatellite = solarSatellite;
    }
}
