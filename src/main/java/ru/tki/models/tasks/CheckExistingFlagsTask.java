package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;

//Validate existing flags on planets and empire to avoid deadlocks for buildings
public class CheckExistingFlagsTask extends Task {

    transient Empire     empire;
    private   Navigation navigation;

    public CheckExistingFlagsTask(Empire empire) {
        name = "Verify all flags on planets are valid";
        this.empire = empire;
    }

    @Override
    public Action execute() {
        navigation = new Navigation();
        // TODO: 28.01.2018 Implement this
        return null;
    }

    @Override
    public String toString() {
        return String.format("Verify all flags are validly set on planets and empire");
    }
}
