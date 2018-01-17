package ru.tki.models.tasks;

import ru.tki.models.ITask;

import java.util.Date;

public abstract class Task implements ITask {
    private Date executionDate;

    public Date getExecutionTime() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }
}
