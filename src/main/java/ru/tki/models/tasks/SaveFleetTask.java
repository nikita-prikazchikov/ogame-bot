package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.types.MissionType;

public class SaveFleetTask extends FleetTask {

    public SaveFleetTask(Empire empire, AbstractPlanet planet, FleetAction action) {
        super(empire, planet);
        name = "Save fleet on planet: " + planet.getCoordinates();
        if(empire.getPlanets().size() == 1){
            //In case we are alone save fleet through attack to attacker
            setTargetPlanet(action.getPlanet());
            setMissionType(MissionType.ATTACK);
        } else {
            setTargetPlanet(empire.getPlanets().stream().filter(p -> !p.equals(planet)).reduce(planet::closer).get());
        }
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
