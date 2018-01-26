package ru.tki.models.actions;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;

public class BuildingAction extends Action {

    public BuildingAction(AbstractPlanet planet) {
        super(planet);
        name = "Building";
    }

    @Override
    public void complete(Empire empire) {
        planet.setBuildInProgress(false);
    }
}
