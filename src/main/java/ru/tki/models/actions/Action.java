package ru.tki.models.actions;

import com.google.gson.Gson;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.tasks.Task;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public abstract class Action {

    protected AbstractPlanet planet;
    protected Duration       duration;
    protected Instant        finishTime;
    protected String         name;

    protected List<Task> tasks = new ArrayList<>();

    public Action() {
        finishTime = Instant.now();
    }

    public Action(AbstractPlanet planet) {
        this();
        this.planet = planet;
    }

    abstract public void complete(Empire empire);

    public Boolean isFinished() {
        return Instant.now().compareTo(finishTime) > 0;
    }

    public AbstractPlanet getPlanet() {
        return planet;
    }

    public void setPlanet(AbstractPlanet planet) {
        this.planet = planet;
    }

    public void addDuration(Duration duration) {
        this.finishTime = finishTime.plus(duration).plus(Duration.ofSeconds(5));
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        if (null != tasks) {
            this.tasks = tasks;
        } else {
            this.tasks = new ArrayList<>();
        }
    }

    public void addTask(Task tasks) {
        if (tasks == null) {
            return;
        }
        this.tasks.add(tasks);
    }

    public void addTasks(List<Task> tasks) {
        tasks.forEach(this::addTask);
    }

    public boolean hasTask() {
        return tasks.size() > 0;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String toLog() {
        return String.format("%s on planet %s finish time: %s",
                this.getClass().getCanonicalName().replaceAll("\\w+\\.", ""), planet.getCoordinates().getFormattedCoordinates(), finishTime);
    }
}
