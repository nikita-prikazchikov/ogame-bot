package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.po.BasePage;

//Verify if the empire is currently under attack
public class CheckAttackTask extends Task {

    transient Empire empire;

    public CheckAttackTask(Empire empire) {
        this.empire = empire;
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        navigation.openOverview();
        BasePage basePage = new BasePage();
        if( basePage.isUnderAttack()){
            empire.setUnderAttack(true);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Check if the EMPIRE is under attack";
    }
}
