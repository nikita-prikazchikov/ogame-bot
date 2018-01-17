package ru.tki.executor;

import ru.tki.models.actions.Action;
import ru.tki.models.tasks.Task;

public class TaskExecutor {

    public Action executeTask(Task task){
        return task.execute();
    }
}
