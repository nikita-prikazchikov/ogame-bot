package ru.tki.models;

import com.google.gson.Gson;

public class Resources {

    private Integer metal = 0;
    private Integer crystal = 0;
    private Integer deuterium = 0;
    private Integer energy = 0;

    public Resources() {
    }

    public Resources(Integer metal, Integer crystal) {
        this.metal = metal;
        this.crystal = crystal;
    }

    public Resources(Integer metal, Integer crystal, Integer deuterium) {
        this(metal, crystal);
        this.deuterium = deuterium;
    }

    public Resources(Integer metal, Integer crystal, Integer deuterium, Integer energy) {
        this(metal, crystal, deuterium);
        this.energy = energy;
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

    public Integer getEnergy() {
        return energy;
    }

    public Resources setEnergy(Integer energy) {
        this.energy = energy;
        return this;
    }

    public Integer getCapacity(){
        return metal + crystal + deuterium;
    }

    public Resources multiply(Double value){
        return new Resources(
                ((Double)(metal * value)).intValue(),
                ((Double)(crystal * value)).intValue(),
                ((Double)(deuterium * value)).intValue(),
                ((Double)(energy * value)).intValue());
    }

    public Resources multiply(Integer value){
        return new Resources(
                metal * value,
                crystal * value,
                deuterium * value,
                energy * value);
    }

    public Resources add (Resources resources){
        return new Resources(
                metal + resources.getMetal(),
                crystal + resources.getCrystal(),
                deuterium + resources.getDeuterium(),
                energy + resources.getEnergy()
        );
    }

    public Resources deduct (Resources resources) {
        return new Resources(
                Math.max(0, metal - resources.getMetal()),
                Math.max(0, crystal - resources.getCrystal()),
                Math.max(0, deuterium - resources.getDeuterium()),
                Math.max(0, energy - resources.getEnergy())
        );
    }

    public Boolean isEnoughFor(Resources resources) {
        return metal >= resources.getMetal()
                && crystal >= resources.getCrystal()
                && deuterium >= resources.getDeuterium()
                && (resources.getEnergy() == 0 || energy >= resources.getEnergy());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
