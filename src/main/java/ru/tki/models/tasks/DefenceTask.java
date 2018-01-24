package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.actions.DefenceAction;
import ru.tki.models.types.DefenceType;
import ru.tki.po.BasePage;
import ru.tki.po.DefencePage;
import ru.tki.po.components.BuildDetailComponent;

import java.time.Duration;

//Build new defence on the planet
public class DefenceTask extends Task {

    DefenceType type;
    Integer amount;

    public DefenceTask(AbstractPlanet planet, DefenceType type, Integer amount) {
        this.amount = amount;
        setPlanet(planet);
        this.type = type;
    }

    public DefenceType getType() {
        return type;
    }

    public void setType(DefenceType type) {
        this.type = type;
    }

    @Override
    public DefenceAction execute() {
        super.execute();
        DefenceAction action = new DefenceAction(planet);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(planet);
        basePage.leftMenu.openDefense();

        DefencePage defencePage = new DefencePage();
        defencePage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        Duration duration = buildDetailComponent.getDuration();
        action.setDuration(duration.multipliedBy(amount));
        buildDetailComponent.setAmount(amount);
        buildDetailComponent.build();

        planet.setResources(basePage.resources.getResources());
        planet.setShipyardBusy(true);

        return action;
    }

    @Override
    public String toString() {
        return String.format("Build %d %s on planet %s", amount, type, planet.getCoordinates().getFormattedCoordinates());
    }
}
