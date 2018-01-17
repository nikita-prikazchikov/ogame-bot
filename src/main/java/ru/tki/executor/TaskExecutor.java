package ru.tki.executor;

import ru.tki.models.IAction;
import ru.tki.models.ITask;
import ru.tki.models.actions.BuildResourceAction;

public class TaskExecutor {

    public IAction executeTask(ITask task){
        switch (task.getType()){

            case BUILD_RESOURCES:
                return executeBuildResources();
            default:
                return null;
        }
    }

    public IAction executeBuildResources(){
        return new BuildResourceAction();
    }
}
