package ru.tki.brain;

import ru.tki.ContextHolder;
import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Planet;
import ru.tki.models.actions.Action;
import ru.tki.models.actions.FleetAction;
import ru.tki.models.tasks.*;
import ru.tki.models.types.PlanetType;
import ru.tki.po.LoginPage;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Mainframe {
    private static final Duration checkAttackDuration          = Duration.ofSeconds(120);
    private static final Duration checkUpdateResourcesDuration = Duration.ofMinutes(15);

    private static Logger logger = Logger.getLogger(String.valueOf(Mainframe.class));

    private Empire empire;
    private Instant lastAttackCheck = Instant.now();
    private Instant lastUpdateResources = Instant.now();//.minus(Duration.ofMinutes(15));
    private Instant executionStopTime;//.minus(Duration.ofMinutes(15));

    TaskGenerator taskGenerator;

    Navigation navigation;
    LoginPage  loginPage;

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
        if(duration.isZero()){
            executionStopTime = null;
        }
        else{
            executionStopTime = Instant.now().plus(duration);
            System.out.println("Bot will be executed for the: " + duration.getSeconds() + " seconds");
        }

        if (!empire.load()) {
            empire.addTask(new EmpireTask(empire));
            lastUpdateResources = Instant.now();
        }
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
                if (exceptionCount >= 20) {
                    restartEmpire();
                    exceptionCount = 0;
                }
                if (null != executionStopTime && executionStopTime.compareTo(Instant.now()) < 0) {
                    System.out.println("Bot execution time is over. Buy! Till the next time!");
                    return;
                }
                execute();
                verifySchedules();
                verifyActions();
                thinkBuildings();
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

    private void restartEmpire(){
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
        if(lastAttackCheck.plus(checkAttackDuration).compareTo(Instant.now())<0){
            empire.addTask(new CheckAttackTask(empire));
            lastAttackCheck = Instant.now();
        }

        if(lastUpdateResources.plus(checkUpdateResourcesDuration).compareTo(Instant.now())<0){
            empire.addTask(new UpdateResourcesTask(empire));
            lastUpdateResources = Instant.now();
        }
    }

    private void verifyActions() throws InterruptedException {
        List<Action> actions = new ArrayList<>();
        empire.getActions().stream().filter(action1 -> !(action1 instanceof FleetAction)).filter(Action::isFinished).forEach(action ->{
            action.complete(empire);
            if(action.hasSubtask()){
                empire.addTask(action.getSubtask());
            }
            actions.add(action);
        });
        empire.getActions().stream().filter(action1 -> action1 instanceof FleetAction).forEach(action ->{
            if(action.isFinished())
            {action.complete(empire);
                actions.add(action);}
            else if(true){
                if(action.hasSubtask()){
                    empire.addTask(action.getSubtask());
                    //TODO update fleet actions processing
                }
            }

        });

        actions.forEach(action -> empire.removeAction(action));
        if(!empire.getTasks().isEmpty()){
            execute();
        }
    }

    //Get new task for build resource building or factory
    private void thinkBuildings() {
        //minimize main planets count first
        empire.addTask(taskGenerator.checkMainPlanetsCount());

        //Find appropriate task on planets thyself
        for (AbstractPlanet planet : empire.getPlanets()) {
            //avoid 2 tasks on 1 planet
            if (planet.hasTask()) {
                continue;
            }
            if (planet.isPlanet()) {
                empire.addTask(taskGenerator.getTask((Planet) planet));
                if (planet.hasTask()) continue;
                empire.addTask(taskGenerator.getFleetTask((Planet) planet));
            } else if (planet.getType() == PlanetType.MOON) {
                //Do nothing now
            }
        }

        //Find possible task with adding resources by transport them

    }

    //Execute existing tasks
    private void execute() throws InterruptedException {
        if (empire.getTasks().isEmpty()) {
            System.out.print('.');
            Thread.sleep(10000);
        }
        List<Task> tasksForRemove = new ArrayList<>();
        empire.getTasks().stream().filter(Task::canExecute).forEach(task -> {
            System.out.println("Execute task: " + task.toString());
            Action action = task.execute();
            if (null != action) {
                if (task.hasSubtask()){
                    action.setSubtask(task.getSubtask());
                }
                empire.addAction(action);
            }
            tasksForRemove.add(task);
        });
        for (Task task : tasksForRemove) {
            empire.removeTask(task);
        }
    }
}
