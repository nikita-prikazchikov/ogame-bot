package ru.tki.models.actions;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.types.FactoryType;

public class FactoryAction extends Action {

    FactoryType factoryType;

    public FactoryAction(AbstractPlanet planet, FactoryType factoryType) {
        super(planet);
        this.factoryType = factoryType;
        name = "Factory";
    }

    @Override
    public void complete(Empire empire) {
        planet.setBuildInProgress(false);
        if(factoryType == FactoryType.SHIPYARD){
            planet.setShipyardBusy(false);
        }
        if(factoryType == FactoryType.RESEARCH_LAB){
            empire.setResearchInProgress(false);
        }
    }
}
