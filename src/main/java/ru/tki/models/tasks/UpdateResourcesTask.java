package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.po.BasePage;

public class UpdateResourcesTask extends Task {

    Empire empire;

    public UpdateResourcesTask(Empire empire) {
        this.empire = empire;
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        navigation.openResources();
        BasePage basePage = new BasePage();
        for (AbstractPlanet planet : empire.getPlanets()){
            navigation.selectPlanet(planet);
            planet.setResources(basePage.resources.getResources());
        }
        return null;
    }

    @Override
    public String toString() {
        return "Update current resources in the empire";
    }
}