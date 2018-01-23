package ru.tki.brain;

import ru.tki.executor.Navigation;
import ru.tki.models.AbstractPlanet;
import ru.tki.models.Empire;
import ru.tki.models.Planet;
import ru.tki.models.actions.Action;
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

        if (!empire.load()) {
            empire.addTask(new EmpireTask(empire));
            lastUpdateResources = Instant.now();
        }
        empire.addTask(new CheckExistingActionsTask(empire));
        runExecution();
    }

    private void runExecution() {
        int exceptionCount = 0;
        do {
            try {
                if(!loginPage.isLoggedIn()){
                    navigation.openHomePage();
                    loginPage.login();
                }
                if(exceptionCount >= 20){
                    restartEmpire();
                    exceptionCount = 0;
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
        empire.getActions().stream().filter(Action::isFinished).forEach(action ->{
            action.complete(empire);
            if(action.hasSubtask()){
                empire.addTask(action.getSubtask());
            }
            actions.add(action);
        });
        actions.forEach(action -> empire.removeAction(action));
        if(!empire.getTasks().isEmpty()){
            execute();
        }
    }

    private void thinkBuildings() {
        Task task;
        for (AbstractPlanet planet : empire.getPlanets()) {
            if (planet.getType() == PlanetType.PLANET) {
                task = taskGenerator.getTask((Planet) planet);
                if (null != task) {
                    empire.addTask(task);
                }
            } else if (planet.getType() == PlanetType.MOON) {
                //Do nothing now
            }
        }
    }

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
