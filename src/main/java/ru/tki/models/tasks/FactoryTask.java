package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.types.FactoryType;
import ru.tki.models.actions.FactoryAction;
import ru.tki.po.BasePage;
import ru.tki.po.FactoriesPage;
import ru.tki.po.components.BuildDetailComponent;

public class FactoryTask extends Task {

    FactoryType type;

    public FactoryTask(AbstractPlanet planet, FactoryType type) {
        this.planet = planet;
        this.type = type;
    }

    public FactoryType getType() {
        return type;
    }

    public void setType(FactoryType type) {
        this.type = type;
    }

    @Override
    public FactoryAction execute() {
        FactoryAction action = new FactoryAction(planet);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(planet);
        basePage.leftMenu.openFactory();

        FactoriesPage factoriesPage = new FactoriesPage();
        factoriesPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.setDuration(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        return action;
    }
}
