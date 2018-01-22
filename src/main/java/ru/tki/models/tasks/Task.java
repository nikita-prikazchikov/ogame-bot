package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.actions.Action;

import java.time.Instant;

public abstract class Task {

    private   Instant        executionDate;
    protected AbstractPlanet planet;
    protected Task subtask = null;

    public abstract Action execute();

    public void setPlanet(AbstractPlanet planet) {
        this.planet = planet;
    }

    public AbstractPlanet getPlanet() {
        return planet;
    }

    public Instant getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Instant executionDate) {
        this.executionDate = executionDate;
    }

    public Task getSubtask() {
        return subtask;
    }

    public boolean hasSubtask() {
        return null != subtask;
    }

    public void setSubtask(Task subtask) {
        this.subtask = subtask;
    }

    public boolean canExecute() {
        return (null == executionDate || Instant.now().compareTo(executionDate) < 0);
    }
}
