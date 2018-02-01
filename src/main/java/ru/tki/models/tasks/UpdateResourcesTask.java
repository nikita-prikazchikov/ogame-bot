package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Resources;
import ru.tki.models.actions.Action;
import ru.tki.po.BasePage;

//Go to each planet and check current resources amount on it
public class UpdateResourcesTask extends Task {

    transient Empire empire;

    public UpdateResourcesTask(Empire empire) {
        this.empire = empire;
        name = "Update existing resources on the planets";
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        navigation.openResources();
        BasePage basePage = new BasePage();
        Resources r = new Resources();
        for (AbstractPlanet planet : empire.getPlanets()){
            navigation.selectPlanet(planet);
            planet.setResources(basePage.resources.getResources());
            planet.logResources();
            r = r.add(planet.getResources());
        }
        System.out.println("Total empire resources: " + r);
        return null;
    }

    @Override
    public String toString() {
        return "Check current resources in the empire";
    }
}
