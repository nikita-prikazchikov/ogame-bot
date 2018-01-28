package ru.tki.models.tasks;

import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.actions.*;
import ru.tki.models.types.FactoryType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

//Validate existing flags on planets and empire to avoid deadlocks for buildings
public class CheckExistingFlagsTask extends Task {

    transient Empire     empire;

    public CheckExistingFlagsTask(Empire empire) {
        name = "Verify all flags on planets are valid";
        this.empire = empire;
    }

    @Override
    public Action execute() {
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(empire.getTasks());

        empire.getTasks().stream().filter(Task::hasSubtask).forEach(task -> {
            tasks.addAll(task.getTasks());
        });
        empire.getActions().stream().filter(Action::hasTask).forEach(action -> {
            tasks.addAll(action.getTasks());
        });

        //Check if there actions or tasks blocking research
        if (empire.isResearchInProgress()) {
            if (tasks.stream().filter(task -> task instanceof ResearchTask).count() == 0
                    && empire.getActions().stream().filter(action -> action instanceof ResearchAction).count() == 0
                    && tasks.stream().filter(task1 ->
                    task1 instanceof FactoryTask
                            && ((FactoryTask) task1).getType() == FactoryType.RESEARCH_LAB).count() == 0) {
                System.out.println(Instant.now() + " Current researches are blocked for no reason. Set flag to false");
                empire.setResearchInProgress(false);
            }
        }

        empire.getPlanets().stream().filter(AbstractPlanet::isPlanet).forEach(planet -> {
            if (planet.hasTask()) {
                if (tasks.stream().filter(task ->
                        task.getPlanet() != null && task.getPlanet().equals(planet)
                ).count() == 0) {
                    System.out.printf("%s I don't find any task assigned to planet %s. Set flag HAS_TASK to false%n", Instant.now(), planet.getCoordinates().getFormattedCoordinates());
                    planet.setHasTask(false);
                }
            }

            if (planet.getBuildInProgress()) {
                if (tasks.stream().filter(task ->
                        task.getPlanet() != null && task.getPlanet().equals(planet)
                                && (task instanceof BuildingTask || task instanceof FactoryTask)
                ).count() == 0
                        && empire.getActions().stream().filter(action ->
                        action.getPlanet() != null && action.getPlanet().equals(planet) &&
                                (action instanceof BuildingAction || action instanceof FactoryAction)
                ).count() == 0
                        ) {
                    System.out.printf("%s I don't find any action or task assigned to planet %s. Set flag BUILD_IN_PROGRESS to false%n", Instant.now(), planet.getCoordinates().getFormattedCoordinates());
                    planet.setBuildInProgress(false);
                }
            }

            if (planet.getShipyardBusy()) {
                if (tasks.stream().filter(task ->
                        task.getPlanet() != null && task.getPlanet().equals(planet) &&
                                (task instanceof DefenceTask
                                        || task instanceof ShipyardTask
                                        ||
                                        (task instanceof FactoryTask &&
                                                (((FactoryTask) task).getType() == FactoryType.SHIPYARD ||
                                                        ((FactoryTask) task).getType() == FactoryType.NANITE_FACTORY)
                                        )
                                )
                ).count() == 0
                        && empire.getActions().stream().filter(action ->
                        action.getPlanet() != null && action.getPlanet().equals(planet) &&
                                (action instanceof DefenceAction
                                        || action instanceof ShipyardAction
                                        ||
                                        (action instanceof FactoryAction &&
                                                (((FactoryAction) action).getFactoryType() == FactoryType.SHIPYARD ||
                                                        ((FactoryAction) action).getFactoryType() == FactoryType.NANITE_FACTORY)
                                        )
                                )
                ).count() == 0) {
                    System.out.printf("%s I don't find any action or task assigned to planet %s. Set flag SHIPYARD_BUSY to false%n", Instant.now(), planet.getCoordinates().getFormattedCoordinates());
                    planet.setShipyardBusy(false);
                }
            }
        });

        return null;
    }

    @Override
    public String toString() {
        return "Verify all flags are validly set on planets and empire";
    }
}
