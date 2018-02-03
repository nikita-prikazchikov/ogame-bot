package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.ShipyardAction;
import ru.tki.po.TraderPage;
import ru.tki.po.components.ResourcesComponent;

//Get daily bonus in import/export
public class GetDailyBonusTask extends Task {

    transient Empire empire;

    public GetDailyBonusTask(Empire empire, AbstractPlanet planet) {
        name = "Get daily bonus in import/export task";
        this.empire = empire;
        setPlanet(planet);
    }

    @Override
    public ShipyardAction execute() {
        super.execute();

        Navigation navigation = new Navigation();
        TraderPage traderPage = new TraderPage();
        ResourcesComponent resourcesComponent = new ResourcesComponent();

        navigation.selectPlanet(getPlanet());
        navigation.leftMenu.openTraderOverview();
        traderPage.selectImportExport();

        if (traderPage.getCost() < getPlanet().getResources().getMetal()) {
            traderPage.payWithMetal();
            getPlanet().setResources(resourcesComponent.getResources());
        } else {
            System.out.println(String.format("Planet %s does not have enough metal to pay daily bonus", getPlanet().getCoordinates()));
        }

        return null;
    }

    @Override
    public String toString() {
        return "Get daily bonus in import/export";
    }
}
