package ru.tki.models;

import com.google.gson.Gson;

import java.time.Instant;

public class GalaxySector {

    private Coordinates start;
    private Coordinates end;
    private Instant     lastUpdated;

    public GalaxySector(Coordinates start, Coordinates end) {
        this.start = start;
        this.end = end;
    }

    public Coordinates getStart() {
        return start;
    }

    public void setStart(Coordinates start) {
        this.start = start;
    }

    public Coordinates getEnd() {
        return end;
    }

    public void setEnd(Coordinates end) {
        this.end = end;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setUpdatedNow() {
        setLastUpdated(Instant.now());
    }

    public boolean planetInSector(AbstractPlanet planet) {
        Coordinates coordinates = planet.getCoordinates();
        return start.getGalaxy().equals(coordinates.getGalaxy()) && start.getSystem() <= coordinates.getSystem() && end.getSystem() >= coordinates.getSystem();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GalaxySector sector = (GalaxySector) o;

        return start.equals(sector.start) && end.equals(sector.end);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }
}
