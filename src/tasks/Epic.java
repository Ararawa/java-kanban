package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public ArrayList<Integer> epicSubtasks = new ArrayList<>();

    @Override
    public String toString() {
        return "tasks.Epic{" + super.toString() +
                " epicSubtasks=" + epicSubtasks +
                "}";
    }
}
