package ru.tki.brain;

import ru.tki.ContextHolder;
import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Planet;
import ru.tki.models.actions.Action;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.tasks.*;
import ru.tki.models.types.MissionType;
import ru.tki.models.types.PlanetType;
import ru.tki.models.types.UpdateTaskType;
import ru.tki.po.LoginPage;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Mainframe {
    private static final Duration checkAttackDuration          = Duration.ofMinutes(2);
    private static final Duration checkUpdateResourcesDuration = Duration.ofMinutes(15);
    private static final Duration checkFlagsDuration           = Duration.ofMinutes(30);
    private static final Duration checkFleetDuration           = Duration.ofMinutes(60);
    private static final Duration saveFleetDuration            = Duration.ofMinutes(51);

    private Instant lastAttackCheck     = Instant.now();
    private Instant lastUpdateResources = Instant.now();
    private Instant lastFlagsCheck      = Instant.now();
    private Instant lastFleetCheck      = Instant.now();
    private Instant executionStopTime;

    private TaskGenerator taskGenerator;
    private Empire        empire;
    private Navigation    navigation;
    private LoginPage     loginPage;

    public Mainframe() {
        this.empire = new Empire();
        navigation = new Navigation();
        loginPage = new LoginPage();
        taskGenerator = new TaskGenerator(empire);
    }

    public void start() {
        System.out.println("Let's the magic start!");

        Duration duration = Duration.ZERO;
        duration = duration.plusHours(ContextHolder.getBotConfigMain().getExecutionHours());
        duration = duration.plusMinutes(ContextHolder.getBotConfigMain().getExecutionMinutes());
        if (duration.isZero()) {
            executionStopTime = null;
        } else {
            executionStopTime = Instant.now().plus(duration);
            System.out.println("Bot will be executed for the: " + duration.getSeconds() + " seconds");
        }

        if (!empire.load()) {
            empire.addTask(new EmpireTask(empire));
            lastUpdateResources = Instant.now();
        } else {
            for (AbstractPlanet planet : empire.getPlanets()) {
                //Loaded empire can't has tasks
                planet.setHasTask(false);
            }
        }
        empire.addTask(new CheckAttackTask(empire));
        empire.addTask(new CheckExistingActionsTask(empire));
        runExecution();
    }

    // Main bot executor. Infinite loop for operations in the system
    private void runExecution() {
        int exceptionCount = 0;
        do {
            try {
                if (!loginPage.isLoggedIn()) {
                    navigation.openHomePage();
                    loginPage.login();
                }
                if (exceptionCount >= 5) {
                    restartEmpire();
                    exceptionCount = 0;
                }
                if (null != executionStopTime && executionStopTime.compareTo(Instant.now()) < 0) {
                    System.out.println("Bot execution time is over. Buy! Till the next time!");
                    return;
                }
                verifyActions();
                execute();
                verifySaveEmpireFromAttack();
                verifySchedules();
                think();
                //reset fails count in case everything was executed
                exceptionCount = 0;
            } catch (Exception ex) {
                exceptionCount++;
                System.out.println("Exception count: " + exceptionCount);
                ex.printStackTrace();
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        while (true);
    }

    private void restartEmpire() {
        System.out.println("");
        System.out.println("=========================================================================================");
        System.out.println("                          Restart current empire details                                 ");
        System.out.println("=========================================================================================");
        this.empire = new Empire();
        empire.addTask(new EmpireTask(empire));
        empire.addTask(new CheckExistingActionsTask(empire));
    }

    // Add tasks that are periodic in the system
    // Like: check attack or update existing resources
    private void verifySchedules() {
        if (lastAttackCheck.plus(checkAttackDuration).compareTo(Instant.now()) < 0) {
            empire.addTask(new CheckAttackTask(empire));
            lastAttackCheck = Instant.now();
        }

        if (lastUpdateResources.plus(checkUpdateResourcesDuration).compareTo(Instant.now()) < 0) {
            empire.addTask(new UpdateResourcesTask(empire));
            lastUpdateResources = Instant.now();
        }

        if (lastFlagsCheck.plus(checkFlagsDuration).compareTo(Instant.now()) < 0) {
            empire.addTask(new CheckExistingFlagsTask(empire));
            lastFlagsCheck = Instant.now();
        }

        if (lastFleetCheck.plus(checkFleetDuration).compareTo(Instant.now()) < 0) {
            empire.addTask(new CheckFleetsCountTask(empire));
            lastFleetCheck = Instant.now();
        }
    }

    private void verifySaveEmpireFromAttack() {
        if (!empire.isUnderAttack()) {
            return;
        }
        if (empire.getEnemyFleets().stream().filter(fleetAction ->
                fleetAction.getMissionType() == MissionType.ATTACK
                        || fleetAction.getMissionType() == MissionType.JOINT_ATTACK
        ).count() > 0) {
            empire.getPlanets().forEach(planet ->
                    empire.getEnemyFleets().stream().filter(fleetAction ->
                            planet.equals(fleetAction.getTargetPlanet())).forEach(fleetAction -> {
                        if (!planet.getFleet().isEmpty() && fleetAction.getFinishTime().minus(saveFleetDuration).compareTo(Instant.now()) < 0) {
                            empire.addTask(new SaveFleetTask(empire, planet));
                        }
                    }));
        }
    }

    private void verifyActions() throws InterruptedException {
        List<Action> actions = new ArrayList<>();
        empire.getActions().stream().filter(action1 -> !(action1 instanceof FleetAction)).filter(Action::isFinished).forEach(action -> {
            action.complete(empire);
            empire.addTasks(action.getTasks());
            actions.add(action);
        });
        empire.getActions().stream().filter(action1 -> action1 instanceof FleetAction).map(a -> (FleetAction) a).forEach(action -> {
            if (action.isFinished()) {
                action.complete(empire);
                empire.addTask(new UpdateInfoTask(empire, action.getPlanet(), UpdateTaskType.FLEET));
                actions.add(action);
            } else if (action.isTargetAchieved()) {
                System.out.printf("Fleet come to the target planet: %s : %s", action.getTargetPlanet().getCoordinates(), action.getFleet().getDetails());
                action.setTargetAchieved();
                if (action.hasTask()) {
                    empire.addTasks(action.getTasks());
                    action.setTasks(null);
                }
                if (empire.isMyPlanet(action.getTargetPlanet())) {
                    empire.addTask(new UpdateInfoTask(empire, action.getTargetPlanet(), UpdateTaskType.FLEET));
                }
            }
            if (!empire.isUnderAttack() && action.isSaveFlight()) {
                // TODO: 29.01.2018 Think about revert in case of multiple attacks
                empire.addTask(new RevertFleetTask(empire, action));
                actions.add(action);
            }
        });
        actions.forEach(action -> empire.removeAction(action));
    }

    //Get new task for build resource building or factory
    private void think() {
        //minimize main planets count first
        empire.addTask(taskGenerator.checkMainPlanetsCount());
        empire.addTask(taskGenerator.getColonyTask());

        //Find appropriate task on planets thyself
        for (AbstractPlanet planet : empire.getPlanets()) {
            //avoid 2 tasks on 1 planet
            if (planet.hasTask()) {
                continue;
            }
            if (planet.isPlanet()) {
                //build or research something first
                empire.addTask(taskGenerator.getTask((Planet) planet));
            } else if (planet.getType() == PlanetType.MOON) {
                //Do nothing now
            }
        }

        //Find possible task with adding resources by transport them
        empire.addTask(taskGenerator.checkTransportForBuild());

        empire.getPlanets().stream().filter(planet -> !planet.hasTask() && planet.isPlanet() && !empire.isPlanetMain(planet)).forEach(p -> {
            //Move resources to main planet if possible
            empire.addTask(taskGenerator.getFleetTask((Planet) p));
        });

        //empire.addTask(taskGenerator.getColonyTask());
        empire.addTask(taskGenerator.sendExpedition());
    }

    //Execute existing tasks
    private void execute() throws InterruptedException {
        if (empire.getTasks().isEmpty()) {
            System.out.print('.');
            Thread.sleep(10000);
        }

        List<Task> tasksForRemove = new ArrayList<>();
        empire.getTasks().stream().filter(Task::canExecute).forEach(task -> {
            if (!task.isExecuted()) {
                System.out.printf("%s: Execute task: %s%n", Instant.now(), task.toString());
                Action action = task.execute();
                if (null != action) {
                    action.addTasks(task.getTasks());
                    empire.addAction(action);
                }
                task.setExecuted(true);
            }
            tasksForRemove.add(task);
        });
        for (Task task : tasksForRemove) {
            empire.removeTask(task);
        }
    }
}
