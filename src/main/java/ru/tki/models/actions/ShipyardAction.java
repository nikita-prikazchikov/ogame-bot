package ru.tki.models.actions;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.types.ShipType;

public class ShipyardAction extends Action {

    private ShipType type;

    public ShipyardAction(AbstractPlanet planet) {
        super(planet);
        name = "Shipyard";
    }

    public ShipyardAction(AbstractPlanet planet, ShipType type){
        this(planet);
        this.type = type;
    }

    public ShipType getType() {
        return type;
    }

    @Override
    public void complete(Empire empire) {
        planet.setShipyardBusy(false);
    }
}
