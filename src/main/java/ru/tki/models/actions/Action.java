package ru.tki.models.actions;

import com.google.gson.Gson;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.tasks.Task;

import java.time.Duration;
import java.time.Instant;

public abstract class Action {

    protected AbstractPlanet planet;
    protected Duration       duration;
    protected Instant        finishTime;
    protected Task           subtask;
    protected String         name;

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
        this.finishTime = finishTime.plus(duration).plus(Duration.ofSeconds(15));
    }

    public Task getSubtask() {
        return subtask;
    }

    public void setSubtask(Task subtask) {
        this.subtask = subtask;
    }

    public boolean hasSubtask() {
        return null != subtask;
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
