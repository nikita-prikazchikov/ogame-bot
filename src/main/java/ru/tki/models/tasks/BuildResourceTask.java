package ru.tki.models.tasks;

import ru.tki.models.IAction;
import ru.tki.models.ITask;
import ru.tki.models.TaskType;
import ru.tki.models.actions.Action;

public class BuildResourceTask extends Task implements ITask{
    public TaskType getType() {
        return TaskType.BUILD_RESOURCES;
    }
}
