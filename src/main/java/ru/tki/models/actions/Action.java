package ru.tki.models.actions;

import ru.tki.models.AbstractPlanet;

import java.time.Duration;
import java.time.Instant;

public abstract class Action {

    protected AbstractPlanet planet;
    protected Instant        endDate;

    public Action() {
    }

    public Action(AbstractPlanet planet) {
        this.planet = planet;
    }

    public AbstractPlanet getPlanet() {
        return planet;
    }

    public void setPlanet(AbstractPlanet planet) {
        this.planet = planet;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(Duration duration){
        Instant instant = Instant.now();
        //add 5 seconds to avoid time difference between object creation and actual execution of build time
        this.endDate = instant.plus(duration).plusSeconds(5);
    }

    @Override
    public String toString() {
        return "Action{" +
                "planet=" + planet +
                ", endDate=" + endDate +
                '}';
    }
}
