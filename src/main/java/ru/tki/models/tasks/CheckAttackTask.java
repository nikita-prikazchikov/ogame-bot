package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.po.BasePage;
import ru.tki.po.components.EventComponent;

//Verify if the empire is currently under attack
public class CheckAttackTask extends Task {

    transient Empire empire;

    public CheckAttackTask(Empire empire) {
        this.empire = empire;
        name = "Check empire under attack";
        setPriority(IMPORTANT);
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        //Open random
        Double r = Math.random();
        if (r < 0.166) navigation.leftMenu.openOverview();
        else if (r < 0.333) navigation.leftMenu.openResources();
        else if (r < 0.5) navigation.leftMenu.openDefense();
        else if (r < 0.666) navigation.leftMenu.openFleet();
        else if (r < 0.833) navigation.leftMenu.openResearch();
        else navigation.leftMenu.openFactory();

        BasePage basePage = new BasePage();
        if (basePage.isUnderAttack()) {
            empire.setUnderAttack(true);
            System.out.println("Empire is under attack!");
            EventComponent eventComponent = new EventComponent();
            //Refresh enemy attacks every scan
            empire.setEnemyFleets(null);
            empire.addEnemyFleets(eventComponent.getHostileFleets(empire));
        } else {
            empire.setUnderAttack(false);
            empire.setEnemyFleets(null);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Check if the EMPIRE is under attack";
    }
}
