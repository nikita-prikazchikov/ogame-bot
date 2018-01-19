package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.actions.Action;

import java.util.Date;

public abstract class Task {

    private   Date           executionDate;
    protected AbstractPlanet planet;

    public abstract Action execute();

    public void setPlanet(AbstractPlanet planet) {
        this.planet = planet;
    }

    public AbstractPlanet getPlanet() {
        return planet;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }
}
