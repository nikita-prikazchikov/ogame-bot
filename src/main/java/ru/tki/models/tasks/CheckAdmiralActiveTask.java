package ru.tki.models.tasks;

import ru.tki.ContextHolder;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.po.BasePage;
import ru.tki.po.components.BuildDetailComponent;
import ru.tki.po.components.ResourcesComponent;

//Verify if the empire is currently under attack
public class CheckAdmiralActiveTask extends Task {

    transient Empire empire;

    public CheckAdmiralActiveTask(Empire empire) {
        this.empire = empire;
        name = "Check admiral active";
    }

    @Override
    public Action execute() {
        BasePage basePage = new BasePage();
        ResourcesComponent resourcesComponent = new ResourcesComponent();
        if (basePage.isAdmiralActive()) {
            empire.setAdmiralActive(true);
        } else {
            if (ContextHolder.getBotConfigMain().HIRE_ADMIRAL && resourcesComponent.getDarkMatter() > 5000) {
                basePage.clickAdmiral();
                BuildDetailComponent component = new BuildDetailComponent();
                component.build();
            } else {
                empire.setAdmiralActive(false);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Check if the EMPIRE is under attack";
    }
}
