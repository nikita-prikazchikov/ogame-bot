package ru.tki.models;

import ru.tki.models.types.ResearchType;

public class Researches {

    Integer energy,
            laser,
            ion,
            hyper,
            plasma,
            reactiveEngine,
            impulseEngine,
            hyperEngine,
            espionage,
            computer,
            astrophysics,
            mis,
            gravity,
            weapon,
            shields,
            armor;

    public Researches() {
        this.energy = 0;
        this.laser = 0;
        this.ion = 0;
        this.hyper = 0;
        this.plasma = 0;
        this.reactiveEngine = 0;
        this.impulseEngine = 0;
        this.hyperEngine = 0;
        this.espionage = 0;
        this.computer = 0;
        this.astrophysics = 0;
        this.mis = 0;
        this.gravity = 0;
        this.weapon = 0;
        this.shields = 0;
        this.armor = 0;
    }

    public Integer get(ResearchType type) {
        switch (type) {
            case ENERGY:
                return energy;
            case LASER:
                return laser;
            case ION:
                return ion;
            case HYPER:
                return hyper;
            case PLASMA:
                return plasma;
            case REACTIVE_ENGINE:
                return reactiveEngine;
            case IMPULSE_ENGINE:
                return impulseEngine;
            case HYPER_ENGINE:
                return hyperEngine;
            case ESPIONAGE:
                return espionage;
            case COMPUTER:
                return computer;
            case ASTROPHYSICS:
                return astrophysics;
            case MIS:
                return mis;
            case GRAVITY:
                return gravity;
            case WEAPON:
                return weapon;
            case SHIELDS:
                return shields;
            case ARMOR:
                return armor;
        }
        return 0;
    }

    public void set(ResearchType type, Integer count) {
        switch (type) {
            case ENERGY:
                setEnergy(count);
                break;
            case LASER:
                setLaser(count);
                break;
            case ION:
                setIon(count);
                break;
            case HYPER:
                setHyper(count);
                break;
            case PLASMA:
                setPlasma(count);
                break;
            case REACTIVE_ENGINE:
                setReactiveEngine(count);
                break;
            case IMPULSE_ENGINE:
                setImpulseEngine(count);
                break;
            case HYPER_ENGINE:
                setHyperEngine(count);
                break;
            case ESPIONAGE:
                setEspionage(count);
                break;
            case COMPUTER:
                setComputer(count);
                break;
            case ASTROPHYSICS:
                setAstrophysics(count);
                break;
            case MIS:
                setMis(count);
                break;
            case GRAVITY:
                setGravity(count);
                break;
            case WEAPON:
                setWeapon(count);
                break;
            case SHIELDS:
                setShields(count);
                break;
            case ARMOR:
                setArmor(count);
                break;
        }
    }

    public Integer getEnergy() {
        return energy;
    }

    public void setEnergy(Integer energy) {
        this.energy = energy;
    }

    public Integer getLaser() {
        return laser;
    }

    public void setLaser(Integer laser) {
        this.laser = laser;
    }

    public Integer getIon() {
        return ion;
    }

    public void setIon(Integer ion) {
        this.ion = ion;
    }

    public Integer getHyper() {
        return hyper;
    }

    public void setHyper(Integer hyper) {
        this.hyper = hyper;
    }

    public Integer getPlasma() {
        return plasma;
    }

    public void setPlasma(Integer plasma) {
        this.plasma = plasma;
    }

    public Integer getReactiveEngine() {
        return reactiveEngine;
    }

    public void setReactiveEngine(Integer reactiveEngine) {
        this.reactiveEngine = reactiveEngine;
    }

    public Integer getImpulseEngine() {
        return impulseEngine;
    }

    public void setImpulseEngine(Integer impulseEngine) {
        this.impulseEngine = impulseEngine;
    }

    public Integer getHyperEngine() {
        return hyperEngine;
    }

    public void setHyperEngine(Integer hyperEngine) {
        this.hyperEngine = hyperEngine;
    }

    public Integer getEspionage() {
        return espionage;
    }

    public void setEspionage(Integer espionage) {
        this.espionage = espionage;
    }

    public Integer getComputer() {
        return computer;
    }

    public void setComputer(Integer computer) {
        this.computer = computer;
    }

    public Integer getAstrophysics() {
        return astrophysics;
    }

    public void setAstrophysics(Integer astrophysics) {
        this.astrophysics = astrophysics;
    }

    public Integer getMis() {
        return mis;
    }

    public void setMis(Integer mis) {
        this.mis = mis;
    }

    public Integer getGravity() {
        return gravity;
    }

    public void setGravity(Integer gravity) {
        this.gravity = gravity;
    }

    public Integer getWeapon() {
        return weapon;
    }

    public void setWeapon(Integer weapon) {
        this.weapon = weapon;
    }

    public Integer getShields() {
        return shields;
    }

    public void setShields(Integer shields) {
        this.shields = shields;
    }

    public Integer getArmor() {
        return armor;
    }

    public void setArmor(Integer armor) {
        this.armor = armor;
    }
}
