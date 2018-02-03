package ru.tki.models;

import ru.tki.models.types.DefenceType;

public class Defence {

    Integer rocket,
            lightLaser,
            heavyLaser,
            gauss,
            ion,
            plasma,
            smallShield,
            bigShield,
            defenceMissile,
            missile;

    public Defence() {
        this.rocket = 0;
        this.lightLaser = 0;
        this.heavyLaser = 0;
        this.gauss = 0;
        this.ion = 0;
        this.plasma = 0;
        this.smallShield = 0;
        this.bigShield = 0;
        this.defenceMissile = 0;
        this.missile = 0;
    }

    public Defence(DefenceType type, Integer count) {
        this();
        set(type, count);
    }

    public Defence(DefenceType type, Integer count, DefenceType type2, Integer count2) {
        this();
        set(type, count);
        set(type2, count2);
    }

    public Defence(DefenceType type, Integer count, DefenceType type2, Integer count2, DefenceType type3, Integer count3) {
        this();
        set(type, count);
        set(type2, count2);
        set(type3, count3);
    }

    public Integer get(DefenceType type){
        switch (type){
            case ROCKET:
                return rocket;
            case LIGHT_LASER:
                return lightLaser;
            case HEAVY_LASER:
                return heavyLaser;
            case GAUSS:
                return gauss;
            case ION:
                return ion;
            case PLASMA:
                return plasma;
            case SMALL_SHIELD:
                return smallShield;
            case BIG_SHIELD:
                return bigShield;
            case DEFENCE_MISSILE:
                return defenceMissile;
            case MISSILE:
                return missile;
        }
        return 0;
    }

    public void set(DefenceType type, Integer count){
        switch (type){
            case ROCKET:
                setRocket(count);
                break;
            case LIGHT_LASER:
                setLightLaser(count);
                break;
            case HEAVY_LASER:
                setHeavyLaser(count);
                break;
            case GAUSS:
                setGauss(count);
                break;
            case ION:
                setIon(count);
                break;
            case PLASMA:
                setPlasma(count);
                break;
            case SMALL_SHIELD:
                setSmallShield(count);
                break;
            case BIG_SHIELD:
                setBigShield(count);
                break;
            case DEFENCE_MISSILE:
                setDefenceMissile(count);
                break;
            case MISSILE:
                setMissile(count);
                break;
        }
    }

    public Long getCost(){
        Long cost = 0L;
        for(DefenceType type:  DefenceType.values()){
            cost += OGameLibrary.getDefencePrice(type).getCapacity() * get(type);
        }
        return cost;
    }

    public Integer getRocket() {
        return rocket;
    }

    public void setRocket(Integer rocket) {
        this.rocket = rocket;
    }

    public Integer getLightLaser() {
        return lightLaser;
    }

    public void setLightLaser(Integer lightLaser) {
        this.lightLaser = lightLaser;
    }

    public Integer getHeavyLaser() {
        return heavyLaser;
    }

    public void setHeavyLaser(Integer heavyLaser) {
        this.heavyLaser = heavyLaser;
    }

    public Integer getGauss() {
        return gauss;
    }

    public void setGauss(Integer gauss) {
        this.gauss = gauss;
    }

    public Integer getIon() {
        return ion;
    }

    public void setIon(Integer ion) {
        this.ion = ion;
    }

    public Integer getPlasma() {
        return plasma;
    }

    public void setPlasma(Integer plasma) {
        this.plasma = plasma;
    }

    public Integer getSmallShield() {
        return smallShield;
    }

    public void setSmallShield(Integer smallShield) {
        this.smallShield = smallShield;
    }

    public Integer getBigShield() {
        return bigShield;
    }

    public void setBigShield(Integer bigShield) {
        this.bigShield = bigShield;
    }

    public Integer getDefenceMissile() {
        return defenceMissile;
    }

    public void setDefenceMissile(Integer defenceMissile) {
        this.defenceMissile = defenceMissile;
    }

    public Integer getMissile() {
        return missile;
    }

    public void setMissile(Integer missile) {
        this.missile = missile;
    }
}
