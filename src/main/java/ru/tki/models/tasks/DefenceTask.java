package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.DefenceType;
import ru.tki.models.actions.DefenceAction;
import ru.tki.po.BasePage;
import ru.tki.po.DefencePage;
import ru.tki.po.components.BuildDetailComponent;

import java.time.Duration;

public class DefenceTask extends Task {

    DefenceType type;
    Integer amount;

    public DefenceTask(AbstractPlanet planet, DefenceType type, Integer amount) {
        this.amount = amount;
        this.planet = planet;
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
        DefenceAction action = new DefenceAction(planet);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(planet);
        basePage.leftMenu.openDefense();

        DefencePage defencePage = new DefencePage();
        defencePage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        Duration duration = buildDetailComponent.getDuration();
        action.setEndDate(duration.multipliedBy(amount));
        buildDetailComponent.setAmount(amount);
        buildDetailComponent.build();

        return action;
    }
}
