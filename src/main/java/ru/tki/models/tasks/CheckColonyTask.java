package ru.tki.models.tasks;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.models.types.UpdateTaskType;
import ru.tki.po.OverviewPage;

//Verify colony is good size
public class CheckColonyTask extends Task {

    transient Empire empire;
    private Integer size;

    public CheckColonyTask(Empire empire, AbstractPlanet planet, Integer size) {
        name = "Colony check task";
        this.size = size;
        this.empire = empire;
        setPlanet(planet, false);
    }

    @Override
    public Action execute() {
        Navigation navigation = new Navigation();
        navigation.openOverview();
        navigation.selectPlanet(getPlanet());

        OverviewPage overviewPage = new OverviewPage();
        //TODO write code for colony remove. Keep all planets now
//        if(overviewPage.getPlanetSize() < size){
        if(overviewPage.getPlanetSize() < 0){
            //remove planet
            return null;
        }
        System.out.println(String.format("New planet was found and added to the empire: %s", getPlanet().getCoordinates().getFormattedCoordinates()));
        empire.addPlanet(getPlanet());
        empire.addTask(new UpdateInfoTask(empire, getPlanet(), UpdateTaskType.All));
        return null;
    }

    @Override
    public String toString() {
            return String.format("Verify new colony %s has correct size %s", getPlanet().getCoordinates().getFormattedCoordinates(), size);
    }
}
