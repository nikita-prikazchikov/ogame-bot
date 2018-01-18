package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.BuildingType;
import ru.tki.models.actions.ResourceAction;
import ru.tki.po.BasePage;
import ru.tki.po.ResourcesPage;
import ru.tki.po.components.BuildDetailComponent;

public class ResourceTask extends Task {

    BuildingType type;

    public ResourceTask(AbstractPlanet planet, BuildingType type) {
        this.planet = planet;
        this.type = type;
    }

    public BuildingType getType() {
        return type;
    }

    public void setType(BuildingType type) {
        this.type = type;
    }

    @Override
    public ResourceAction execute() {
        ResourceAction action = new ResourceAction(planet);

        BasePage basePage = new BasePage();
        basePage.myWorlds.selectPlanet(planet);
        basePage.leftMenu.openResources();

        ResourcesPage resourcesPage = new ResourcesPage();
        resourcesPage.select(type);

        BuildDetailComponent buildDetailComponent = new BuildDetailComponent();
        action.setEndDate(buildDetailComponent.getDuration());
        buildDetailComponent.build();

        return action;
    }
}
