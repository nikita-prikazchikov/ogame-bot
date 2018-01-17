package ru.tki.models;

import java.util.Date;

public interface ITask {

    TaskType getType();

    Date getExecutionTime();

}
