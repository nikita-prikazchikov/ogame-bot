package ru.tki.models.tasks;

import com.github.javafaker.Faker;
import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.models.actions.BuildingAction;
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

        AbstractPlanet planet = getPlanet();
        OverviewPage overviewPage = new OverviewPage();
        if(overviewPage.getPlanetSize() < size){
            System.out.println(String.format("New planet %s was found and it is too small. We have to delete it and find new one.", getPlanet().getCoordinates()));
            overviewPage.leavePlanet();
            return null;
        }

        Faker faker = new Faker();
        String name = faker.lordOfTheRings().location();
        overviewPage.renamePlanet(name);

        planet.setName(overviewPage.getPlanetName());
        planet.setSize(overviewPage.getPlanetSize());

        System.out.println(String.format("New planet was found and added to the empire: %s", getPlanet().getCoordinates()));
        empire.addPlanet(getPlanet());
        Action action = new BuildingAction(planet);
        action.addTask(new UpdateInfoTask(empire, getPlanet(), UpdateTaskType.All));
        return action;
    }

    @Override
    public String toString() {
            return String.format("Verify new colony %s has correct size %s", getPlanet().getCoordinates(), size);
    }
}
