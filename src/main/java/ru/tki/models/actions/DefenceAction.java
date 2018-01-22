package ru.tki.models.actions;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;

public class DefenceAction extends Action {

    public DefenceAction(AbstractPlanet planet) {
        super(planet);
    }

    @Override
    public void complete(Empire empire) {
        planet.setShipyardBusy(false);
    }
}
