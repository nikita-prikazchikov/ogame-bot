package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Resources;
import ru.tki.models.actions.Action;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class Task {

    private   Instant        executionDate;
    private   AbstractPlanet planet;
    protected String         name;

    protected Resources  resources  = new Resources();
    protected List<Task> tasks      = new ArrayList<>();
    protected Boolean    isExecuted = false;

    public Action execute() {
        if (null != getPlanet()) {
            getPlanet().setHasTask(false);
        }
        return null;
    }

    //Delete task and all it blocking requirements
    public void removeFromQueue(){
        if (null != getPlanet()) {
            getPlanet().setHasTask(false);
        }
    }

    public void setPlanet(AbstractPlanet planet) {
        setPlanet(planet, true);
    }

    public void setPlanet(AbstractPlanet planet, Boolean planetWillBeChanged) {
        if(planetWillBeChanged) {
            planet.setHasTask(true);
        }
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

    public List<Task> getTasks() {
        return tasks;
    }

    public boolean hasSubtask() {
        return tasks.size() > 0;
    }

    public void addTask(Task tasks) {
        this.tasks.add(tasks);
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
