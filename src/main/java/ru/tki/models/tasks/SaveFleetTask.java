package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.FleetAction;

public class SaveFleetTask extends FleetTask {

    public SaveFleetTask(Empire empire, AbstractPlanet planet) {
        super(empire, planet);
        name = "Save fleet on planet: " + planet.getCoordinates();
        setTargetPlanet(empire.getPlanets().stream().filter(p -> !p.equals(planet)).reduce(planet::closer).get());
        setRandomSaveSpeed();
        setFleet(planet.getFleet());
        setResources(planet.getResources());
    }

    @Override
    public FleetAction execute() {
        FleetAction action = super.execute();
        action.setSaveFlight(true);
        return action;
    }

    @Override
    public String toString() {
        return String.format("Save fleet %s from planet %s to %s with %s",
                fleet.getDetails(), getPlanet().getCoordinates(), targetPlanet.getCoordinates(),
                resources == null ? "empty" : resources);
    }
}
