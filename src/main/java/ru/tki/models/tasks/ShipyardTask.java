package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Fleet;
import ru.tki.models.actions.ShipyardAction;
import ru.tki.models.types.PlanetType;
import ru.tki.models.types.ShipType;
import ru.tki.po.BasePage;
import ru.tki.po.ShipyardPage;
import ru.tki.po.components.BuildDetailComponent;

import java.time.Duration;

public class ShipyardTask extends Task {

    ShipType type;
    Integer amount;
    Empire empire;

    public ShipyardTask(Empire empire, AbstractPlanet planet, ShipType type, Integer amount) {
        this.empire = empire;
        this.amount = amount;
        setPlanet(planet);
        this.type = type;
    }

    public ShipType getType() {
        return type;
    }

    public void setType(ShipType type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    @Override
    public ShipyardAction execute() {
        super.execute();
        ShipyardAction action = new ShipyardAction(planet);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(planet);
        basePage.leftMenu.openShipyard();

        ShipyardPage shipyardPage = new ShipyardPage();
        Fleet fleet = shipyardPage.getFleet();
        shipyardPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        Duration duration = buildDetailComponent.getDuration();
        action.setDuration(duration.multipliedBy(amount));
        buildDetailComponent.setAmount(amount);
        buildDetailComponent.build();

        planet.setResources(basePage.resources.getResources());
        planet.setShipyardBusy(true);
        fleet.set(type, fleet.get(type) + 1);
        planet.setFleet(fleet);
        empire.savePlanet(planet);

        return action;
    }

    @Override
    public String toString() {
        if(planet.getType() == PlanetType.PLANET) {
            return String.format("Build %d %s on planet %s", amount, type, planet.getCoordinates().getFormattedCoordinates());
        }
        else{
            return "Build some ships";
        }
    }
}
