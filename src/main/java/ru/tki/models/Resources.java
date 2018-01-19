package ru.tki.models;

public class Resources {

    private Integer metal;
    private Integer crystal;
    private Integer deuterium;

    public Resources() {
    }

    public Resources(Integer metal, Integer crystal, Integer deuterium) {
        this.metal = metal;
        this.crystal = crystal;
        this.deuterium = deuterium;
    }

    public Integer getMetal() {
        return metal;
    }

    public Resources setMetal(int metal) {
        this.metal = metal;
        return this;
    }

    public Integer getCrystal() {
        return crystal;
    }

    public Resources setCrystal(int crystal) {
        this.crystal = crystal;
        return this;
    }

    public Integer getDeuterium() {
        return deuterium;
    }

    public Resources setDeuterium(int deuterium) {
        this.deuterium = deuterium;
        return this;
    }
}
