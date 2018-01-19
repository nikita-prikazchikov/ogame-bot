package ru.tki.models.actions;

import ru.tki.models.AbstractPlanet;

import java.time.Duration;
import java.time.Instant;

public abstract class Action {

    protected AbstractPlanet planet;
    protected Duration       duration;
    protected Instant        startDate;

    public Action() {
        startDate = Instant.now();
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Instant getStartDate() {
        return startDate;
    }

    @Override
    public String toString() {
        return "Action{" +
                "planet=" + planet +
                "startDate=" + startDate +
                ", duration=" + duration +
                '}';
    }
}
