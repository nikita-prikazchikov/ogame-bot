package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Resources;
import ru.tki.models.actions.Action;

import java.time.Instant;

public abstract class Task {

    private   Instant        executionDate;
    protected AbstractPlanet planet;
    protected Resources resources = new Resources();
    protected Task subtask = null;

    public Action execute() {
        if (null != planet) {
            planet.setHasTask(false);
        }
        return null;
    }

    public void setPlanet(AbstractPlanet planet) {
        planet.setHasTask(true);
        this.planet = planet;
    }

    public AbstractPlanet getPlanet() {
        return planet;
    }

    public boolean canExecute() {
        return (null == executionDate || Instant.now().compareTo(executionDate) < 0);
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

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }
}
