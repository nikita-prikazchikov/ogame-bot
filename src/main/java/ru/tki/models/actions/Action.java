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
    protected Instant        startDate;
    protected Task           subtask;

    public Action() {
        startDate = Instant.now();
    }

    public Action(AbstractPlanet planet) {
        this();
        this.planet = planet;
    }

    abstract public void complete(Empire empire);

    public Boolean isFinished() {
        if (null != duration) {
            return Instant.now().compareTo(startDate.plus(duration)) > 0;
        } else {
            return Instant.now().compareTo(startDate) > 0;
        }
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
        this.duration = duration.plus(Duration.ofSeconds(10));
    }

    public Instant getStartDate() {
        return startDate;
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

    public String toLog(){
        return String.format("%s on planet %s will be executed for %s seconds",
                this.getClass().getCanonicalName(), planet.getCoordinates().getFormattedCoordinates(), duration.getSeconds());
    }
}
