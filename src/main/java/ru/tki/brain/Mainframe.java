package ru.tki.brain;

import ru.tki.executor.Navigation;
import ru.tki.models.Empire;
import ru.tki.models.actions.Action;
import ru.tki.models.tasks.EmpireTask;
import ru.tki.models.tasks.Task;
import ru.tki.po.LoginPage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Mainframe {

    private static Logger logger = Logger.getLogger(String.valueOf(Mainframe.class));

    private Empire empire;

    Navigation navigation;
    LoginPage  loginPage;

    public Mainframe(Empire empire) {
        this.empire = empire;
        navigation = new Navigation();
        loginPage = new LoginPage();
    }

    public void start() {
        navigation.openHomePage();
        loginPage.login();

        if (!empire.load()) {
            empire.addTask(new EmpireTask(empire));
        }
        runExecution();
    }

    private void runExecution() {
        logger.info("Let's the magic start!");
        do {
            try {
                loginPage.checkLogin();

                think();
                execute();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        while (true);
    }

    private void think() {
        verifyActions();
        thinkBuildings();
    }

    private void verifyActions(){

    }

    private void thinkBuildings(){

    }

    private void execute() throws InterruptedException {
        if (empire.getTasks().isEmpty()) {
            System.out.print('.');
            Thread.sleep(10000);
        }
        List<Task> tasksForRemove = new ArrayList<>();
        for (Task task : empire.getTasks()) {
            logger.info("Execute task: " + task.toString());
            Action action = task.execute();
            if (null != action) {
                empire.addAction(action);
            }
            tasksForRemove.add(task);
        }
        for (Task task : tasksForRemove) {
            empire.removeTask(task);
        }
    }
}
