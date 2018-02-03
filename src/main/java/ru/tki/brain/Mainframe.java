package ru.tki.brain;

import ru.tki.BotConfigMain;
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
    private static Duration checkAttackDuration;
    private static Duration checkUpdateResourcesDuration;
    private static Duration saveFleetDuration;
    private static Duration checkFlagsDuration;
    private static Duration checkFleetDuration;
    private static Duration checkDailyBonusDuration;

    private Instant lastAttackCheck     = Instant.now();
    private Instant lastUpdateResources = Instant.now();
    private Instant lastFlagsCheck      = Instant.now();
    private Instant lastFleetCheck      = Instant.now();
    private Instant lastDailyBonusCheck = Instant.now();
    private Instant executionStopTime;

    private TaskGenerator taskGenerator;
    private Empire        empire;

    private Navigation navigation;
    private LoginPage  loginPage;

    private BotConfigMain config;

    public Mainframe(BotConfigMain config) {
        this.config = config;

        this.empire = new Empire();
        navigation = new Navigation();
        loginPage = new LoginPage();
        taskGenerator = new TaskGenerator(empire, config);
    }

    public void start() {
        System.out.println("Let's the magic start!");

        setTimeouts();

        checkBotExecutionDuration();
        loadEmpire();
        setEmpireInitialTasks();

        runExecution();
    }

    private void setTimeouts() {
        checkAttackDuration = Duration.ofSeconds(config.ATTACK_CHECK_TIMEOUT);
        checkUpdateResourcesDuration = Duration.ofSeconds(config.UPDATE_RESOURCES_TIMEOUT);
        saveFleetDuration = Duration.ofSeconds(config.FLEET_SAVE_TIMEOUT);

        //Following flags are for internal use and should not be configurable
        checkFlagsDuration = Duration.ofMinutes(30);
        checkFleetDuration = Duration.ofMinutes(58);
        checkDailyBonusDuration = Duration.ofMinutes(123);
    }

    private void loadEmpire() {
        if (!empire.load()) {
            empire.addTask(new EmpireTask(empire));
            lastUpdateResources = Instant.now();
        } else {
            for (AbstractPlanet planet : empire.getPlanets()) {
                //Loaded empire can't has tasks
                planet.setHasTask(false);
            }
        }
    }

    //Setup initial tasks in the Empire
    private void setEmpireInitialTasks() {
        empire.addTask(new CheckAttackTask(empire));
        empire.addTask(new CheckExistingActionsTask(empire));
        empire.addTask(taskGenerator.getTaskImportExport());
    }

    //Verify if bot should work only limited time
    private void checkBotExecutionDuration() {
        Duration duration = Duration.ZERO;
        duration = duration.plusHours(config.EXECUTION_HOURS);
        duration = duration.plusMinutes(config.EXECUTION_MINUTES);
        if (duration.isZero()) {
            executionStopTime = null;
        } else {
            executionStopTime = Instant.now().plus(duration);
            System.out.println("Bot will be executed for the: " + duration);
        }
    }

    // Main bot executor. Infinite loop for operations in the system
    private void runExecution() {
        int exceptionCount = 0;
        do {
            try {
                checkLogin();
                if (exceptionCount >= 5) {
                    restartEmpire();
                    exceptionCount = 0;
                }
                if (null != executionStopTime && executionStopTime.compareTo(Instant.now()) < 0) {
                    System.out.println("Bot execution time is over. Buy! Till the next time!");
                    return;
                }
                execution();
                //reset fails count in case everything was executed
                exceptionCount = 0;
            } catch (Exception ex) {
                exceptionCount++;
                System.out.println("Exception count: " + exceptionCount);
                ex.printStackTrace();
                try {
                    Thread.sleep(config.SLEEP_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        while (true);
    }

    private void execution() throws InterruptedException {
        verifyActions();
        execute();
        verifySaveEmpireFromAttack();
        verifySchedules();
        think();
    }

    private void checkLogin() {
        if (!loginPage.isLoggedIn()) {
            navigation.openHomePage();
            loginPage.login();
        }
    }

    private void restartEmpire() {
        System.out.println("");
        System.out.println("=========================================================================================");
        System.out.println("                          Restart current empire details                                 ");
        System.out.println("=========================================================================================");
        this.empire = new Empire();
        empire.addTask(new EmpireTask(empire));
        taskGenerator.setEmpire(empire);
        setEmpireInitialTasks();
    }

    // Add tasks that are periodic in the system
    // Like: check attack or update existing resources
    private void verifySchedules() {
        Instant now = Instant.now();
        if (config.DO_CHECK_ATTACK && lastAttackCheck.plus(checkAttackDuration).compareTo(now) < 0) {
            empire.addTask(new CheckAttackTask(empire));
            lastAttackCheck = Instant.now();
        }

        if (lastUpdateResources.plus(checkUpdateResourcesDuration).compareTo(now) < 0) {
            empire.addTask(new UpdateResourcesTask(empire));
            lastUpdateResources = Instant.now();
        }

        if (lastFlagsCheck.plus(checkFlagsDuration).compareTo(now) < 0) {
            empire.addTask(new CheckExistingFlagsTask(empire));
            lastFlagsCheck = Instant.now();
        }

        if (lastFleetCheck.plus(checkFleetDuration).compareTo(now) < 0) {
            empire.addTask(new CheckFleetsCountTask(empire));
            lastFleetCheck = Instant.now();
        }

        if (lastDailyBonusCheck.plus(checkDailyBonusDuration).compareTo(now) < 0) {
            empire.addTask(taskGenerator.getTaskImportExport());
            lastDailyBonusCheck = Instant.now();
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
                System.out.println(String.format("Fleet come to the target planet: %s : %s ", action.getTargetPlanet().getCoordinates(), action.getFleet().getDetails()));
                action.setTargetAchieved();
                if (action.hasTask()) {
                    empire.addTasks(action.getTasks());
                    action.setTasks(null);
                }
                if (empire.isMyPlanet(action.getTargetPlanet())) {
                    empire.addTask(new UpdateInfoTask(empire, action.getTargetPlanet(), UpdateTaskType.FLEET));
                }
            }
            //Revert save flight in case planet is no longer under attack
            if (action.isSaveFlight() && !empire.isPlanetUnderAttack(action.getPlanet())) {
                empire.addTask(new RevertFleetTask(empire, action));
                actions.add(action);
            }
            //Revert KEEP flights in case target planet is under attack in range save fleet frame
            if (action.getMissionType() == MissionType.KEEP
                    && !action.isReturnFlight()
                    && empire.isAttackMeetsFleet(action)) {
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

        empire.addTask(taskGenerator.sendExpedition());

        empire.addTask(taskGenerator.getScanGalaxyTask());
    }

    //Execute existing tasks
    private void execute() throws InterruptedException {
        if (empire.getTasks().isEmpty()) {
            System.out.print('>');
            Thread.sleep(config.SLEEP_TIMEOUT);
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
