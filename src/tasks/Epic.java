package tasks;

import support.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task{
    public Epic(String name, int id, String description, TaskStatus status) {
        super(name, id, description, status);
    }

    public ArrayList<Integer> epicSubtasks = new ArrayList<>();

    @Override
    public String toString() {
        return "tasks.Epic{" + super.toString() +
                " epicSubtasks=" + epicSubtasks +
                "}";
    }
}
