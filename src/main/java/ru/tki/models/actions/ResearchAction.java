package ru.tki.models.actions;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;

public class ResearchAction extends Action {

    public ResearchAction(AbstractPlanet planet) {
        super(planet);
        name = "Research";
    }

    @Override
    public void complete(Empire empire) {
        empire.setResearchInProgress(false);
    }
}
