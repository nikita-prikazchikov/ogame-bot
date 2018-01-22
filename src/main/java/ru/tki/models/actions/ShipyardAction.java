package ru.tki.models.actions;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;

public class ShipyardAction extends Action {

    public ShipyardAction(AbstractPlanet planet) {
        super(planet);
    }

    @Override
    public void complete(Empire empire) {
        planet.setShipyardBusy(false);
    }
}
