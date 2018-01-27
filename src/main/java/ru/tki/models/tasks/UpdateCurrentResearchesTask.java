package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.po.ResearchesPage;

public class UpdateCurrentResearchesTask extends Task {

    transient  Empire empire;

    public UpdateCurrentResearchesTask(Empire empire) {
        this.empire = empire;
        name = "Update current empire researches";
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        navigation.selectPlanet(empire.selectMain());
        navigation.openResearch();

        ResearchesPage researchesPage = new ResearchesPage();
        empire.setResearches(researchesPage.getResearches());
        empire.saveResearches();
        return null;
    }

    @Override
    public String toString() {
        return "Check current empire researches";
    }
}
