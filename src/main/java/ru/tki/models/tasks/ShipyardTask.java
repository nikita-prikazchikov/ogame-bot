package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Fleet;
import ru.tki.models.actions.ShipyardAction;
import ru.tki.models.types.ShipType;
import ru.tki.po.BasePage;
import ru.tki.po.ShipyardPage;
import ru.tki.po.components.BuildDetailComponent;

import java.time.Duration;

//Build new ships on the planet
public class ShipyardTask extends Task {

    ShipType type;
    Integer  amount;
    transient Empire empire;

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
        ShipyardAction action = new ShipyardAction(getPlanet());

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(getPlanet());
        basePage.leftMenu.openShipyard();

        ShipyardPage shipyardPage = new ShipyardPage();
        Fleet fleet = shipyardPage.getFleet();
        shipyardPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        Duration duration = buildDetailComponent.getDuration();
        action.addDuration(duration.multipliedBy(amount));
        buildDetailComponent.setAmount(amount);
        buildDetailComponent.build();

        getPlanet().setResources(basePage.resources.getResources());
        getPlanet().setShipyardBusy(true);
        fleet.set(type, fleet.get(type) + 1);
        getPlanet().setFleet(fleet);
        empire.savePlanet(getPlanet());

        return action;
    }

    @Override
    public String toString() {
        if (getPlanet().isPlanet()) {
            return String.format("Build %d %s on planet %s", amount, type, getPlanet().getCoordinates().getFormattedCoordinates());
        } else {
            return "Build some ships";
        }
    }
}
