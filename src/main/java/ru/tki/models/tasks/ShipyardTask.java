package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.ShipyardAction;
import ru.tki.models.types.ShipType;
import ru.tki.models.types.UpdateTaskType;
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
        name = "Shipyard task";
        this.empire = empire;
        this.amount = amount;
        setPlanet(planet);
        this.type = type;
        getPlanet().setShipyardBusy(true);
    }

    @Override
    public void removeFromQueue() {
        super.removeFromQueue();
        getPlanet().setShipyardBusy(false);
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
        ShipyardAction action = new ShipyardAction(getPlanet(), type);

        BasePage basePage = new BasePage();
        Navigation navigation = new Navigation();
        basePage.myWorlds.selectPlanet(getPlanet());
        navigation.openShipyard();

        ShipyardPage shipyardPage = new ShipyardPage();
        shipyardPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        Duration duration = buildDetailComponent.getDuration();
        action.addDuration(duration.multipliedBy(amount));
        buildDetailComponent.setAmount(amount);
        buildDetailComponent.build();

        getPlanet().setResources(basePage.resources.getResources());
        action.addTask(new UpdateInfoTask(empire, getPlanet(), UpdateTaskType.FLEET));

        return action;
    }

    @Override
    public String toString() {
        if (getPlanet().isPlanet()) {
            return String.format("Build %d %s on planet %s", amount, type, getPlanet().getCoordinates());
        } else {
            return "Build some ships";
        }
    }
}
