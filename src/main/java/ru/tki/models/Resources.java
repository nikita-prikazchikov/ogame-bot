package ru.tki.models;

public class Resources {

    private int metal;
    private int crystal;
    private int deuterium;

    public Resources() {
    }

    public Resources(int metal, int crystal, int deuterium) {
        this.metal = metal;
        this.crystal = crystal;
        this.deuterium = deuterium;
    }

    public int getMetal() {
        return metal;
    }

    public Resources setMetal(int metal) {
        this.metal = metal;
        return this;
    }

    public int getCrystal() {
        return crystal;
    }

    public Resources setCrystal(int crystal) {
        this.crystal = crystal;
        return this;
    }

    public int getDeuterium() {
        return deuterium;
    }

    public Resources setDeuterium(int deuterium) {
        this.deuterium = deuterium;
        return this;
    }
}
