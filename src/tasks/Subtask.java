package tasks;

import support.TaskStatus;

public class Subtask extends Task{

    public int epicID;

    public Subtask(String name, int numberID, String description, TaskStatus status, int epicID) {
        super(name, numberID, description, status);
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "tasks.Subtask{" +
                "epicID=" + epicID +
                "} " + super.toString();
    }
}
