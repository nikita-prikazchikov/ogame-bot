package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Resources;
import ru.tki.models.actions.Action;

import java.time.Instant;

public abstract class Task {

    private   Instant        executionDate;
    private   AbstractPlanet planet;
    protected String         name;

    protected Resources resources  = new Resources();
    protected Task      subtask    = null;
    protected Boolean   isExecuted = false;

    public Action execute() {
        if (null != getPlanet()) {
            getPlanet().setHasTask(false);
        }
        return null;
    }

    //Delete task and all it blocking requirements
    public void remove(){
        if (null != getPlanet()) {
            getPlanet().setHasTask(false);
        }
    }

    public void setPlanet(AbstractPlanet planet) {
        setPlanet(planet, true);
    }

    public void setPlanet(AbstractPlanet planet, Boolean planetWillBeChanged) {
        planet.setHasTask(planetWillBeChanged);
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

    public Boolean isExecuted() {
        return isExecuted;
    }

    public void setExecuted(Boolean executed) {
        isExecuted = executed;
    }
}
