package ru.tki.models.actions;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;

public class FactoryAction extends Action {

    public FactoryAction(AbstractPlanet planet) {
        super(planet);
    }

    @Override
    public void complete(Empire empire) {

    }
}
