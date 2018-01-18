package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.ShipType;
import ru.tki.models.StationType;
import ru.tki.models.actions.ShipyardAction;
import ru.tki.models.actions.StationAction;
import ru.tki.po.BasePage;
import ru.tki.po.ShipyardPage;
import ru.tki.po.StationsPage;
import ru.tki.po.components.BuildDetailComponent;

import java.time.Duration;

public class ShipyardTask extends Task {

    ShipType type;
    Integer amount;

    public ShipyardTask(AbstractPlanet planet, ShipType type, Integer amount) {
        this.amount = amount;
        this.planet = planet;
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
        ShipyardAction action = new ShipyardAction(planet);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(planet);
        basePage.leftMenu.openShipyard();

        ShipyardPage shipyardPage = new ShipyardPage();
        shipyardPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        Duration duration = buildDetailComponent.getDuration();
        action.setEndDate(duration.multipliedBy(amount));
        buildDetailComponent.setAmount(amount);
        buildDetailComponent.build();

        return action;
    }
}
